package pl.uam.chess.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public final class Square {

    private static final List<Square> ALL = allSquares();

    private final Column column;
    private final Row row;

    public Square(Column column, Row row) {
        if (column == null) {
            throw new IllegalArgumentException("Brak kolumny pola");
        }
        if (row == null) {
            throw new IllegalArgumentException("Brak rzędu pola");
        }
        this.column = column;
        this.row = row;
    }

    public static Square at(Column column, Row row) {
        return new Square(column, row);
    }

    public static List<Square> all() {
        return ALL;
    }

    public ChessPoint point() {
        return new ChessPoint(column, row);
    }

    public String notation() {
        return column.name() + row.number();
    }

    private static List<Square> allSquares() {
        List<Square> squares = new ArrayList<>(Column.values().length * Row.values().length);
        for (Row row : Row.values()) {
            for (Column column : Column.values()) {
                squares.add(new Square(column, row));
            }
        }
        return List.copyOf(squares);
    }
}
