package pl.uam.chess.logic.engine;

import pl.uam.chess.logic.move.Board;
import pl.uam.chess.logic.move.LegalMovesCalculator;
import pl.uam.chess.logic.pieces.ChessPosition;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;

public final class MateCalculator {

    private final Board board;
    private final ChessPosition position;
    private final LegalMovesCalculator legalMovesCalculator;

    public MateCalculator(Board board, ChessPosition position, LegalMovesCalculator legalMovesCalculator) {
        this.board = board;
        this.position = position;
        this.legalMovesCalculator = legalMovesCalculator;
    }

    public boolean isMate() {
        PieceColor side = position.turn();
        if (!kingAttacked(side)) {
            return false;
        }
        for (Piece piece : board.pieces(side)) {
            if (!legalMovesCalculator.forPiece(piece).moves().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean kingAttacked(PieceColor side) {
        PieceColor enemy = side.opposite();
        for (Piece piece : board.pieces(side)) {
            if (piece.isRoyal() && position.isAttacked(piece.square(), enemy)) {
                return true;
            }
        }
        return false;
    }
}
