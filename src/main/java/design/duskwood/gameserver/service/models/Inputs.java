package design.duskwood.gameserver.service.models;

import lombok.Data;

@Data
public class Inputs {
  private final KeyboardInputs keyboardInputs;

  public Inputs() {
    this(new KeyboardInputs());
  }

  public Inputs(KeyboardInputs keyboardInputs) {
    this.keyboardInputs = keyboardInputs;
  }
}
