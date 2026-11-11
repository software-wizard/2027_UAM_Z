package pl.uam.chess.logic.engine;

import pl.uam.chess.model.PieceColor;

public final class SideToMovePolicy implements TurnPolicy {

    @Override
    public boolean mayMove(PieceColor piece, PieceColor sideToMove) {
        return piece == sideToMove;
    }
}
