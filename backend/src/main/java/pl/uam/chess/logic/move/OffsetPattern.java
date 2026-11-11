package pl.uam.chess.logic.move;

import pl.uam.chess.model.ChessPoint;
import pl.uam.chess.logic.pieces.Occupancy;
import pl.uam.chess.model.Square;

public final class OffsetPattern implements MovementPattern {

    public static final OffsetPattern KNIGHT = new OffsetPattern(new int[][]{
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    });

    public static final OffsetPattern KING = new OffsetPattern(new int[][]{
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    });

    private final int[][] offsets;

    private OffsetPattern(int[][] offsets) {
        this.offsets = offsets;
    }

    @Override
    public boolean hits(Occupancy position, Square from, Square target) {
        ChessPoint origin = from.point();
        ChessPoint goal = target.point();
        int rowDelta = origin.getRow().stepsTo(goal.getRow());
        int columnDelta = origin.getColumn().stepsTo(goal.getColumn());
        for (int[] offset : offsets) {
            if (offset[0] == rowDelta && offset[1] == columnDelta) {
                return true;
            }
        }
        return false;
    }
}
