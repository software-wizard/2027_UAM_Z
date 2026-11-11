package pl.uam.chess.game;

import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.PieceType;
import pl.uam.chess.model.Position;

import java.util.Arrays;

final class BoardState {

    Piece[][] board;
    PieceColor turn;
    boolean whiteKingSide;
    boolean whiteQueenSide;
    boolean blackKingSide;
    boolean blackQueenSide;
    Position enPassant;
    Position selected;

    void reset() {
        board = new Piece[8][8];
        PieceType[] backRank = {
                PieceType.ROOK,
                PieceType.KNIGHT,
                PieceType.BISHOP,
                PieceType.QUEEN,
                PieceType.KING,
                PieceType.BISHOP,
                PieceType.KNIGHT,
                PieceType.ROOK
        };
        for (int col = 0; col < 8; col++) {
            board[0][col] = new Piece(backRank[col], PieceColor.BLACK);
            board[1][col] = new Piece(PieceType.PAWN, PieceColor.BLACK);
            board[6][col] = new Piece(PieceType.PAWN, PieceColor.WHITE);
            board[7][col] = new Piece(backRank[col], PieceColor.WHITE);
        }
        turn = PieceColor.WHITE;
        whiteKingSide = true;
        whiteQueenSide = true;
        blackKingSide = true;
        blackQueenSide = true;
        enPassant = null;
        selected = null;
    }

    BoardState copy() {
        BoardState copy = new BoardState();
        copy.board = new Piece[8][8];
        for (int row = 0; row < 8; row++) {
            copy.board[row] = Arrays.copyOf(board[row], 8);
        }
        copy.turn = turn;
        copy.whiteKingSide = whiteKingSide;
        copy.whiteQueenSide = whiteQueenSide;
        copy.blackKingSide = blackKingSide;
        copy.blackQueenSide = blackQueenSide;
        copy.enPassant = enPassant;
        copy.selected = selected;
        return copy;
    }
}
