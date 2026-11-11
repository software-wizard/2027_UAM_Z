package pl.uam.chess.communication;

import org.springframework.stereotype.Service;
import pl.uam.chess.logic.engine.Game;
import pl.uam.chess.logic.engine.GameSession;
import pl.uam.chess.logic.engine.SelectionManager;
import pl.uam.chess.model.LegalMove;
import pl.uam.chess.model.Column;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.Row;
import pl.uam.chess.model.Square;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameService {

    private GameSession session = new GameSession();

    public synchronized GameView view() {
        return prepareGameView();
    }

    public synchronized GameView start() {
        session = new GameSession();
        return prepareGameView();
    }

    public synchronized GameView click(Column column, Row row) {
        session.click(column, row);
        return prepareGameView();
    }

    private GameView prepareGameView() {
        Game game = session.game();
        Map<String, PieceView> cells = new LinkedHashMap<>();
        for (Square square : Square.all()) {
            Piece piece = game.pieceAt(square);
            if (piece != null) {
                cells.put(square.notation(), new PieceView(piece.name(), piece.color().name()));
            }
        }
        SelectionManager selectionManager = session.selectionManager();
        Square selected = selectionManager.square();
        List<MoveView> legalMoves = new ArrayList<>();
        for (LegalMove move : selectionManager.legalMoves().moves()) {
            legalMoves.add(new MoveView(move.getColumn(), move.getRow(), move.isCapture()));
        }
        return new GameView(
                cells,
                game.turn().name(),
                selected == null ? null : new SquareView(selected.getColumn(), selected.getRow()),
                legalMoves,
                session.mate());
    }
}
