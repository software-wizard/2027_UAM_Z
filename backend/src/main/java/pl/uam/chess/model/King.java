package pl.uam.chess.model;

import pl.uam.chess.logic.move.CastlingCalculator;
import pl.uam.chess.logic.pieces.ChessPosition;
import pl.uam.chess.logic.pieces.LandingRule;
import pl.uam.chess.logic.move.OffsetPattern;

public final class King extends Piece implements CastlingRightsChange {

    public static final String NAME = "KING";

    public King(PieceColor color) {
        super(color);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean isRoyal() {
        return true;
    }

    @Override
    public boolean canBeCaptured() {
        return false;
    }

    @Override
    public boolean canReach(ChessPosition position, Square target) {
        if (OffsetPattern.KING.hits(position, square(), target)) {
            return LandingRule.canLand(position, target, color());
        }
        return CastlingCalculator.canReach(position, this, target);
    }

    @Override
    public boolean attacks(ChessPosition position, Square target) {
        return OffsetPattern.KING.hits(position, square(), target);
    }

    @Override
    public CastleLoss lostAfterMove(Square from) {
        return CastleLoss.BOTH;
    }
}
