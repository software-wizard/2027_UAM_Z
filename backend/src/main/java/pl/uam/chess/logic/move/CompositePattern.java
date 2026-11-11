package pl.uam.chess.logic.move;

import java.util.List;
import pl.uam.chess.logic.pieces.Occupancy;
import pl.uam.chess.model.Square;

public final class CompositePattern implements MovementPattern {

    private final List<MovementPattern> parts;

    public CompositePattern(MovementPattern... parts) {
        this.parts = List.of(parts);
    }

    @Override
    public boolean hits(Occupancy position, Square from, Square target) {
        for (MovementPattern part : parts) {
            if (part.hits(position, from, target)) {
                return true;
            }
        }
        return false;
    }
}
