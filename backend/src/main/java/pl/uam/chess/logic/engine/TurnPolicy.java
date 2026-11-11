package pl.uam.chess.logic.engine;

import pl.uam.chess.model.PieceColor;

public interface TurnPolicy {

    boolean mayMove(PieceColor piece, PieceColor sideToMove);
}
