package pl.uam.chess.model;

import pl.uam.chess.logic.pieces.Capture;
import pl.uam.chess.logic.pieces.ChessPosition;
import pl.uam.chess.logic.engine.EnPassant;

public abstract class Piece {

    private final PieceColor color;
    private Square square;

    protected Piece(PieceColor color) {
        this.color = color;
    }

    public final PieceColor color() {
        return color;
    }

    public final Square square() {
        return square;
    }

    public final void place(Square square) {
        this.square = square;
    }

    public abstract String name();

    public final boolean canMove(ChessPosition position, Square target) {
        if (square == null || !position.myMove(color)) {
            return false;
        }
        if (!canReach(position, target)) {
            return false;
        }
        return position.allows(this, target);
    }

    public abstract boolean canReach(ChessPosition position, Square target);

    public abstract boolean attacks(ChessPosition position, Square target);

    public Capture takenOn(ChessPosition position, Square target) {
        Capture passing = passingCapture(position, target);
        if (passing != null) {
            return passing;
        }
        Piece occupant = position.pieceAt(target);
        if (occupant != null && occupant.color() != color && occupant.canBeCaptured()) {
            return new Capture(occupant, target);
        }
        return null;
    }

    private Capture passingCapture(ChessPosition position, Square target) {
        if (!(this instanceof PassingPawn passing)) {
            return null;
        }
        EnPassant enPassant = position.rule(EnPassant.class);
        if (enPassant == null || !passing.capturesEnPassant(enPassant.target(), square, target)) {
            return null;
        }
        Square victimAt = passing.victimSquare(square, target);
        Piece victim = position.pieceAt(victimAt);
        if (victim == null) {
            return null;
        }
        return new Capture(victim, victimAt);
    }

    public boolean isRoyal() {
        return false;
    }

    public boolean isCastleRook() {
        return false;
    }

    public boolean canBeCaptured() {
        return true;
    }
}
