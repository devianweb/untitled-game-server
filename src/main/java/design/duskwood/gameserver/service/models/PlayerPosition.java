package design.duskwood.gameserver.service.models;

import lombok.Value;
import org.joml.Vector3d;

@Value
public class PlayerPosition implements MessagePayload {

  Vector3d position;
  Vector3d velocity;
}
