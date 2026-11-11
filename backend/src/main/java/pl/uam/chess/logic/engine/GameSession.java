package pl.uam.chess.logic.engine;

import java.util.function.Consumer;
import pl.uam.chess.logic.move.Board;
import pl.uam.chess.logic.move.CastlingHandler;
import pl.uam.chess.logic.move.EnPassantHandler;
import pl.uam.chess.logic.move.LegalMovesCalculator;
import pl.uam.chess.logic.move.MoveHandler;
import pl.uam.chess.logic.move.MovePlayed;
import pl.uam.chess.logic.move.MoveRequested;
import pl.uam.chess.logic.move.SelectionHandler;
import pl.uam.chess.logic.move.TurnHandler;
import pl.uam.chess.logic.pieces.PieceFactory;
import pl.uam.chess.logic.preparation.PositionSetup;
import pl.uam.chess.logic.preparation.StartingSetup;
import pl.uam.chess.model.Column;
import pl.uam.chess.model.Row;
import pl.uam.chess.model.Square;

public final class GameSession {

    private final EventBus events = new EventBus();
    private final Game game;
    private final SelectionManager selectionManager = new SelectionManager();
    private final MateCalculator mateCalculator;

    public GameSession() {
        this(new PieceFactory(), new StartingSetup(), bus -> {
        });
    }

    public GameSession(PieceFactory factory, PositionSetup setup, Consumer<EventBus> extend) {
        Board board = new Board(factory);
        game = new Game(board, setup);
        LegalMovesCalculator legalMovesCalculator = new LegalMovesCalculator(game);
        mateCalculator = new MateCalculator(board, game, legalMovesCalculator);
        SelectionHandler selectionHandler = new SelectionHandler(selectionManager, legalMovesCalculator);
        events.subscribe(SquareClicked.class, new ClickDispatcher(board::at, game::myMove, selectionManager, mateCalculator::isMate, events)::onClick);
        events.subscribe(PieceSelected.class, selectionHandler::onSelected);
        events.subscribe(MoveRequested.class, new MoveHandler(board, game, events)::onRequested);
        events.subscribe(MovePlayed.class, selectionHandler::onPlayed);
        events.subscribe(MovePlayed.class, new TurnHandler(game::setTurn)::onPlayed);
        events.subscribe(MovePlayed.class, new CastlingHandler(game.rule(CastlingRights.class))::onPlayed);
        events.subscribe(MovePlayed.class, new EnPassantHandler(game.rule(EnPassant.class))::onPlayed);
        extend.accept(events);
    }

    public void click(Column column, Row row) {
        Square square = column == null || row == null ? null : Square.at(column, row);
        events.publish(new SquareClicked(square));
    }

    public Game game() {
        return game;
    }

    public SelectionManager selectionManager() {
        return selectionManager;
    }

    public boolean mate() {
        return mateCalculator.isMate();
    }
}
