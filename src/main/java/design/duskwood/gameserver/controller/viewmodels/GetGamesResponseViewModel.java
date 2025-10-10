package design.duskwood.gameserver.controller.viewmodels;

import design.duskwood.gameserver.service.GameInstance;

import java.util.Collection;

public record GetGamesResponseViewModel(Collection<GameInstance> games) {
}
