package design.duskwood.gameserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import design.duskwood.gameserver.service.models.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@RequiredArgsConstructor
public class GameInstance implements Runnable {
  private final String id;
  private volatile boolean running = true;
  private ObjectMapper objectMapper = new ObjectMapper();
  private GameState gameState = new GameState();
  @Getter
  private final Map<WebSocketSession, SessionMessageQueue> sessions = new ConcurrentHashMap<>();
  private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();


  public void handleNewWebsocketConnection(String userId, WebSocketSession session) {
    userSessions.put(userId, session);
    sessions.put(session, new SessionMessageQueue(session));
    gameState.addNewPlayer(userId);
  }

  public void handleConnectionClosed(String userId, WebSocketSession session) {
    sessions.get(session).shutdownQueue();
    sessions.remove(session);
    userSessions.remove(userId);
    gameState.removePlayer(userId);
  }

  public void handleTextMessage(String userId, WebSocketSession session, MessageWrapper message) throws IOException {
    if (message.type().equals(MessageType.INPUT) && message.payload() instanceof PlayerInput inputs) {
      gameState.updatePlayerInputs(userId, inputs.up(), inputs.down(), inputs.left(), inputs.right());
    }
  }

  private void broadcastToAll(MessageWrapper message) throws IOException {
    if (sessions.isEmpty()) return;
    var json = objectMapper.writeValueAsString(message);
    var text = new TextMessage(json);

    for (Map.Entry<WebSocketSession, SessionMessageQueue> entry : sessions.entrySet()) {
      var sesh = entry.getKey();
      if (sesh.isOpen()) {
        entry.getValue().enqueue(text);
      }
    }
  }

  private void broadcastToAllExcept(String userId, MessageWrapper message) throws IOException {
    if (sessions.isEmpty()) return;
    var json = objectMapper.writeValueAsString(message);
    var text = new TextMessage(json);
    var excluded = userSessions.get(userId);

    for (Map.Entry<WebSocketSession, SessionMessageQueue> entry : sessions.entrySet()) {
      var sesh = entry.getKey();
      if (sesh != excluded && sesh.isOpen()) {
        entry.getValue().enqueue(text);
      }
    }
  }

  @SneakyThrows
  @Override
  public void run() {
    var lastUpdate = System.nanoTime();
    var timeStep = 1000000000 / 60;
    var tick = 0;


    while (running) {
      var now = System.nanoTime();
      var dt = now - lastUpdate;

      while (dt >= timeStep) {
        updateGame();
        ifMovingSendPositionUpdates();
        serverAuthoritativeTick(tick);
        gameStateLogging(tick);
        dt -= timeStep;
        lastUpdate += timeStep;
        tick++;
      }
    }
  }

  private void serverAuthoritativeTick(int tick) throws IOException {
      if (tick % 6 == 0) {
        for (String userId : gameState.getPlayers().keySet()) {
          var message = new MessageWrapper(userId, MessageType.AUTHORITATIVE, gameState);
          broadcastToAll(message);
        }
      }
  }

  private void ifMovingSendPositionUpdates() throws IOException {
    for (Map.Entry<String, Player> entry : gameState.getPlayers().entrySet()) {
      var userId = entry.getKey();
      var player = entry.getValue();
      if (player.isMoving()) {
        var message = new MessageWrapper(userId, MessageType.POSITION, new PlayerPosition(player.getX(), player.getY(), player.getVx(), player.getVy()));
        broadcastToAllExcept(entry.getKey(), message);
      }
    }
  }

  private void updateGame() {
    var players = gameState.getPlayers().values();
    for (Player player : players) {
      updatePlayer(player);
    }
  }

  private void updatePlayer(Player player) {
    //velocity
    if (player.isUp() && player.getVy() < 0.1d) {
      player.incrementVy(0.01d);
    }
    if (player.isDown() && player.getVy() > -0.1d) {
      player.decrementVy(0.01d);
    }
    if (player.isLeft() && player.getVx() > -0.1d) {
      player.decrementVx(0.01d);
    }
    if (player.isRight() && player.getVx() < 0.1d) {
      player.incrementVx(0.01d);
    }

    //friction
    if (player.getVx() > 0) {
        player.decrementVx(0.0025d);
        if (player.getVx() < 0.005d) {
          player.setVx(0);
        }
    } else if (player.getVx() < 0) {
      player.incrementVx(0.0025d);
      if (player.getVx() > -0.005d) {
        player.setVx(0);
      }
    }
    if (player.getVy() > 0) {
      player.decrementVy(0.0025d);
      if (player.getVy() < 0.005d) {
        player.setVy(0);
      }
    } else if (player.getVy() < 0) {
      player.incrementVy(0.0025d);
      if (player.getVy() > -0.005d) {
        player.setVy(0);
      }
    }

    player.applyVelocity();
  }

  private void gameStateLogging(int tick) {
    if (tick % 600 == 0) {
      System.out.println("-----");
      System.out.println("Game " + id + " is running...");
      System.out.println(gameState);
    }
  }
}

