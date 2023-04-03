class Piece {
    private final int type;
    private final String color;
    private final String shape;
    private final String size;
    private final String filling;

    Piece(int piece_type) {
        type = piece_type;
        color = ((type & 0b1000) != 0) ? "Dark" : "Light";
        shape = ((type & 0b0100) != 0) ? "Round" : "Square";
        size = ((type & 0b0010) != 0) ? "Tall" : "Short";
        filling = ((type & 0b0001) != 0) ? "Solid" : "Hollow";
    }

    public int getType() {
        return type;
    }

    public String feature() {
        return color + ", " + shape + ", " + size + ", " + filling;
    }

    @Override
    public String toString() {
        return String.format("%04d", Integer.parseInt(Integer.toBinaryString(type)));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Piece && type == ((Piece) obj).getType();
    }
}
