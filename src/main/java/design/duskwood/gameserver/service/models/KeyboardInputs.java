package design.duskwood.gameserver.service.models;

import lombok.Data;

@Data
public class KeyboardInputs {
  private boolean up;
  private boolean down;
  private boolean left;
  private boolean right;

  public KeyboardInputs() {
    this(false, false, false, false);
  }

  public KeyboardInputs(boolean up, boolean down, boolean left, boolean right) {
    this.up = up;
    this.down = down;
    this.left = left;
    this.right = right;
  }
}
