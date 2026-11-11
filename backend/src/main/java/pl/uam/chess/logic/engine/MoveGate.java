package pl.uam.chess.logic.engine;

import pl.uam.chess.model.Piece;
import pl.uam.chess.model.Square;

public interface MoveGate {

    boolean allows(Piece piece, Square target);
}
