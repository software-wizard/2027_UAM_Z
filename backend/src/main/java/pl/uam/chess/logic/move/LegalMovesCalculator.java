package pl.uam.chess.logic.move;

import java.util.ArrayList;
import java.util.List;
import pl.uam.chess.logic.pieces.ChessPosition;
import pl.uam.chess.model.LegalMove;
import pl.uam.chess.model.LegalMoves;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.Square;

public final class LegalMovesCalculator {

    private final ChessPosition position;

    public LegalMovesCalculator(ChessPosition position) {
        this.position = position;
    }

    public LegalMoves forPiece(Piece piece) {
        if (piece == null || !position.myMove(piece.color())) {
            return LegalMoves.none();
        }
        List<LegalMove> moves = new ArrayList<>();
        for (Square target : Square.all()) {
            if (piece.canMove(position, target)) {
                moves.add(new LegalMove(target.getColumn(), target.getRow(), piece.takenOn(position, target) != null));
            }
        }
        return new LegalMoves(moves);
    }
}
