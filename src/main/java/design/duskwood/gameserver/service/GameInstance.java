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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.LockSupport;

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
  private final Map<String, Integer> userSeqIds = new ConcurrentHashMap<>();
  private final List<MessageWrapper> messages = new CopyOnWriteArrayList<>();

  private final double maxV = 0.1d;
  private final double acceleration = maxV * 0.1d;
  private final double deceleration = maxV * 0.025d;

  public void handleNewWebsocketConnection(String userId, WebSocketSession session) {
    userSessions.put(userId, session);
    sessions.put(session, new SessionMessageQueue(session));
    userSeqIds.put(userId, 0);
    gameState.addNewPlayer(userId);
  }

  public void handleConnectionClosed(String userId, WebSocketSession session) {
    sessions.get(session).shutdownQueue();
    sessions.remove(session);
    userSessions.remove(userId);
    userSeqIds.remove(userId);
    gameState.removePlayer(userId);
  }

  public void handleTextMessage(String userId, WebSocketSession session, MessageWrapper message) throws IOException {
    messages.add(message);
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
        messages.forEach(message -> {
          if (message.type().equals(MessageType.INPUT) && message.payload() instanceof PlayerInput inputs) {
            if (gameState.getPlayer(message.userId()) != null) {
              gameState.updatePlayerInputs(message.userId(), inputs.up(), inputs.down(), inputs.left(), inputs.right());
              userSeqIds.put(message.userId(), message.seqId());
            }
          }
        });

        if (!messages.isEmpty()) {
          messages.clear();
        }

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
          var message = new MessageWrapper(userId, MessageType.AUTHORITATIVE, userSeqIds.get(userId), gameState);
          broadcastToAll(message);
        }
      }
  }

  private void ifMovingSendPositionUpdates() throws IOException {
    for (Map.Entry<String, Player> entry : gameState.getPlayers().entrySet()) {
      var userId = entry.getKey();
      var player = entry.getValue();
      var seqId = userSeqIds.get(userId);
      if (player.isMoving()) {
        var message = new MessageWrapper(userId, MessageType.POSITION, seqId, new PlayerPosition(player.getX(), player.getY(), player.getVx(), player.getVy()));
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
    if (player.isUp() && player.getVy() < maxV) {
      player.incrementVy(acceleration);
    }
    if (player.isDown() && player.getVy() > -maxV) {
      player.decrementVy(acceleration);
    }
    if (player.isLeft() && player.getVx() > -maxV) {
      player.decrementVx(acceleration);
    }
    if (player.isRight() && player.getVx() < maxV) {
      player.incrementVx(acceleration);
    }

    //friction
    if (player.getVx() > 0) {
        player.decrementVx(deceleration);
        if (player.getVx() < 0.005d) {
          player.setVx(0);
        }
    } else if (player.getVx() < 0) {
      player.incrementVx(deceleration);
      if (player.getVx() > -0.005d) {
        player.setVx(0);
      }
    }
    if (player.getVy() > 0) {
      player.decrementVy(deceleration);
      if (player.getVy() < 0.005d) {
        player.setVy(0);
      }
    } else if (player.getVy() < 0) {
      player.incrementVy(deceleration);
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

