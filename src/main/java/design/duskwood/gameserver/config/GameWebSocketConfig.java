package design.duskwood.gameserver.config;

import design.duskwood.gameserver.config.utils.MessageDecoder;
import design.duskwood.gameserver.service.GameServer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class GameWebSocketConfig implements WebSocketConfigurer {

  private final GameServer gameServer;
  private final MessageDecoder messageDecoder;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
      .addHandler(new GameWebSocketHandler(gameServer, messageDecoder), "/ws/games/{gameId}")
      .setAllowedOrigins("*");
  }
}
