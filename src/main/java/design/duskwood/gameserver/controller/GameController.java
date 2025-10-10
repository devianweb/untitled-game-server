package design.duskwood.gameserver.controller;

import design.duskwood.gameserver.controller.viewmodels.GetGamesResponseViewModel;
import design.duskwood.gameserver.service.GameServer;
import design.duskwood.gameserver.service.exceptions.NoAvailableThreadsException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameController {

  private final GameServer gameServer;

  @RequestMapping(value = "/1/games", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GetGamesResponseViewModel> getGameInstances() {
    System.out.println("enter getGameInstances()::");

    var games = gameServer.getInstances();
    var viewModel = new GetGamesResponseViewModel(games);
    var response = ResponseEntity.ok().body(viewModel);

    System.out.println("exit getGameInstances():: response=" + response);
    return response;
  }

  @RequestMapping(value = "/1/games/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> createGameInstance() {
    System.out.println("enter createGameInstance()::");

    ResponseEntity<String> response;
    try {
      var id = gameServer.startNewInstance();
      response = ResponseEntity.ok().body(id);
    } catch (NoAvailableThreadsException e) {
      response = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

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
