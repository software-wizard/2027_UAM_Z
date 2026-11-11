package pl.uam.chess.model;

import pl.uam.chess.logic.pieces.ChessPosition;
import pl.uam.chess.logic.move.CompositePattern;
import pl.uam.chess.logic.pieces.LandingRule;
import pl.uam.chess.logic.move.MovementPattern;

public final class Queen extends Piece {

    public static final String NAME = "QUEEN";
    public static final MovementPattern MOVEMENT = new CompositePattern(Bishop.MOVEMENT, Rook.MOVEMENT);

    public Queen(PieceColor color) {
        super(color);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean canReach(ChessPosition position, Square target) {
        return MOVEMENT.hits(position, square(), target)
                && LandingRule.canLand(position, target, color());
    }

    @Override
    public boolean attacks(ChessPosition position, Square target) {
        return MOVEMENT.hits(position, square(), target);
    }
}
