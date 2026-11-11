package pl.uam.chess.api;

import pl.uam.chess.model.MoveTarget;
import pl.uam.chess.model.Position;

import java.util.List;

public record GameView(
        PieceView[][] board,
        String turn,
        Position selected,
        List<MoveTarget> legalMoves
) {
}
