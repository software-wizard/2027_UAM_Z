package pl.uam.chess.communication;

import lombok.Value;
import pl.uam.chess.model.Column;
import pl.uam.chess.model.Row;

@Value
public class SquareView {
    Column column;
    Row row;
}
