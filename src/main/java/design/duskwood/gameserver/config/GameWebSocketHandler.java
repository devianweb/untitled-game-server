package design.duskwood.gameserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
public class GameWebSocketHandler extends TextWebSocketHandler {

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) {
    String gameId = extractGameId(session);
    System.out.println(gameId + ": " + message.getPayload());
  }

  private String extractGameId(WebSocketSession session) {
    var path = session.getUri().getPath();
    var parts = path.split("/");
    return parts[3];
  }
}