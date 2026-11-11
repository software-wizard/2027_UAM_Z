package pl.uam.chess.logic.move;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import pl.uam.chess.logic.pieces.Capture;
import pl.uam.chess.logic.pieces.ChessPosition;
import pl.uam.chess.model.Pawn;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.Queen;
import pl.uam.chess.model.Square;

public final class MoveMaker {

    private final Function<Square, Piece> pieceAt;
    private final BiConsumer<Square, Square> move;
    private final Function<Square, Piece> remove;
    private final BiConsumer<Piece, Square> put;
    private final BiConsumer<Square, Piece> replace;
    private final ChessPosition position;
    private final BiFunction<String, PieceColor, Piece> create;

    public MoveMaker(
            Function<Square, Piece> pieceAt,
            BiConsumer<Square, Square> move,
            Function<Square, Piece> remove,
            BiConsumer<Piece, Square> put,
            BiConsumer<Square, Piece> replace,
            BiFunction<String, PieceColor, Piece> create,
            ChessPosition position) {
        this.pieceAt = pieceAt;
        this.move = move;
        this.remove = remove;
        this.put = put;
        this.replace = replace;
        this.create = create;
        this.position = position;
    }

    public MovePlayed play(Square from, Square to) {
        Piece moving = pieceAt.apply(from);
        if (moving == null) {
            throw new IllegalStateException("Na " + from.notation() + " nie ma figury");
        }
        Capture capture = moving.takenOn(position, to);
        if (capture != null && !capture.getSquare().equals(to)) {
            remove.apply(capture.getSquare());
        }
        if (CastlingCalculator.isCastling(moving, from, to)) {
            move.accept(CastlingCalculator.rookFrom(from, to), CastlingCalculator.rookTo(from, to));
        }
        move.accept(from, to);
        if (moving instanceof Pawn pawn && pawn.readyToPromote(to)) {
            replace.accept(to, create.apply(Queen.NAME, pawn.color()));
        }
        return new MovePlayed(
                moving,
                from,
                to,
                capture == null ? null : capture.getPiece(),
                capture == null ? null : capture.getSquare());
    }

    public void undo(MovePlayed played) {
        Square from = played.getFrom();
        Square to = played.getTo();
        Piece moved = played.getPiece();
        if (pieceAt.apply(to) != moved) {
            remove.apply(to);
            put.accept(moved, from);
        } else {
            move.accept(to, from);
        }
        if (played.getCaptured() != null) {
            put.accept(played.getCaptured(), played.getCapturedAt());
        }
        if (CastlingCalculator.isCastling(moved, from, to)) {
            move.accept(CastlingCalculator.rookTo(from, to), CastlingCalculator.rookFrom(from, to));
        }
    }
}
