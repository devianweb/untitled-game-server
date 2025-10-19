package design.duskwood.gameserver.service.models;

public record PlayerInput(boolean up, boolean down, boolean left, boolean right, int seq) implements MessagePayload {
}
