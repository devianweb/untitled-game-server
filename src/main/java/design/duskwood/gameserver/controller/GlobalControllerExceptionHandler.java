package design.duskwood.gameserver.controller;

import design.duskwood.gameserver.service.models.exceptions.TooManyInstancesException;
import design.duskwood.gameserver.service.models.exceptions.UserAlreadyHasInstanceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

  @ExceptionHandler(value = TooManyInstancesException.class)
  public ErrorResponse handleTooManyInstances(TooManyInstancesException ex) {
    return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(value = UserAlreadyHasInstanceException.class)
  public ErrorResponse handleUserAlreadyHasInstance(UserAlreadyHasInstanceException ex) {
    return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(value = Exception.class)
  public ErrorResponse handleGeneric(Exception ex) {
    return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }
}
