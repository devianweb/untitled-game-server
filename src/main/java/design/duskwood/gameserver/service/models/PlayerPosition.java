package design.duskwood.gameserver.service.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class PlayerPosition implements MessagePayload {

  double x;
  double y;
  double vx;
  double vy;
  int lastSeq;

  @JsonCreator
  public PlayerPosition(
      @JsonProperty("x") double x,
      @JsonProperty("y") double y,
      @JsonProperty("vx") double vx,
      @JsonProperty("vy") double vy,
      @JsonProperty("lastSeq") int lastSeq) {
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    this.lastSeq = lastSeq;
  }
}
