package pl.uam.chess.logic.preparation;

import java.util.Map;
import pl.uam.chess.logic.move.Board;
import pl.uam.chess.model.Bishop;
import pl.uam.chess.model.Column;
import pl.uam.chess.model.King;
import pl.uam.chess.model.Knight;
import pl.uam.chess.model.Pawn;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.Queen;
import pl.uam.chess.model.Rook;
import pl.uam.chess.model.Row;
import pl.uam.chess.model.Square;

public final class StartingSetup implements PositionSetup {

    private static final Map<Column, String> BACK_RANK = Map.of(
            Column.A, Rook.NAME,
            Column.B, Knight.NAME,
            Column.C, Bishop.NAME,
            Column.D, Queen.NAME,
            Column.E, King.NAME,
            Column.F, Bishop.NAME,
            Column.G, Knight.NAME,
            Column.H, Rook.NAME
    );

    @Override
    public void place(Board board) {
        for (Column column : Column.values()) {
            board.put(BACK_RANK.get(column), PieceColor.BLACK, Square.at(column, Row.R8));
            board.put(Pawn.NAME, PieceColor.BLACK, Square.at(column, Row.R7));
            board.put(Pawn.NAME, PieceColor.WHITE, Square.at(column, Row.R2));
            board.put(BACK_RANK.get(column), PieceColor.WHITE, Square.at(column, Row.R1));
        }
    }
}
