package pl.uam.chess.game;

import pl.uam.chess.api.GameView;
import pl.uam.chess.api.PieceView;
import pl.uam.chess.model.MoveTarget;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.Position;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final BoardState state = new BoardState();

    public GameService() {
        state.reset();
    }

    public synchronized GameView view() {
        return toView();
    }

    public synchronized GameView reset() {
        state.reset();
        return toView();
    }

    public synchronized GameView click(int row, int col) {
        if (!inside(row, col)) {
            state.selected = null;
            return toView();
        }
        if (state.selected == null) {
            Piece piece = state.board[row][col];
            if (piece != null && piece.color() == state.turn) {
                state.selected = new Position(row, col);
            }
            return toView();
        }

        int fromRow = state.selected.row();
        int fromCol = state.selected.col();
        boolean legal = ChessRules.legalTargets(state, fromRow, fromCol).stream()
                .anyMatch(target -> target.row() == row && target.col() == col);
        if (legal) {
            ChessRules.apply(state, fromRow, fromCol, row, col);
        }
        state.selected = null;
        return toView();
    }

    BoardState position() {
        return state;
    }

    private GameView toView() {
        PieceView[][] cells = new PieceView[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = state.board[row][col];
                if (piece != null) {
                    cells[row][col] = new PieceView(piece.type().name(), piece.color().name());
                }
            }
        }
        List<MoveTarget> legalMoves = List.of();
        if (state.selected != null) {
            legalMoves = ChessRules.legalTargets(state, state.selected.row(), state.selected.col());
        }
        return new GameView(cells, state.turn.name(), state.selected, legalMoves);
    }

    private static boolean inside(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
}
