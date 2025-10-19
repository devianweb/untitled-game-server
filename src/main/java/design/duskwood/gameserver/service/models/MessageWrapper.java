package design.duskwood.gameserver.service.models;

public record MessageWrapper(String userId, MessageType type, int seqId, MessagePayload payload) {
}
