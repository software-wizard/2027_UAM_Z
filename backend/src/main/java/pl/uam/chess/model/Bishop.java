package pl.uam.chess.model;

import pl.uam.chess.logic.pieces.ChessPosition;
import pl.uam.chess.logic.pieces.LandingRule;
import pl.uam.chess.logic.move.SlidePattern;

public final class Bishop extends Piece {

    public static final String NAME = "BISHOP";
    public static final SlidePattern MOVEMENT = SlidePattern.BISHOP;

    public Bishop(PieceColor color) {
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
