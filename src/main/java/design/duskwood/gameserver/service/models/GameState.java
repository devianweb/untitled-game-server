package design.duskwood.gameserver.service.models;

import lombok.Value;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Value
public class GameState implements MessagePayload {

  Map<String, Player> players= new ConcurrentHashMap<>();

  public void addNewPlayer(String userId) {
    players.put(userId, new Player());
  }

  public void updatePlayerInputs(String userId, boolean up, boolean down, boolean left, boolean right) {
    var player = players.get(userId);
    player.setUp(up);
    player.setDown(down);
    player.setLeft(left);
    player.setRight(right);
  }

  public Player getPlayer(String userId) {
    return players.get(userId);
  }

  public void removePlayer(String userId) {
    players.remove(userId);
  }

}
