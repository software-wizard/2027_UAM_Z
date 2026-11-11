package pl.uam.chess.logic.pieces;

import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.Square;

public final class LandingRule {

    private LandingRule() {
    }

    public static boolean canLand(ChessPosition position, Square target, PieceColor color) {
        Piece occupant = position.pieceAt(target);
        if (occupant == null) {
            return true;
        }
        return occupant.color() != color && occupant.canBeCaptured();
    }
}
