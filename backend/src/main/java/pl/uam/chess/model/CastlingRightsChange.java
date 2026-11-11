package pl.uam.chess.model;

public interface CastlingRightsChange {

    CastleLoss lostAfterMove(Square from);

    default CastleLoss lostAfterCapture(Square where) {
        return lostAfterMove(where);
    }
}
