package pl.uam.chess.logic.engine;

import pl.uam.chess.model.LegalMoves;
import pl.uam.chess.model.Square;

public final class SelectionManager {

    private Square square;
    private LegalMoves legalMoves = LegalMoves.none();

    public Square square() {
        return square;
    }

    public LegalMoves legalMoves() {
        return legalMoves;
    }

    public void choose(Square square, LegalMoves legalMoves) {
        this.square = square;
        this.legalMoves = legalMoves;
    }

    public void clear() {
        square = null;
        legalMoves = LegalMoves.none();
    }

    public boolean isDestination(Square square) {
        return legalMoves.contains(square);
    }
}
