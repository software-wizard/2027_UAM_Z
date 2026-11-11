package pl.uam.chess.communication;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class GameView {
    Map<String, PieceView> board;
    String turn;
    SquareView selected;
    List<MoveView> legalMoves;
    boolean mate;
}
