package pl.uam.chess.logic.pieces;

import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.logic.engine.RuleState;
import pl.uam.chess.model.Square;

public interface ChessPosition extends Occupancy {

    PieceColor turn();

    boolean myMove(PieceColor color);

    <T extends RuleState> T rule(Class<T> type);

    boolean isAttacked(Square square, PieceColor by);

    boolean allows(Piece piece, Square target);
}
