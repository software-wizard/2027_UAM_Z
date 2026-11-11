package pl.uam.chess.logic.move;

import pl.uam.chess.logic.engine.CastlingRights;
import pl.uam.chess.model.CastleLoss;
import pl.uam.chess.model.CastlingRightsChange;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.Square;

public final class CastlingHandler {

    private final CastlingRights rights;

    public CastlingHandler(CastlingRights rights) {
        this.rights = rights;
    }

    public void onPlayed(MovePlayed event) {
        apply(event.getPiece(), event.getFrom(), true);
        apply(event.getCaptured(), event.getCapturedAt(), false);
    }

    private void apply(Piece piece, Square square, boolean moved) {
        if (!(piece instanceof CastlingRightsChange change) || square == null || rights == null) {
            return;
        }
        CastleLoss loss = moved ? change.lostAfterMove(square) : change.lostAfterCapture(square);
        switch (loss) {
            case KING_SIDE -> rights.revokeKingSide(piece.color());
            case QUEEN_SIDE -> rights.revokeQueenSide(piece.color());
            case BOTH -> rights.revokeAll(piece.color());
            case NONE -> {
            }
        }
    }
}
