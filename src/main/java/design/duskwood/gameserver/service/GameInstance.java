package design.duskwood.gameserver.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GameInstance implements Runnable {
  private final String id;
  private boolean running = true;

  @Override
  public void run() {
    while (running) {
      System.out.println("Game " + id + " running...");
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }
}

