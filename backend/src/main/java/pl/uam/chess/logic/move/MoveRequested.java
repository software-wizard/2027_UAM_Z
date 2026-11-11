package pl.uam.chess.logic.move;

import lombok.Value;
import pl.uam.chess.model.Square;

@Value
public class MoveRequested {
    Square from;
    Square to;

}
