package pl.uam.chess.logic.engine;

import pl.uam.chess.logic.move.Board;
import pl.uam.chess.logic.move.MoveMaker;
import pl.uam.chess.logic.move.MovePlayed;
import pl.uam.chess.logic.pieces.ChessPosition;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.Square;

final class KingSafetyGate implements MoveGate {

    private final Board board;
    private final ChessPosition position;
    private final MoveMaker moveMaker;

    KingSafetyGate(Board board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.moveMaker = new MoveMaker(
                board::at,
                board::move,
                board::remove,
                board::put,
                board::replace,
                board::create,
                position);
    }

    @Override
    public boolean allows(Piece mover, Square target) {
        MovePlayed played = moveMaker.play(mover.square(), target);
        try {
            return !attackedKing(mover.color());
        } finally {
            moveMaker.undo(played);
        }
    }

    private boolean attackedKing(PieceColor color) {
        PieceColor enemy = color.opposite();
        for (Piece piece : board.pieces(color)) {
            if (piece.isRoyal() && position.isAttacked(piece.square(), enemy)) {
                return true;
            }
        }
        return false;
    }
}
