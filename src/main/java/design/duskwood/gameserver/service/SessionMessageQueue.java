package design.duskwood.gameserver.service;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SessionMessageQueue {
  private final BlockingQueue<TextMessage> queue = new LinkedBlockingQueue<>();
  private final WebSocketSession session;
  private final Thread worker;

  public SessionMessageQueue(WebSocketSession session) {
    this.session = session;
    this.worker = new Thread(this::processQueue);
    this.worker.start();
  }

  public void enqueue(TextMessage message) {
    queue.offer(message);
  }

  public void shutdownQueue() {
    worker.interrupt();
  }

  private void processQueue() {
    try {
      while (!worker.isInterrupted() && session.isOpen()) {
        TextMessage message = queue.take();
        session.sendMessage(message);
      }
    } catch (InterruptedException ex) {
      System.out.println("Thread interrupted...");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
