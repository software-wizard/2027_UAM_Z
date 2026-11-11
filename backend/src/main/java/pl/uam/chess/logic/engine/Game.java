package pl.uam.chess.logic.engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pl.uam.chess.logic.move.Board;
import pl.uam.chess.logic.pieces.ChessPosition;
import pl.uam.chess.logic.preparation.PositionSetup;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.Square;

public final class Game implements ChessPosition {

    private final Board board;
    private final Map<Class<? extends RuleState>, RuleState> rules = new LinkedHashMap<>();
    private final List<MoveGate> gates = new ArrayList<>();
    private final TurnPolicy turns = new SideToMovePolicy();
    private PieceColor turn = PieceColor.WHITE;

    public Game(Board board, PositionSetup setup) {
        this.board = board;
        addRule(new EnPassant());
        addRule(CastlingRights.all());
        addGate(new KingSafetyGate(board, this));
        setup.place(board);
    }

    @Override
    public Piece pieceAt(Square square) {
        return board.at(square);
    }

    @Override
    public PieceColor turn() {
        return turn;
    }

    public void setTurn(PieceColor turn) {
        this.turn = turn;
    }

    public void addRule(RuleState rule) {
        rules.put(rule.getClass(), rule);
    }

    public void addGate(MoveGate gate) {
        gates.add(gate);
    }

    @Override
    public boolean myMove(PieceColor color) {
        return turns.mayMove(color, turn);
    }

    @Override
    public <T extends RuleState> T rule(Class<T> type) {
        return type.cast(rules.get(type));
    }

    @Override
    public boolean isAttacked(Square square, PieceColor by) {
        for (Piece piece : board.pieces(by)) {
            if (piece.attacks(this, square)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allows(Piece piece, Square target) {
        for (MoveGate gate : gates) {
            if (!gate.allows(piece, target)) {
                return false;
            }
        }
        return true;
    }

    public Board board() {
        return board;
    }
}
