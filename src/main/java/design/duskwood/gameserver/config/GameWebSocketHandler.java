package design.duskwood.gameserver.config;

import design.duskwood.gameserver.config.utils.MessageDecoder;
import design.duskwood.gameserver.service.GameServer;
import design.duskwood.gameserver.service.SessionMessageQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
public class GameWebSocketHandler extends TextWebSocketHandler {

  private final GameServer gameServer;
  private final MessageDecoder messageDecoder;

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    var gameId = extractGameId(session);
    var userId = extractUserId(session);
    gameServer.handleNewWebsocketConnection(gameId, userId, session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    var gameId = extractGameId(session);
    var userId = extractUserId(session);
    gameServer.handleConnectionClosed(gameId, userId, session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    var gameId = extractGameId(session);
    var userId = extractUserId(session);
    var decodedMessage = messageDecoder.decode(message.getPayload());
//    System.out.println(decodedMessage);
    gameServer.handleTextMessage(gameId, userId, session, decodedMessage);
  }

  private String extractGameId(WebSocketSession session) {
    var path = session.getUri().getPath();
    var parts = path.split("/");
    return parts[3];
  }

  private String extractUserId(WebSocketSession session) {
    String queries = session.getUri().getQuery();
    if (queries == null) {
      return null;
    }
    for (String param : queries.split("&")) {
      String[] keyValue = param.split("=", 2);
      if (keyValue.length == 2 && keyValue[0].equals("userId")) {
        return keyValue[1];
      }
    }
    return null;
  }
}