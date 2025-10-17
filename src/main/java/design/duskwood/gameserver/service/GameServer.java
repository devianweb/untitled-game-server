package design.duskwood.gameserver.service;

import design.duskwood.gameserver.service.models.MessageWrapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameServer {
  private final Map<String, GameInstance> instances = new ConcurrentHashMap<>();
  private final Map<String, Thread> threads = new ConcurrentHashMap<>();

  public void handleNewWebsocketConnection(String gameId, String userId, WebSocketSession session) {
    var instance = this.getInstance(gameId);
    instance.handleNewWebsocketConnection(userId, session);
  }

  public void handleConnectionClosed(String gameId, String userId, WebSocketSession session) {
    var instance = this.getInstance(gameId);
    instance.handleConnectionClosed(userId, session);
  }

  public void handleTextMessage(String gameId, String userId, WebSocketSession session, MessageWrapper message) throws IOException {
    var instance = this.getInstance(gameId);
    instance.handleTextMessage(userId, session, message);
  }

  public String startNewInstance() {
    var id = UUID.randomUUID().toString();
    return startNewInstance(id);
  }

  public String startNewInstance(String id) {
    GameInstance instance = new GameInstance(id);
    instances.put(id, instance);
    var thread = new Thread(instance);
    thread.start();
    threads.put(id, thread);
    return id;
  }

  public GameInstance getInstance(String id) {
    var instance = instances.get(id);
    if (instance == null) {
      startNewInstance(id);
    }
    return instances.get(id);
  }

  public Collection<GameInstance> getInstances() {
    return instances.values();
  }

  public boolean shutdownInstance(String id) {
    var thread = threads.get(id);
    thread.interrupt();

    var loop = true;
    while (loop) {
      if (!thread.isAlive()) {
        threads.remove(id);
        instances.remove(id);
        loop = false;
      }
    }

    return true;
  }
}

