package pl.uam.chess.logic.move;

import org.junit.jupiter.api.Test;
import pl.uam.chess.logic.pieces.PieceFactory;
import pl.uam.chess.model.*;

import static org.assertj.core.api.Assertions.assertThat;

class BoardTest {

    @Test
    void shouldXYZ() {
        Board board = new Board();

        Square kingPlace = new Square(Column.E, Row.R1);
        board.put("KING", PieceColor.WHITE, kingPlace);

        assertThat(board.at(kingPlace).name()).isEqualTo(King.NAME);
    }
}