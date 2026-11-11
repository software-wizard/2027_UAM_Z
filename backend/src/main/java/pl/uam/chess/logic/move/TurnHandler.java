package pl.uam.chess.logic.move;

import java.util.function.Consumer;
import pl.uam.chess.model.PieceColor;

public final class TurnHandler {

    private final Consumer<PieceColor> turn;

    public TurnHandler(Consumer<PieceColor> turn) {
        this.turn = turn;
    }

    public void onPlayed(MovePlayed event) {
        turn.accept(event.getPiece().color().opposite());
    }
}
