package pl.uam.chess.logic.move;

import lombok.Value;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.Square;

@Value
public class MovePlayed {
    Piece piece;
    Square from;
    Square to;
    Piece captured;
    Square capturedAt;

}
