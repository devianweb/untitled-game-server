package design.duskwood.gameserver.service.models.exceptions;

import lombok.RequiredArgsConstructor;

public class TooManyInstancesException extends RuntimeException {
  public TooManyInstancesException(String message) {
    super(message);
  }
}
