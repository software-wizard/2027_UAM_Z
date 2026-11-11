package pl.uam.chess.logic.move;

import pl.uam.chess.model.ChessPoint;
import pl.uam.chess.model.Column;
import pl.uam.chess.logic.pieces.Occupancy;
import pl.uam.chess.model.Row;
import pl.uam.chess.model.Square;

public final class SlidePattern implements MovementPattern {

    public static final SlidePattern BISHOP = new SlidePattern(new int[][]{
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    });

    public static final SlidePattern ROOK = new SlidePattern(new int[][]{
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    });

    private final int[][] directions;

    private SlidePattern(int[][] directions) {
        this.directions = directions;
    }

    @Override
    public boolean hits(Occupancy position, Square from, Square target) {
        ChessPoint origin = from.point();
        ChessPoint goal = target.point();
        int rowDelta = origin.getRow().stepsTo(goal.getRow());
        int columnDelta = origin.getColumn().stepsTo(goal.getColumn());
        if (rowDelta == 0 && columnDelta == 0) {
            return false;
        }
        int stepRow = Integer.signum(rowDelta);
        int stepColumn = Integer.signum(columnDelta);
        if (!allows(stepRow, stepColumn) || !onLine(rowDelta, columnDelta)) {
            return false;
        }
        Row row = origin.getRow().shift(stepRow);
        Column column = origin.getColumn().shift(stepColumn);
        while (row != goal.getRow() || column != goal.getColumn()) {
            if (position.pieceAt(Square.at(column, row)) != null) {
                return false;
            }
            row = row.shift(stepRow);
            column = column.shift(stepColumn);
        }
        return true;
    }

    private boolean allows(int stepRow, int stepColumn) {
        for (int[] direction : directions) {
            if (direction[0] == stepRow && direction[1] == stepColumn) {
                return true;
            }
        }
        return false;
    }

    private static boolean onLine(int rowDelta, int columnDelta) {
        return rowDelta == 0 || columnDelta == 0 || Math.abs(rowDelta) == Math.abs(columnDelta);
    }
}
