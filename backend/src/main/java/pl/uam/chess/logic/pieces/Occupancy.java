package pl.uam.chess.logic.pieces;

import pl.uam.chess.model.Piece;
import pl.uam.chess.model.Square;

public interface Occupancy {

    Piece pieceAt(Square square);
}
