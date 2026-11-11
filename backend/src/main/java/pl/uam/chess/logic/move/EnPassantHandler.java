package pl.uam.chess.logic.move;

import pl.uam.chess.logic.engine.EnPassant;
import pl.uam.chess.model.PassingPawn;
import pl.uam.chess.model.Square;

public final class EnPassantHandler {

    private final EnPassant enPassant;

    public EnPassantHandler(EnPassant enPassant) {
        this.enPassant = enPassant;
    }

    public void onPlayed(MovePlayed event) {
        if (enPassant == null) {
            return;
        }
        Square target = null;
        if (event.getPiece() instanceof PassingPawn pawn) {
            target = pawn.enPassantTargetAfter(event.getFrom(), event.getTo());
        }
        enPassant.set(target);
    }
}
