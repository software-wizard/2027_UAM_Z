package pl.uam.chess.logic.move;

import pl.uam.chess.logic.pieces.Occupancy;
import pl.uam.chess.model.Square;

public interface MovementPattern {

    boolean hits(Occupancy position, Square from, Square target);
}
