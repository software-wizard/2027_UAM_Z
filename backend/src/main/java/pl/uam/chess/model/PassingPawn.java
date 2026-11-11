package pl.uam.chess.model;

public interface PassingPawn {

    Square enPassantTargetAfter(Square from, Square to);

    default boolean capturesEnPassant(Square enPassantTarget, Square from, Square to) {
        return enPassantTarget != null && to.equals(enPassantTarget) && from.point().getColumn() != to.point().getColumn();
    }

    default Square victimSquare(Square from, Square to) {
        return Square.at(to.point().getColumn(), from.point().getRow());
    }
}
