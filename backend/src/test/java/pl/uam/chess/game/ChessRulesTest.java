package pl.uam.chess.game;

import pl.uam.chess.model.MoveTarget;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.PieceType;
import pl.uam.chess.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChessRulesTest {

    @Test
    void initialBoardHasStandardPieces() {
        BoardState state = new BoardState();
        state.reset();

        assertEquals(piece(PieceType.ROOK, PieceColor.BLACK), state.board[0][0]);
        assertEquals(piece(PieceType.QUEEN, PieceColor.BLACK), state.board[0][3]);
        assertEquals(piece(PieceType.KING, PieceColor.BLACK), state.board[0][4]);
        assertEquals(piece(PieceType.PAWN, PieceColor.BLACK), state.board[1][7]);
        assertEquals(piece(PieceType.PAWN, PieceColor.WHITE), state.board[6][0]);
        assertEquals(piece(PieceType.QUEEN, PieceColor.WHITE), state.board[7][3]);
        assertEquals(piece(PieceType.KING, PieceColor.WHITE), state.board[7][4]);
        assertNull(state.board[4][4]);
        assertEquals(PieceColor.WHITE, state.turn);
    }

    @Test
    void whitePawnMovesOneOrTwoSquaresAndBlackPawnAdvancesDown() {
        BoardState state = new BoardState();
        state.reset();

        assertTrue(has(ChessRules.legalTargets(state, 6, 4), 5, 4));
        assertTrue(has(ChessRules.legalTargets(state, 6, 4), 4, 4));
        assertFalse(has(ChessRules.legalTargets(state, 6, 4), 3, 4));

        ChessRules.apply(state, 6, 4, 4, 4);
        assertEquals(new Position(5, 4), state.enPassant);
        assertEquals(PieceColor.BLACK, state.turn);

        assertTrue(has(ChessRules.legalTargets(state, 1, 4), 2, 4));
        assertTrue(has(ChessRules.legalTargets(state, 1, 4), 3, 4));
        ChessRules.apply(state, 1, 0, 2, 0);
        assertNull(state.enPassant);
    }

    @Test
    void pawnIsBlockedByAPieceInFront() {
        BoardState state = emptyBoard();
        put(state, 6, 4, PieceType.PAWN, PieceColor.WHITE);
        put(state, 4, 4, PieceType.KNIGHT, PieceColor.BLACK);

        List<MoveTarget> moves = ChessRules.legalTargets(state, 6, 4);
        assertTrue(has(moves, 5, 4));
        assertFalse(has(moves, 4, 4));

        put(state, 5, 4, PieceType.PAWN, PieceColor.BLACK);
        assertTrue(ChessRules.legalTargets(state, 6, 4).isEmpty());
    }

    @Test
    void pawnCapturesDiagonally() {
        BoardState state = emptyBoard();
        put(state, 4, 4, PieceType.PAWN, PieceColor.WHITE);
        put(state, 3, 3, PieceType.PAWN, PieceColor.BLACK);
        put(state, 3, 5, PieceType.KNIGHT, PieceColor.BLACK);

        List<MoveTarget> moves = ChessRules.legalTargets(state, 4, 4);
        assertTrue(has(moves, 3, 4));
        assertTrue(capture(moves, 3, 3));
        assertTrue(capture(moves, 3, 5));
        assertFalse(has(moves, 3, 2));
    }

    @Test
    void knightJumpsAndRookStopsAtFirstPiece() {
        BoardState state = new BoardState();
        state.reset();
        List<MoveTarget> knightMoves = ChessRules.legalTargets(state, 7, 1);
        assertTrue(has(knightMoves, 5, 0));
        assertTrue(has(knightMoves, 5, 2));
        assertEquals(2, knightMoves.size());
        assertTrue(ChessRules.legalTargets(state, 7, 0).isEmpty());

        BoardState open = emptyBoard();
        put(open, 4, 0, PieceType.ROOK, PieceColor.WHITE);
        put(open, 2, 0, PieceType.KNIGHT, PieceColor.BLACK);
        put(open, 1, 0, PieceType.PAWN, PieceColor.BLACK);
        List<MoveTarget> rookMoves = ChessRules.legalTargets(open, 4, 0);
        assertTrue(has(rookMoves, 3, 0));
        assertTrue(capture(rookMoves, 2, 0));
        assertFalse(has(rookMoves, 1, 0));
        assertFalse(has(rookMoves, 0, 0));
    }

    @Test
    void bishopDoesNotJumpOverPieces() {
        BoardState state = emptyBoard();
        put(state, 4, 4, PieceType.BISHOP, PieceColor.WHITE);
        put(state, 3, 3, PieceType.PAWN, PieceColor.BLACK);
        put(state, 2, 2, PieceType.PAWN, PieceColor.BLACK);

        List<MoveTarget> moves = ChessRules.legalTargets(state, 4, 4);
        assertTrue(capture(moves, 3, 3));
        assertFalse(has(moves, 2, 2));
        assertTrue(has(moves, 5, 5));
        assertTrue(has(moves, 7, 7));
    }

    @Test
    void whitePawnAttacksOnlyForwardDiagonals() {
        BoardState state = emptyBoard();
        put(state, 4, 4, PieceType.PAWN, PieceColor.WHITE);

        assertTrue(ChessRules.isAttacked(state.board, 3, 3, PieceColor.WHITE));
        assertTrue(ChessRules.isAttacked(state.board, 3, 5, PieceColor.WHITE));
        assertFalse(ChessRules.isAttacked(state.board, 3, 4, PieceColor.WHITE));
        assertFalse(ChessRules.isAttacked(state.board, 5, 3, PieceColor.WHITE));
    }

    @Test
    void cannotLeaveTheKingInCheckOrMoveNextToTheOtherKing() {
        BoardState state = emptyBoard();
        state.board[7][6] = null;
        state.board[0][1] = null;
        put(state, 7, 4, PieceType.KING, PieceColor.WHITE);
        put(state, 7, 7, PieceType.ROOK, PieceColor.BLACK);
        put(state, 0, 0, PieceType.KING, PieceColor.BLACK);

        List<MoveTarget> escapes = ChessRules.legalTargets(state, 7, 4);
        assertTrue(has(escapes, 6, 4));
        assertFalse(has(escapes, 7, 5));
        assertFalse(has(escapes, 7, 7));

        BoardState adjacent = emptyBoard();
        adjacent.board[7][6] = null;
        adjacent.board[0][1] = null;
        put(adjacent, 7, 4, PieceType.KING, PieceColor.WHITE);
        put(adjacent, 5, 4, PieceType.KING, PieceColor.BLACK);
        List<MoveTarget> kingMoves = ChessRules.legalTargets(adjacent, 7, 4);
        assertTrue(has(kingMoves, 7, 3));
        assertTrue(has(kingMoves, 7, 5));
        assertFalse(has(kingMoves, 6, 4));
        assertFalse(has(kingMoves, 6, 3));
        assertFalse(has(kingMoves, 6, 5));
    }

    @Test
    void pinnedRookMayOnlyMoveAlongThePin() {
        BoardState state = emptyBoard();
        state.board[7][6] = null;
        put(state, 7, 4, PieceType.KING, PieceColor.WHITE);
        put(state, 6, 4, PieceType.ROOK, PieceColor.WHITE);
        put(state, 0, 4, PieceType.ROOK, PieceColor.BLACK);

        List<MoveTarget> moves = ChessRules.legalTargets(state, 6, 4);
        assertTrue(has(moves, 5, 4));
        assertTrue(capture(moves, 0, 4));
        assertFalse(has(moves, 6, 3));
        assertFalse(has(moves, 6, 5));
        assertFalse(has(moves, 7, 4));
    }

    @Test
    void enPassantCapturesThePawnThatJustPassed() {
        BoardState state = emptyBoard();
        put(state, 3, 4, PieceType.PAWN, PieceColor.WHITE);
        put(state, 3, 5, PieceType.PAWN, PieceColor.BLACK);
        state.enPassant = new Position(2, 5);

        List<MoveTarget> moves = ChessRules.legalTargets(state, 3, 4);
        assertTrue(capture(moves, 2, 5));

        ChessRules.apply(state, 3, 4, 2, 5);
        assertEquals(piece(PieceType.PAWN, PieceColor.WHITE), state.board[2][5]);
        assertNull(state.board[3][4]);
        assertNull(state.board[3][5]);
        assertNull(state.enPassant);
    }

    @Test
    void pawnPromotesToQueen() {
        BoardState state = emptyBoard();
        put(state, 1, 7, PieceType.PAWN, PieceColor.WHITE);
        ChessRules.apply(state, 1, 7, 0, 7);
        assertEquals(piece(PieceType.QUEEN, PieceColor.WHITE), state.board[0][7]);

        BoardState black = emptyBoard();
        put(black, 6, 0, PieceType.PAWN, PieceColor.BLACK);
        black.turn = PieceColor.BLACK;
        ChessRules.apply(black, 6, 0, 7, 0);
        assertEquals(piece(PieceType.QUEEN, PieceColor.BLACK), black.board[7][0]);
    }

    @Test
    void kingsideAndQueensideCastlingMoveTheRook() {
        BoardState kingside = new BoardState();
        kingside.reset();
        kingside.board[7][5] = null;
        kingside.board[7][6] = null;
        assertTrue(has(ChessRules.legalTargets(kingside, 7, 4), 7, 6));
        ChessRules.apply(kingside, 7, 4, 7, 6);
        assertEquals(piece(PieceType.KING, PieceColor.WHITE), kingside.board[7][6]);
        assertEquals(piece(PieceType.ROOK, PieceColor.WHITE), kingside.board[7][5]);
        assertNull(kingside.board[7][4]);
        assertNull(kingside.board[7][7]);
        assertFalse(kingside.whiteKingSide);
        assertFalse(kingside.whiteQueenSide);

        BoardState queenside = new BoardState();
        queenside.reset();
        queenside.board[7][1] = null;
        queenside.board[7][2] = null;
        queenside.board[7][3] = null;
        assertTrue(has(ChessRules.legalTargets(queenside, 7, 4), 7, 2));
        ChessRules.apply(queenside, 7, 4, 7, 2);
        assertEquals(piece(PieceType.KING, PieceColor.WHITE), queenside.board[7][2]);
        assertEquals(piece(PieceType.ROOK, PieceColor.WHITE), queenside.board[7][3]);
        assertNull(queenside.board[7][0]);
        assertNull(queenside.board[7][4]);
    }

    @Test
    void blackCanCastleKingside() {
        BoardState state = new BoardState();
        state.reset();
        state.board[0][5] = null;
        state.board[0][6] = null;
        state.turn = PieceColor.BLACK;
        assertTrue(has(ChessRules.legalTargets(state, 0, 4), 0, 6));
    }

    @Test
    void cannotCastleThroughCheckOrOutOfCheck() {
        BoardState through = new BoardState();
        through.reset();
        through.board[7][5] = null;
        through.board[7][6] = null;
        through.board[1][5] = null;
        through.board[6][5] = null;
        through.board[0][5] = piece(PieceType.ROOK, PieceColor.BLACK);
        assertFalse(has(ChessRules.legalTargets(through, 7, 4), 7, 6));

        BoardState inCheck = new BoardState();
        inCheck.reset();
        inCheck.board[7][5] = null;
        inCheck.board[7][6] = null;
        inCheck.board[1][4] = null;
        inCheck.board[6][4] = null;
        inCheck.board[0][0] = piece(PieceType.KING, PieceColor.BLACK);
        inCheck.board[0][4] = piece(PieceType.ROOK, PieceColor.BLACK);
        assertFalse(has(ChessRules.legalTargets(inCheck, 7, 4), 7, 6));
    }

    @Test
    void movingOrCapturingARookDropsCastlingRight() {
        BoardState state = emptyBoard();
        state.board[7][6] = null;
        put(state, 7, 4, PieceType.KING, PieceColor.WHITE);
        put(state, 7, 7, PieceType.ROOK, PieceColor.WHITE);
        state.whiteKingSide = true;
        state.whiteQueenSide = true;
        ChessRules.apply(state, 7, 7, 6, 7);
        assertFalse(state.whiteKingSide);
        assertTrue(state.whiteQueenSide);

        BoardState captured = new BoardState();
        captured.reset();
        captured.board[1][0] = piece(PieceType.ROOK, PieceColor.WHITE);
        ChessRules.apply(captured, 1, 0, 0, 0);
        assertFalse(captured.blackQueenSide);
        assertTrue(captured.whiteQueenSide);
    }

    private static BoardState emptyBoard() {
        BoardState state = new BoardState();
        state.reset();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                state.board[row][col] = null;
            }
        }
        state.whiteKingSide = false;
        state.whiteQueenSide = false;
        state.blackKingSide = false;
        state.blackQueenSide = false;
        state.enPassant = null;
        state.turn = PieceColor.WHITE;
        state.selected = null;
        put(state, 7, 6, PieceType.KING, PieceColor.WHITE);
        put(state, 0, 1, PieceType.KING, PieceColor.BLACK);
        return state;
    }

    private static void put(BoardState state, int row, int col, PieceType type, PieceColor color) {
        state.board[row][col] = piece(type, color);
    }

    private static Piece piece(PieceType type, PieceColor color) {
        return new Piece(type, color);
    }

    private static boolean has(List<MoveTarget> moves, int row, int col) {
        return moves.stream().anyMatch(move -> move.row() == row && move.col() == col);
    }

    private static boolean capture(List<MoveTarget> moves, int row, int col) {
        return moves.stream().anyMatch(move -> move.row() == row && move.col() == col && move.capture());
    }
}
