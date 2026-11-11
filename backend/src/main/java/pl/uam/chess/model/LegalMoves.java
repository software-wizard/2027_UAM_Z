package pl.uam.chess.model;

import java.util.List;

public final class LegalMoves {

    private final List<LegalMove> moves;

    public LegalMoves(List<LegalMove> moves) {
        this.moves = List.copyOf(moves);
    }

    public static LegalMoves none() {
        return new LegalMoves(List.of());
    }

    public List<LegalMove> moves() {
        return moves;
    }

    public boolean contains(Square square) {
        for (LegalMove move : moves) {
            if (move.getColumn() == square.getColumn() && move.getRow() == square.getRow()) {
                return true;
            }
        }
        return false;
    }
}
