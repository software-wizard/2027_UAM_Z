package pl.uam.chess.logic.engine;

import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import pl.uam.chess.logic.move.MoveRequested;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.Square;

public final class ClickDispatcher {

    private final Function<Square, Piece> pieceAt;
    private final Predicate<PieceColor> myMove;
    private final SelectionManager selectionManager;
    private final BooleanSupplier mate;
    private final EventBus events;

    public ClickDispatcher(
            Function<Square, Piece> pieceAt,
            Predicate<PieceColor> myMove,
            SelectionManager selectionManager,
            BooleanSupplier mate,
            EventBus events) {
        this.pieceAt = pieceAt;
        this.myMove = myMove;
        this.selectionManager = selectionManager;
        this.mate = mate;
        this.events = events;
    }

    public void onClick(SquareClicked event) {
        if (mate.getAsBoolean()) {
            return;
        }
        Square square = event.getSquare();
        if (square == null) {
            selectionManager.clear();
            return;
        }
        if (selectionManager.square() == null) {
            Piece piece = pieceAt.apply(square);
            if (piece != null && myMove.test(piece.color())) {
                events.publish(new PieceSelected(piece));
            }
            return;
        }
        if (selectionManager.isDestination(square)) {
            events.publish(new MoveRequested(selectionManager.square(), square));
            return;
        }
        selectionManager.clear();
    }
}
