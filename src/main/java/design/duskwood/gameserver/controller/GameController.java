package design.duskwood.gameserver.controller;

import design.duskwood.gameserver.controller.viewmodels.CreateGameInstanceRequestModel;
import design.duskwood.gameserver.controller.viewmodels.CreateGameInstanceViewModel;
import design.duskwood.gameserver.controller.viewmodels.GameInstanceViewModel;
import design.duskwood.gameserver.controller.viewmodels.GetGamesResponseViewModel;
import design.duskwood.gameserver.service.GameServer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "https://devianweb.github.io"})
@RequiredArgsConstructor
public class GameController {

  private final GameServer gameServer;

  @RequestMapping(value = "/1/games", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GetGamesResponseViewModel> getGameInstances() {
    System.out.println("enter getGameInstances()::");

    var games = gameServer.getInstances();
    var gameInstanceViewModels = games.stream()
      .map(game -> new GameInstanceViewModel(game.getName(), game.getId(), game.getUserSessions().size()))
      .toList();
    var viewModel = new GetGamesResponseViewModel(gameInstanceViewModels);
    var response = ResponseEntity.ok().body(viewModel);

    System.out.println("exit getGameInstances():: response=" + response);
    return response;
  }

  @RequestMapping(value = "/1/games/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CreateGameInstanceViewModel> createGameInstance(@RequestBody CreateGameInstanceRequestModel req) {
    System.out.println("enter createGameInstance()::");

    var id = gameServer.startNewInstance(req.userId(), req.name());
    var viewModel = new CreateGameInstanceViewModel(id);
    var response = ResponseEntity.ok().body(viewModel);

    System.out.println("exit createGameInstance():: response=" + response);
    return response;
  }

  @RequestMapping(value = "/1/games/{id}/shutdown", method = RequestMethod.POST)
  public ResponseEntity<Void> shutdownGameInstance(@PathVariable String id) {
    System.out.println("enter shutdownGameInstance()::");

    var result = gameServer.shutdownInstance(id);
    ResponseEntity<Void> response = result ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();

    System.out.println("exit shutdownGameInstance():: response=" + response);
    return response;
  }
}
