package pl.uam.chess.game;

import pl.uam.chess.api.GameView;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.PieceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameServiceTest {

    private final GameService service = new GameService();

    @Test
    void selectingAPieceReturnsItsMovesAndAnIllegalClickClearsSelection() {
        GameView selected = service.click(6, 4);
        assertEquals(6, selected.selected().row());
        assertEquals(4, selected.selected().col());
        assertEquals(2, selected.legalMoves().size());
        assertEquals("WHITE", selected.turn());

        GameView cleared = service.click(7, 3);
        assertNull(cleared.selected());
        assertEquals("PAWN", cleared.board()[6][4].type());
        assertEquals("QUEEN", cleared.board()[7][3].type());
        assertEquals("WHITE", cleared.turn());
    }

    @Test
    void clickOnTheSelectedSquareDeselectsIt() {
        service.click(7, 1);
        GameView view = service.click(7, 1);
        assertNull(view.selected());
        assertEquals("KNIGHT", view.board()[7][1].type());
        assertEquals("WHITE", view.turn());
    }

    @Test
    void emptySquareAndOpponentPieceDoNotSelectAnything() {
        assertNull(service.click(4, 4).selected());
        assertNull(service.click(0, 0).selected());
        assertEquals("WHITE", service.view().turn());
    }

    @Test
    void legalClickMovesThePieceAndHandsTheTurnOver() {
        service.click(7, 1);
        GameView view = service.click(5, 2);
        assertNull(view.selected());
        assertNull(view.board()[7][1]);
        assertEquals("KNIGHT", view.board()[5][2].type());
        assertEquals("WHITE", view.board()[5][2].color());
        assertEquals("BLACK", view.turn());

        assertNull(service.click(6, 0).selected());
        assertEquals("PAWN", service.view().board()[6][0].type());
    }

    @Test
    void captureRemovesTheTakenPiece() {
        service.click(6, 4);
        service.click(4, 4);
        service.click(1, 3);
        service.click(3, 3);
        GameView ready = service.click(4, 4);
        assertTrue(ready.legalMoves().stream().anyMatch(move -> move.row() == 3 && move.col() == 3 && move.capture()));

        GameView captured = service.click(3, 3);
        assertEquals("PAWN", captured.board()[3][3].type());
        assertEquals("WHITE", captured.board()[3][3].color());
        assertNull(captured.board()[4][4]);
        assertEquals("BLACK", captured.turn());
    }

    @Test
    void clickThatWouldLeaveTheKingInCheckOnlyClearsSelection() {
        BoardState board = service.position();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board.board[row][col] = null;
            }
        }
        board.board[7][4] = new pl.uam.chess.model.Piece(PieceType.KING, PieceColor.WHITE);
        board.board[6][4] = new pl.uam.chess.model.Piece(PieceType.ROOK, PieceColor.WHITE);
        board.board[0][4] = new pl.uam.chess.model.Piece(PieceType.ROOK, PieceColor.BLACK);
        board.board[0][0] = new pl.uam.chess.model.Piece(PieceType.KING, PieceColor.BLACK);
        board.turn = PieceColor.WHITE;
        board.whiteKingSide = false;
        board.whiteQueenSide = false;
        board.blackKingSide = false;
        board.blackQueenSide = false;

        GameView selected = service.click(6, 4);
        assertTrue(selected.legalMoves().stream().allMatch(move -> move.col() == 4));

        GameView cleared = service.click(6, 0);
        assertNull(cleared.selected());
        assertEquals("ROOK", cleared.board()[6][4].type());
        assertEquals("WHITE", cleared.turn());
    }

    @Test
    void resetRestoresTheOpeningPosition() {
        service.click(6, 4);
        service.click(4, 4);
        GameView view = service.reset();
        assertEquals("PAWN", view.board()[6][4].type());
        assertNull(view.board()[4][4]);
        assertNull(view.selected());
        assertEquals("WHITE", view.turn());
    }
}
