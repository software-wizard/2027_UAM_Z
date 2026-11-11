package pl.uam.chess.logic.move;

import pl.uam.chess.model.Column;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.Row;
import pl.uam.chess.model.Square;

public final class CastleSquares {

    private CastleSquares() {
    }

    public static Square kingHome(PieceColor color) {
        return color == PieceColor.WHITE ? Square.at(Column.E, Row.R1) : Square.at(Column.E, Row.R8);
    }

    public static Square rookHome(PieceColor color, boolean kingSide) {
        Row row = color == PieceColor.WHITE ? Row.R1 : Row.R8;
        return Square.at(kingSide ? Column.H : Column.A, row);
    }
}
