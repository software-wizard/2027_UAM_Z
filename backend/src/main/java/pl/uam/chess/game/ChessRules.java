package pl.uam.chess.game;

import pl.uam.chess.model.MoveTarget;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.PieceType;
import pl.uam.chess.model.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Ruchy figur, roszada, bicie w przelocie i promocja do hetmana.
 * Mata, pata i remisy nie są liczone — partia po prostu trwa.
 */
final class ChessRules {

    private static final int[][] KNIGHT_OFFSETS = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
    };
    private static final int[][] KING_OFFSETS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };
    private static final int[][] BISHOP_DIRECTIONS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    private static final int[][] ROOK_DIRECTIONS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private ChessRules() {
    }

    static List<MoveTarget> legalTargets(BoardState state, int row, int col) {
        Piece piece = at(state.board, row, col);
        if (piece == null || piece.color() != state.turn) {
            return List.of();
        }
        List<MoveTarget> legal = new ArrayList<>();
        for (Position candidate : pseudo(state, row, col, piece)) {
            if (kingSafeAfter(state, row, col, candidate.row(), candidate.col())) {
                legal.add(new MoveTarget(
                        candidate.row(),
                        candidate.col(),
                        isCapture(state, row, col, candidate.row(), candidate.col())));
            }
        }
        return legal;
    }

    static void apply(BoardState state, int fromRow, int fromCol, int toRow, int toCol) {
        Piece moving = state.board[fromRow][fromCol];
        if (moving == null) {
            return;
        }
        Piece captured = state.board[toRow][toCol];
        boolean enPassant = isEnPassant(state, fromRow, fromCol, toRow, toCol);

        clearRookRight(state, toRow, toCol, captured);

        if (moving.type() == PieceType.KING && Math.abs(toCol - fromCol) == 2) {
            int rookFromCol = toCol > fromCol ? 7 : 0;
            int rookToCol = toCol > fromCol ? 5 : 3;
            state.board[fromRow][rookToCol] = state.board[fromRow][rookFromCol];
            state.board[fromRow][rookFromCol] = null;
        }

        if (enPassant) {
            state.board[fromRow][toCol] = null;
        }

        state.board[toRow][toCol] = moving;
        state.board[fromRow][fromCol] = null;

        if (moving.type() == PieceType.PAWN && (toRow == 0 || toRow == 7)) {
            state.board[toRow][toCol] = new Piece(PieceType.QUEEN, moving.color());
        }

        if (moving.type() == PieceType.KING) {
            if (moving.color() == PieceColor.WHITE) {
                state.whiteKingSide = false;
                state.whiteQueenSide = false;
            } else {
                state.blackKingSide = false;
                state.blackQueenSide = false;
            }
        }
        if (moving.type() == PieceType.ROOK) {
            clearRookRight(state, fromRow, fromCol, moving);
        }

        if (moving.type() == PieceType.PAWN && Math.abs(toRow - fromRow) == 2) {
            state.enPassant = new Position((fromRow + toRow) / 2, fromCol);
        } else {
            state.enPassant = null;
        }

        state.turn = moving.color().opposite();
        state.selected = null;
    }

    static boolean isAttacked(Piece[][] board, int row, int col, PieceColor by) {
        int pawnSourceRow = by == PieceColor.WHITE ? row + 1 : row - 1;
        if (is(board, pawnSourceRow, col - 1, by, PieceType.PAWN)
                || is(board, pawnSourceRow, col + 1, by, PieceType.PAWN)) {
            return true;
        }
        for (int[] offset : KNIGHT_OFFSETS) {
            if (is(board, row + offset[0], col + offset[1], by, PieceType.KNIGHT)) {
                return true;
            }
        }
        for (int[] offset : KING_OFFSETS) {
            if (is(board, row + offset[0], col + offset[1], by, PieceType.KING)) {
                return true;
            }
        }
        if (ray(board, row, col, BISHOP_DIRECTIONS, by, PieceType.BISHOP, PieceType.QUEEN)) {
            return true;
        }
        return ray(board, row, col, ROOK_DIRECTIONS, by, PieceType.ROOK, PieceType.QUEEN);
    }

    private static List<Position> pseudo(BoardState state, int row, int col, Piece piece) {
        List<Position> moves = new ArrayList<>();
        switch (piece.type()) {
            case PAWN -> pawnMoves(state, row, col, piece.color(), moves);
            case KNIGHT -> leapMoves(state.board, row, col, piece.color(), KNIGHT_OFFSETS, moves);
            case BISHOP -> slideMoves(state.board, row, col, piece.color(), BISHOP_DIRECTIONS, moves);
            case ROOK -> slideMoves(state.board, row, col, piece.color(), ROOK_DIRECTIONS, moves);
            case QUEEN -> {
                slideMoves(state.board, row, col, piece.color(), BISHOP_DIRECTIONS, moves);
                slideMoves(state.board, row, col, piece.color(), ROOK_DIRECTIONS, moves);
            }
            case KING -> kingMoves(state, row, col, piece.color(), moves);
        }
        return moves;
    }

    private static void pawnMoves(BoardState state, int row, int col, PieceColor color, List<Position> moves) {
        int direction = color == PieceColor.WHITE ? -1 : 1;
        int forward = row + direction;
        if (on(forward, col) && state.board[forward][col] == null) {
            moves.add(new Position(forward, col));
            int startRow = color == PieceColor.WHITE ? 6 : 1;
            int leap = row + 2 * direction;
            if (row == startRow && state.board[leap][col] == null) {
                moves.add(new Position(leap, col));
            }
        }
        for (int fileDelta : new int[]{-1, 1}) {
            int targetCol = col + fileDelta;
            if (!on(forward, targetCol)) {
                continue;
            }
            if (isEnPassant(state, row, col, forward, targetCol)) {
                moves.add(new Position(forward, targetCol));
                continue;
            }
            Piece target = state.board[forward][targetCol];
            if (target != null && target.color() != color && target.type() != PieceType.KING) {
                moves.add(new Position(forward, targetCol));
            }
        }
    }

    private static void leapMoves(
            Piece[][] board,
            int row,
            int col,
            PieceColor color,
            int[][] offsets,
            List<Position> moves) {
        for (int[] offset : offsets) {
            int targetRow = row + offset[0];
            int targetCol = col + offset[1];
            if (!on(targetRow, targetCol)) {
                continue;
            }
            Piece target = board[targetRow][targetCol];
            if (target == null || (target.color() != color && target.type() != PieceType.KING)) {
                moves.add(new Position(targetRow, targetCol));
            }
        }
    }

    private static void slideMoves(
            Piece[][] board,
            int row,
            int col,
            PieceColor color,
            int[][] directions,
            List<Position> moves) {
        for (int[] direction : directions) {
            int targetRow = row + direction[0];
            int targetCol = col + direction[1];
            while (on(targetRow, targetCol)) {
                Piece target = board[targetRow][targetCol];
                if (target == null) {
                    moves.add(new Position(targetRow, targetCol));
                } else {
                    if (target.color() != color && target.type() != PieceType.KING) {
                        moves.add(new Position(targetRow, targetCol));
                    }
                    break;
                }
                targetRow += direction[0];
                targetCol += direction[1];
            }
        }
    }

    private static void kingMoves(BoardState state, int row, int col, PieceColor color, List<Position> moves) {
        leapMoves(state.board, row, col, color, KING_OFFSETS, moves);
        PieceColor enemy = color.opposite();
        if (color == PieceColor.WHITE && row == 7 && col == 4 && !isAttacked(state.board, 7, 4, enemy)) {
            if (state.whiteKingSide
                    && state.board[7][5] == null
                    && state.board[7][6] == null
                    && is(state.board, 7, 7, color, PieceType.ROOK)
                    && !isAttacked(state.board, 7, 5, enemy)
                    && !isAttacked(state.board, 7, 6, enemy)) {
                moves.add(new Position(7, 6));
            }
            if (state.whiteQueenSide
                    && state.board[7][1] == null
                    && state.board[7][2] == null
                    && state.board[7][3] == null
                    && is(state.board, 7, 0, color, PieceType.ROOK)
                    && !isAttacked(state.board, 7, 2, enemy)
                    && !isAttacked(state.board, 7, 3, enemy)) {
                moves.add(new Position(7, 2));
            }
        }
        if (color == PieceColor.BLACK && row == 0 && col == 4 && !isAttacked(state.board, 0, 4, enemy)) {
            if (state.blackKingSide
                    && state.board[0][5] == null
                    && state.board[0][6] == null
                    && is(state.board, 0, 7, color, PieceType.ROOK)
                    && !isAttacked(state.board, 0, 5, enemy)
                    && !isAttacked(state.board, 0, 6, enemy)) {
                moves.add(new Position(0, 6));
            }
            if (state.blackQueenSide
                    && state.board[0][1] == null
                    && state.board[0][2] == null
                    && state.board[0][3] == null
                    && is(state.board, 0, 0, color, PieceType.ROOK)
                    && !isAttacked(state.board, 0, 2, enemy)
                    && !isAttacked(state.board, 0, 3, enemy)) {
                moves.add(new Position(0, 2));
            }
        }
    }

    private static boolean kingSafeAfter(BoardState state, int fromRow, int fromCol, int toRow, int toCol) {
        BoardState copy = state.copy();
        PieceColor mover = state.board[fromRow][fromCol].color();
        apply(copy, fromRow, fromCol, toRow, toCol);
        Position king = findKing(copy.board, mover);
        return king != null && !isAttacked(copy.board, king.row(), king.col(), mover.opposite());
    }

    private static boolean ray(
            Piece[][] board,
            int row,
            int col,
            int[][] directions,
            PieceColor by,
            PieceType first,
            PieceType second) {
        for (int[] direction : directions) {
            int targetRow = row + direction[0];
            int targetCol = col + direction[1];
            while (on(targetRow, targetCol)) {
                Piece piece = board[targetRow][targetCol];
                if (piece != null) {
                    if (piece.color() == by && (piece.type() == first || piece.type() == second)) {
                        return true;
                    }
                    break;
                }
                targetRow += direction[0];
                targetCol += direction[1];
            }
        }
        return false;
    }

    private static boolean isCapture(BoardState state, int fromRow, int fromCol, int toRow, int toCol) {
        return isEnPassant(state, fromRow, fromCol, toRow, toCol) || state.board[toRow][toCol] != null;
    }

    private static boolean isEnPassant(BoardState state, int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = at(state.board, fromRow, fromCol);
        return piece != null
                && piece.type() == PieceType.PAWN
                && state.enPassant != null
                && state.enPassant.row() == toRow
                && state.enPassant.col() == toCol
                && fromCol != toCol;
    }

    private static void clearRookRight(BoardState state, int row, int col, Piece piece) {
        if (piece == null || piece.type() != PieceType.ROOK) {
            return;
        }
        if (row == 7 && col == 0 && piece.color() == PieceColor.WHITE) {
            state.whiteQueenSide = false;
        } else if (row == 7 && col == 7 && piece.color() == PieceColor.WHITE) {
            state.whiteKingSide = false;
        } else if (row == 0 && col == 0 && piece.color() == PieceColor.BLACK) {
            state.blackQueenSide = false;
        } else if (row == 0 && col == 7 && piece.color() == PieceColor.BLACK) {
            state.blackKingSide = false;
        }
    }

    private static Position findKing(Piece[][] board, PieceColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.type() == PieceType.KING && piece.color() == color) {
                    return new Position(row, col);
                }
            }
        }
        return null;
    }

    private static boolean is(Piece[][] board, int row, int col, PieceColor color, PieceType type) {
        Piece piece = at(board, row, col);
        return piece != null && piece.color() == color && piece.type() == type;
    }

    private static Piece at(Piece[][] board, int row, int col) {
        if (!on(row, col)) {
            return null;
        }
        return board[row][col];
    }

    private static boolean on(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
