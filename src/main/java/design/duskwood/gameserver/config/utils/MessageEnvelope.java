package design.duskwood.gameserver.config.utils;

import com.fasterxml.jackson.databind.JsonNode;
import design.duskwood.gameserver.service.models.MessageType;

public record MessageEnvelope(String userId, MessageType type, int seqId, JsonNode payload) {
}
