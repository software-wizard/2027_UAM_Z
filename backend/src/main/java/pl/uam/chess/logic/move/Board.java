package pl.uam.chess.logic.move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.uam.chess.logic.pieces.PieceFactory;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.Square;

public final class Board {

    private final PieceFactory factory = new PieceFactory();
    private final Map<Square, Piece> squares = new HashMap<>();

    public Board() {
    }

    public Piece create(String name, PieceColor color) {
        return factory.create(name, color);
    }

    public Piece at(Square square) {
        return squares.get(square);
    }

    public void put(String name, PieceColor color, Square square) {
        put(create(name, color), square);
    }

    public void put(Piece piece, Square square) {
        Piece previous = squares.put(square, piece);
        if (previous != null) {
            previous.place(null);
        }
        piece.place(square);
    }

    public void move(Square from, Square to) {
        Piece moving = squares.remove(from);
        Piece occupant = squares.put(to, moving);
        if (occupant != null) {
            occupant.place(null);
        }
        moving.place(to);
    }

    public Piece remove(Square square) {
        Piece piece = squares.remove(square);
        if (piece != null) {
            piece.place(null);
        }
        return piece;
    }

    public void replace(Square square, Piece piece) {
        Piece previous = squares.put(square, piece);
        if (previous != null) {
            previous.place(null);
        }
        piece.place(square);
    }

    public List<Piece> pieces(PieceColor color) {
        List<Piece> found = new ArrayList<>();
        for (Square square : Square.all()) {
            Piece piece = at(square);
            if (piece != null && piece.color() == color) {
                found.add(piece);
            }
        }
        return found;
    }
}
