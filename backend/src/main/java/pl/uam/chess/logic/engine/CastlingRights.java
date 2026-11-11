package pl.uam.chess.logic.engine;

import pl.uam.chess.model.PieceColor;

public final class CastlingRights implements RuleState {

    private boolean whiteKingSide;
    private boolean whiteQueenSide;
    private boolean blackKingSide;
    private boolean blackQueenSide;

    public static CastlingRights all() {
        CastlingRights rights = new CastlingRights();
        rights.enableAll();
        return rights;
    }

    public boolean allowed(PieceColor color, boolean kingSide) {
        if (color == PieceColor.WHITE) {
            return kingSide ? whiteKingSide : whiteQueenSide;
        }
        return kingSide ? blackKingSide : blackQueenSide;
    }

    public void revokeAll(PieceColor color) {
        revokeKingSide(color);
        revokeQueenSide(color);
    }

    public void revokeKingSide(PieceColor color) {
        if (color == PieceColor.WHITE) {
            whiteKingSide = false;
        } else {
            blackKingSide = false;
        }
    }

    public void revokeQueenSide(PieceColor color) {
        if (color == PieceColor.WHITE) {
            whiteQueenSide = false;
        } else {
            blackQueenSide = false;
        }
    }

    private void enableAll() {
        whiteKingSide = true;
        whiteQueenSide = true;
        blackKingSide = true;
        blackQueenSide = true;
    }
}
