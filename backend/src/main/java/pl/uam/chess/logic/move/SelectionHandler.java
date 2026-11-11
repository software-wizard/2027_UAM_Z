package pl.uam.chess.logic.move;

import pl.uam.chess.logic.engine.PieceSelected;
import pl.uam.chess.logic.engine.SelectionManager;

public final class SelectionHandler {

    private final SelectionManager selectionManager;
    private final LegalMovesCalculator legalMovesCalculator;

    public SelectionHandler(SelectionManager selectionManager, LegalMovesCalculator legalMovesCalculator) {
        this.selectionManager = selectionManager;
        this.legalMovesCalculator = legalMovesCalculator;
    }

    public void onSelected(PieceSelected event) {
        selectionManager.choose(event.getPiece().square(), legalMovesCalculator.forPiece(event.getPiece()));
    }

    public void onPlayed(MovePlayed event) {
        selectionManager.clear();
    }
}
