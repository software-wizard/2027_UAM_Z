package pl.uam.chess.api;

import pl.uam.chess.game.GameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public GameView game() {
        return gameService.view();
    }

    @PostMapping("/click")
    public GameView click(@RequestBody ClickRequest request) {
        return gameService.click(request.row(), request.col());
    }

    @PostMapping("/reset")
    public GameView reset() {
        return gameService.reset();
    }
}
