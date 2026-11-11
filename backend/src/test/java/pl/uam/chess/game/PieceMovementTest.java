package pl.uam.chess.game;

import pl.uam.chess.model.MoveTarget;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.PieceType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PieceMovementTest {

    @Test
    void pawnMovesForwardAndCapturesDiagonally() {
        BoardState whiteStart = arena();
        put(whiteStart, 6, 4, PieceType.PAWN, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(whiteStart, 6, 4), "e3", "e4");

        BoardState whiteMiddle = arena();
        put(whiteMiddle, 4, 4, PieceType.PAWN, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(whiteMiddle, 4, 4), "e5");

        BoardState blocked = arena();
        put(blocked, 6, 4, PieceType.PAWN, PieceColor.WHITE);
        put(blocked, 5, 4, PieceType.PAWN, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(blocked, 6, 4));

        BoardState capturing = arena();
        put(capturing, 4, 4, PieceType.PAWN, PieceColor.WHITE);
        put(capturing, 3, 3, PieceType.KNIGHT, PieceColor.BLACK);
        put(capturing, 3, 5, PieceType.ROOK, PieceColor.BLACK);
        put(capturing, 3, 4, PieceType.BISHOP, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(capturing, 4, 4), "d5x", "f5x");

        BoardState blackStart = arena();
        blackStart.turn = PieceColor.BLACK;
        put(blackStart, 1, 4, PieceType.PAWN, PieceColor.BLACK);
        assertTargets(ChessRules.legalTargets(blackStart, 1, 4), "e6", "e5");

        BoardState blackCapturing = arena();
        blackCapturing.turn = PieceColor.BLACK;
        put(blackCapturing, 4, 4, PieceType.PAWN, PieceColor.BLACK);
        put(blackCapturing, 5, 3, PieceType.QUEEN, PieceColor.WHITE);
        put(blackCapturing, 5, 5, PieceType.BISHOP, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(blackCapturing, 4, 4), "e3", "d3x", "f3x");

        BoardState promoting = arena();
        put(promoting, 1, 4, PieceType.PAWN, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(promoting, 1, 4), "e8");
        ChessRules.apply(promoting, 1, 4, 0, 4);
        assertEquals(piece(PieceType.QUEEN, PieceColor.WHITE), promoting.board[0][4]);
        assertNull(promoting.board[1][4]);
    }

    @Test
    void knightJumpsInAnLShape() {
        BoardState open = arena();
        put(open, 4, 4, PieceType.KNIGHT, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(open, 4, 4),
                "c5", "c3", "d6", "d2", "f6", "f2", "g5", "g3");

        BoardState corner = arena();
        put(corner, 7, 0, PieceType.KNIGHT, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(corner, 7, 0), "b3", "c2");

        BoardState crowded = arena();
        put(crowded, 4, 4, PieceType.KNIGHT, PieceColor.WHITE);
        put(crowded, 3, 4, PieceType.PAWN, PieceColor.WHITE);
        put(crowded, 2, 3, PieceType.PAWN, PieceColor.WHITE);
        put(crowded, 2, 5, PieceType.BISHOP, PieceColor.BLACK);
        assertTargets(ChessRules.legalTargets(crowded, 4, 4),
                "c5", "c3", "d2", "f6x", "f2", "g5", "g3");

        ChessRules.apply(crowded, 4, 4, 2, 5);
        assertEquals(piece(PieceType.KNIGHT, PieceColor.WHITE), crowded.board[2][5]);
        assertEquals(piece(PieceType.PAWN, PieceColor.WHITE), crowded.board[3][4]);
        assertNull(crowded.board[4][4]);

        crowded.turn = PieceColor.WHITE;
        put(crowded, 4, 4, PieceType.KNIGHT, PieceColor.BLACK);
        assertTrue(ChessRules.legalTargets(crowded, 4, 4).isEmpty());
    }

    @Test
    void bishopMovesOnDiagonalsUntilBlocked() {
        BoardState open = arena();
        put(open, 4, 4, PieceType.BISHOP, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(open, 4, 4),
                "a8", "b7", "c6", "d5", "f5", "g6", "h7",
                "d3", "c2", "b1", "f3", "g2", "h1");

        BoardState blocked = arena();
        put(blocked, 4, 4, PieceType.BISHOP, PieceColor.WHITE);
        put(blocked, 2, 2, PieceType.KNIGHT, PieceColor.BLACK);
        put(blocked, 2, 6, PieceType.PAWN, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(blocked, 4, 4),
                "d5", "c6x",
                "f5",
                "d3", "c2", "b1",
                "f3", "g2", "h1");

        ChessRules.apply(blocked, 4, 4, 2, 2);
        assertEquals(piece(PieceType.BISHOP, PieceColor.WHITE), blocked.board[2][2]);
        assertNull(blocked.board[4][4]);
    }

    @Test
    void rookMovesOnRanksAndFilesUntilBlocked() {
        BoardState open = arena();
        put(open, 4, 4, PieceType.ROOK, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(open, 4, 4),
                "a4", "b4", "c4", "d4", "f4", "g4", "h4",
                "e1", "e2", "e3", "e5", "e6", "e7", "e8");

        BoardState blocked = arena();
        put(blocked, 4, 4, PieceType.ROOK, PieceColor.WHITE);
        put(blocked, 4, 2, PieceType.BISHOP, PieceColor.WHITE);
        put(blocked, 1, 4, PieceType.QUEEN, PieceColor.BLACK);
        assertTargets(ChessRules.legalTargets(blocked, 4, 4),
                "d4",
                "f4", "g4", "h4",
                "e1", "e2", "e3", "e5", "e6", "e7x");

        ChessRules.apply(blocked, 4, 4, 1, 4);
        assertEquals(piece(PieceType.ROOK, PieceColor.WHITE), blocked.board[1][4]);
        assertNull(blocked.board[4][4]);
        assertEquals(piece(PieceType.BISHOP, PieceColor.WHITE), blocked.board[4][2]);
    }

    @Test
    void queenMovesAsRookAndBishop() {
        BoardState open = arena();
        put(open, 4, 4, PieceType.QUEEN, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(open, 4, 4),
                "a8", "b7", "c6", "d5", "f5", "g6", "h7",
                "d3", "c2", "b1", "f3", "g2", "h1",
                "a4", "b4", "c4", "d4", "f4", "g4", "h4",
                "e1", "e2", "e3", "e5", "e6", "e7", "e8");

        BoardState blocked = arena();
        put(blocked, 4, 4, PieceType.QUEEN, PieceColor.WHITE);
        put(blocked, 4, 7, PieceType.ROOK, PieceColor.BLACK);
        put(blocked, 6, 2, PieceType.PAWN, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(blocked, 4, 4),
                "a8", "b7", "c6", "d5", "f5", "g6", "h7",
                "d3",
                "f3", "g2", "h1",
                "a4", "b4", "c4", "d4", "f4", "g4", "h4x",
                "e1", "e2", "e3", "e5", "e6", "e7", "e8");

        ChessRules.apply(blocked, 4, 4, 4, 7);
        assertEquals(piece(PieceType.QUEEN, PieceColor.WHITE), blocked.board[4][7]);
        assertNull(blocked.board[4][4]);
    }

    @Test
    void kingMovesOneSquareInAnyDirection() {
        BoardState open = arena();
        open.board[7][6] = null;
        put(open, 4, 4, PieceType.KING, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(open, 4, 4),
                "d5", "e5", "f5", "d4", "f4", "d3", "e3", "f3");

        BoardState corner = arena();
        corner.board[7][6] = null;
        put(corner, 7, 0, PieceType.KING, PieceColor.WHITE);
        assertTargets(ChessRules.legalTargets(corner, 7, 0), "a2", "b1", "b2");

        BoardState crowded = arena();
        crowded.board[7][6] = null;
        put(crowded, 4, 4, PieceType.KING, PieceColor.WHITE);
        put(crowded, 3, 3, PieceType.ROOK, PieceColor.BLACK);
        put(crowded, 3, 5, PieceType.PAWN, PieceColor.WHITE);
        put(crowded, 5, 3, PieceType.PAWN, PieceColor.BLACK);
        assertTargets(ChessRules.legalTargets(crowded, 4, 4),
                "d5x", "f4", "e3", "f3");

        ChessRules.apply(crowded, 4, 4, 3, 3);
        assertEquals(piece(PieceType.KING, PieceColor.WHITE), crowded.board[3][3]);
        assertNull(crowded.board[4][4]);
        assertEquals(piece(PieceType.PAWN, PieceColor.WHITE), crowded.board[3][5]);
    }

    private static void assertTargets(List<MoveTarget> moves, String... expected) {
        TreeSet<String> actual = new TreeSet<>();
        for (MoveTarget move : moves) {
            actual.add(square(move.row(), move.col()) + (move.capture() ? "x" : ""));
        }
        assertEquals(new TreeSet<>(List.of(expected)), actual);
    }

    private static String square(int row, int col) {
        return "" + (char) ('a' + col) + (8 - row);
    }

    private static BoardState arena() {
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
}
