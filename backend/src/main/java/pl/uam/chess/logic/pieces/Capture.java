package pl.uam.chess.logic.pieces;

import lombok.Value;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.Square;

@Value
public class Capture {
    Piece piece;
    Square square;

}
