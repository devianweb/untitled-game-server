package design.duskwood.gameserver.config.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import design.duskwood.gameserver.service.models.MessageWrapper;
import design.duskwood.gameserver.service.models.PlayerInput;
import design.duskwood.gameserver.service.models.PlayerPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageDecoder {

  private final ObjectMapper objectMapper;

  public MessageWrapper decode(String json) throws JsonProcessingException {
    var env = objectMapper.readValue(json, MessageEnvelope.class);
    var node = env.payload();
    var payload = switch (env.type()) {
      case INPUT -> objectMapper.treeToValue(node, PlayerInput.class);
      case POSITION, AUTHORITATIVE -> throw new RuntimeException("should never happen");
    };

    return new MessageWrapper(env.userId(), env.type(), env.seqId(), payload);
  }
}
