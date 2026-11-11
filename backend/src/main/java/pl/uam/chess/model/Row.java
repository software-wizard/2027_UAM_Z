package pl.uam.chess.model;

public enum Row {
    R1,
    R2,
    R3,
    R4,
    R5,
    R6,
    R7,
    R8;

    public int number() {
        return ordinal() + 1;
    }

    public int stepsTo(Row other) {
        return other.ordinal() - ordinal();
    }

    public Row shift(int steps) {
        int next = ordinal() + steps;
        if (next < 0 || next >= values().length) {
            return null;
        }
        return values()[next];
    }

    public Row between(Row other) {
        return values()[(ordinal() + other.ordinal()) / 2];
    }
}
