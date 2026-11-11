package pl.uam.chess.model;

import pl.uam.chess.logic.move.CastleSquares;
import pl.uam.chess.logic.pieces.ChessPosition;
import pl.uam.chess.logic.pieces.LandingRule;
import pl.uam.chess.logic.move.SlidePattern;

public final class Rook extends Piece implements CastlingRightsChange {

    public static final String NAME = "ROOK";
    public static final SlidePattern MOVEMENT = SlidePattern.ROOK;

    public Rook(PieceColor color) {
        super(color);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean isCastleRook() {
        return true;
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

    @Override
    public CastleLoss lostAfterMove(Square from) {
        if (from.equals(CastleSquares.rookHome(color(), true))) {
            return CastleLoss.KING_SIDE;
        }
        if (from.equals(CastleSquares.rookHome(color(), false))) {
            return CastleLoss.QUEEN_SIDE;
        }
        return CastleLoss.NONE;
    }
}
