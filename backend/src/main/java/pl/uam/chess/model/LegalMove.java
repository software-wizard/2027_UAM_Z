package pl.uam.chess.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public final class LegalMove {
    private final Column column;
    private final Row row;
    private final boolean capture;

    public LegalMove(Column column, Row row, boolean capture) {
        if (column == null) {
            throw new IllegalArgumentException("Brak kolumny ruchu");
        }
        if (row == null) {
            throw new IllegalArgumentException("Brak rzędu ruchu");
        }
        this.column = column;
        this.row = row;
        this.capture = capture;
    }
}
