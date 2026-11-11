package pl.uam.chess.logic.pieces;

import java.util.HashMap;
import java.util.Map;
import pl.uam.chess.model.Bishop;
import pl.uam.chess.model.King;
import pl.uam.chess.model.Knight;
import pl.uam.chess.model.Pawn;
import pl.uam.chess.model.Piece;
import pl.uam.chess.model.PieceColor;
import pl.uam.chess.model.Queen;
import pl.uam.chess.model.Rook;

public final class PieceFactory {

    private final Map<String, PieceCreator> creators = new HashMap<>();

    public PieceFactory() {
        register(King.NAME, King::new);
        register(Queen.NAME, Queen::new);
        register(Rook.NAME, Rook::new);
        register(Bishop.NAME, Bishop::new);
        register(Knight.NAME, Knight::new);
        register(Pawn.NAME, Pawn::new);
    }

    public void register(String name, PieceCreator creator) {
        creators.put(name.toUpperCase(), creator);
    }

    public Piece create(String name, PieceColor color) {
        PieceCreator creator = creators.get(name.toUpperCase());
        if (creator == null) {
            throw new IllegalArgumentException("Nie ma figury o nazwie " + name);
        }
        return creator.create(color);
    }

    @FunctionalInterface
    public interface PieceCreator {
        Piece create(PieceColor color);
    }
}
