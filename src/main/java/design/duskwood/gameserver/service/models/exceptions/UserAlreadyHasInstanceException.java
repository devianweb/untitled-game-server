package design.duskwood.gameserver.service.models.exceptions;

import lombok.RequiredArgsConstructor;

public class UserAlreadyHasInstanceException extends RuntimeException {

  public UserAlreadyHasInstanceException(String message) {
    super(message);
  }
}
