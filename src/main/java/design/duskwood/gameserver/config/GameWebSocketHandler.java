package design.duskwood.gameserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class GameWebSocketHandler extends TextWebSocketHandler {

  private static final Map<String, Set<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    String gameId = extractGameId(session);
    gameSessions.computeIfAbsent(gameId, k -> ConcurrentHashMap.newKeySet()).add(session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    String gameId = extractGameId(session);
    Set<WebSocketSession> sessions = gameSessions.get(gameId);
    if (sessions != null) {
      sessions.remove(session);
      if (sessions.isEmpty()) {
        gameSessions.remove(gameId);
      }
    }
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String gameId = extractGameId(session);
    Set<WebSocketSession> sessions = gameSessions.get(gameId);

    for (WebSocketSession s : sessions) {
      if (!s.equals(session) && s.isOpen()) {
        s.sendMessage(new TextMessage(message.getPayload()));
      }
    }
  }


  private String extractGameId(WebSocketSession session) {
    var path = session.getUri().getPath();
    var parts = path.split("/");
    return parts[3];
  }
}