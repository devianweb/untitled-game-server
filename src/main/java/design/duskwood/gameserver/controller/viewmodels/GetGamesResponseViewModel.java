package design.duskwood.gameserver.controller.viewmodels;

import java.util.Collection;

public record GetGamesResponseViewModel(Collection<GameInstanceViewModel> games) {
}
