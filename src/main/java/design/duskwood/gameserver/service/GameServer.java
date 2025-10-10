package design.duskwood.gameserver.service;

import design.duskwood.gameserver.service.exceptions.NoAvailableThreadsException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GameServer {
  private final Map<String, GameInstance> instances = new ConcurrentHashMap<>();
  private final Map<String, Thread> threads = new ConcurrentHashMap<>();
  private final AtomicInteger currentInstances = new AtomicInteger();

  public String startNewInstance() throws NoAvailableThreadsException {
    if (currentInstances.get() >= 4) {
      throw new NoAvailableThreadsException();
    }
    var id = UUID.randomUUID().toString();
    try {
      GameInstance instance = new GameInstance(id);
      instances.put(id, instance);
      var thread = new Thread(instance);
      thread.start();
      threads.put(id, thread);
      currentInstances.incrementAndGet();
    } catch (RejectedExecutionException ex) {
      throw new NoAvailableThreadsException();
    }
    return id;
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

