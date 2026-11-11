package pl.uam.chess.logic.engine;

import pl.uam.chess.model.Square;

public final class EnPassant implements RuleState {

    private Square target;

    public Square target() {
        return target;
    }

    public void set(Square target) {
        this.target = target;
    }
}
