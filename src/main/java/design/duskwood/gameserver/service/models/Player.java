package design.duskwood.gameserver.service.models;

import lombok.Data;
import lombok.SneakyThrows;
import org.joml.Vector3d;
import org.joml.Vector3f;


@Data
public class Player {

  private final double maxV = 0.1d;
  private final double acceleration = maxV * 0.1d;
  private final double deceleration = maxV * 0.025d;

  private Inputs inputs;
  private Vector3d position;
  private Vector3d velocity;

  public Player() {
    this(new Vector3d(0d, 0d, 0d), new Vector3d(0d, 0d, 0d), new Inputs());
  }

  public Player(Vector3d position, Vector3d velocity, Inputs inputs) {
    this.position = position;
    this.velocity = velocity;
    this.inputs = inputs;
  }

  public boolean isMoving() {
    return velocity.lengthSquared() > 0;
  }

  @SneakyThrows
  public void updatePlayerPositionAndVelocity() {

    var tempVelocity = new Vector3d(velocity);

    //inputs
    var direction = new Vector3d(
      (inputs.getKeyboardInputs().isRight() ? 1 : 0) - (inputs.getKeyboardInputs().isLeft() ? 1 : 0),
      (inputs.getKeyboardInputs().isUp() ? 1 : 0) - (inputs.getKeyboardInputs().isDown() ? 1 : 0),
      0
      );

    //acceleration, but normalised!
    if (direction.lengthSquared() > 0) {
      direction.normalize().mul(this.acceleration);
      tempVelocity.add(direction);
    }

    //friction 'n dead zone 'tings
    if (direction.lengthSquared() == 0 && tempVelocity.lengthSquared() > 0) {
      var speed = tempVelocity.length();
      var newSpeed = speed - this.deceleration;

      if (newSpeed <= 0.005) {
        tempVelocity.set(0, 0, 0);
      } else {
        tempVelocity.mul(newSpeed / speed);
      }
    }

    //clampy mc clampface
    if (tempVelocity.length() > this.maxV) {
      tempVelocity.mul(this.maxV / tempVelocity.length());
    }

    position.add(tempVelocity);
    velocity.set(tempVelocity);
  }

}
