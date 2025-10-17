package design.duskwood.gameserver.service.models;

import lombok.Value;

@Value
public class PlayerPosition implements MessagePayload {

  double x;
  double y;
  double vx;
  double vy;
}
