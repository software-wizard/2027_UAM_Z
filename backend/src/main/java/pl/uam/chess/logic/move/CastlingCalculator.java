package pl.uam.chess.logic.move;

import pl.uam.chess.logic.engine.CastlingRights;
import pl.uam.chess.logic.pieces.ChessPosition;
import pl.uam.chess.model.ChessPoint;
import pl.uam.chess.model.Column;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.Square;

public final class CastlingCalculator {

    private CastlingCalculator() {
    }

    public static boolean canReach(ChessPosition position, Piece king, Square target) {
        Square from = king.square();
        if (!from.equals(CastleSquares.kingHome(king.color()))) {
            return false;
        }
        ChessPoint origin = from.point();
        ChessPoint goal = target.point();
        int distance = origin.getColumn().stepsTo(goal.getColumn());
        if (goal.getRow() != origin.getRow() || Math.abs(distance) != 2) {
            return false;
        }
        boolean kingSide = distance > 0;
        CastlingRights rights = position.rule(CastlingRights.class);
        if (rights == null || !rights.allowed(king.color(), kingSide)) {
            return false;
        }
        Square rookHome = CastleSquares.rookHome(king.color(), kingSide);
        Piece rook = position.pieceAt(rookHome);
        if (rook == null || !rook.isCastleRook() || rook.color() != king.color()) {
            return false;
        }
        int step = kingSide ? 1 : -1;
        Column rookColumn = rookHome.point().getColumn();
        for (Column column = origin.getColumn().shift(step); column != rookColumn; column = column.shift(step)) {
            if (position.pieceAt(Square.at(column, origin.getRow())) != null) {
                return false;
            }
        }
        PieceColor enemy = king.color().opposite();
        if (position.isAttacked(from, enemy)) {
            return false;
        }
        Square passed = Square.at(origin.getColumn().shift(step), origin.getRow());
        return !position.isAttacked(passed, enemy) && !position.isAttacked(target, enemy);
    }

    public static boolean isCastling(Piece king, Square from, Square to) {
        if (!king.isRoyal()) {
            return false;
        }
        ChessPoint origin = from.point();
        ChessPoint goal = to.point();
        return from.equals(CastleSquares.kingHome(king.color()))
                && goal.getRow() == origin.getRow()
                && Math.abs(origin.getColumn().stepsTo(goal.getColumn())) == 2;
    }

    public static Square rookFrom(Square kingFrom, Square kingTo) {
        ChessPoint origin = kingFrom.point();
        boolean kingSide = origin.getColumn().stepsTo(kingTo.point().getColumn()) > 0;
        return Square.at(kingSide ? Column.H : Column.A, origin.getRow());
    }

    public static Square rookTo(Square kingFrom, Square kingTo) {
        ChessPoint origin = kingFrom.point();
        boolean kingSide = origin.getColumn().stepsTo(kingTo.point().getColumn()) > 0;
        return Square.at(origin.getColumn().shift(kingSide ? 1 : -1), origin.getRow());
    }
}
