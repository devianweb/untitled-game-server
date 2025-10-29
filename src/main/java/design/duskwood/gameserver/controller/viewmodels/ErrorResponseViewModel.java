package design.duskwood.gameserver.controller.viewmodels;

import java.time.Instant;

public record ErrorResponseViewModel(String code, String message, Instant timestamp) {
}
