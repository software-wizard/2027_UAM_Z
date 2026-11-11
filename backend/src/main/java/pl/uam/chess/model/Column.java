package pl.uam.chess.model;

public enum Column {
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H;

    public int stepsTo(Column other) {
        return other.ordinal() - ordinal();
    }

    public Column shift(int steps) {
        int next = ordinal() + steps;
        if (next < 0 || next >= values().length) {
            return null;
        }
        return values()[next];
    }
}
