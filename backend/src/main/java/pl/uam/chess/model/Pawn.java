package pl.uam.chess.model;

import pl.uam.chess.logic.pieces.ChessPosition;

public final class Pawn extends Piece implements PassingPawn {

    public static final String NAME = "PAWN";

    public Pawn(PieceColor color) {
        super(color);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean canReach(ChessPosition position, Square target) {
        ChessPoint from = square().point();
        ChessPoint to = target.point();
        int forward = forward();
        int rowDelta = from.getRow().stepsTo(to.getRow());
        int columnDelta = from.getColumn().stepsTo(to.getColumn());
        if (columnDelta == 0 && rowDelta == forward) {
            return position.pieceAt(target) == null;
        }
        if (columnDelta == 0 && rowDelta == 2 * forward && from.getRow() == startRow()) {
            Square oneStep = Square.at(from.getColumn(), from.getRow().shift(forward));
            return position.pieceAt(oneStep) == null && position.pieceAt(target) == null;
        }
        if (Math.abs(columnDelta) == 1 && rowDelta == forward) {
            return takenOn(position, target) != null;
        }
        return false;
    }

    @Override
    public boolean attacks(ChessPosition position, Square target) {
        ChessPoint from = square().point();
        ChessPoint to = target.point();
        return from.getRow().stepsTo(to.getRow()) == forward()
                && Math.abs(from.getColumn().stepsTo(to.getColumn())) == 1;
    }

    public boolean readyToPromote(Square destination) {
        return destination.getRow() == promotionRow();
    }

    @Override
    public Square enPassantTargetAfter(Square from, Square to) {
        ChessPoint start = from.point();
        ChessPoint end = to.point();
        if (start.getColumn() != end.getColumn() || Math.abs(start.getRow().stepsTo(end.getRow())) != 2) {
            return null;
        }
        return Square.at(start.getColumn(), start.getRow().between(end.getRow()));
    }

    private int forward() {
        return color() == PieceColor.WHITE ? 1 : -1;
    }

    private Row startRow() {
        return color() == PieceColor.WHITE ? Row.R2 : Row.R7;
    }

    private Row promotionRow() {
        return color() == PieceColor.WHITE ? Row.R8 : Row.R1;
    }
}
