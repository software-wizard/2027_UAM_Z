package pl.uam.chess.logic.move;

import pl.uam.chess.logic.engine.EventBus;
import pl.uam.chess.logic.pieces.ChessPosition;

public final class MoveHandler {

    private final EventBus events;
    private final MoveMaker moveMaker;

    public MoveHandler(Board board, ChessPosition position, EventBus events) {
        this.events = events;
        this.moveMaker = new MoveMaker(
                board::at,
                board::move,
                board::remove,
                board::put,
                board::replace,
                board::create,
                position);
    }

    public void onRequested(MoveRequested event) {
        events.publish(moveMaker.play(event.getFrom(), event.getTo()));
    }
}
