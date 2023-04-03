class Board {
    private final Piece[][] spaces;
    public static final int LENGTH = 4;

    Board() {
        spaces = new Piece[LENGTH][LENGTH];
    }

    public Piece[][] getSpaces() {
        return spaces;
    }


    public boolean isEmpty(int row, int column) {
        return row >= 0 && row < LENGTH && column >= 0 && column < LENGTH && spaces[row][column] == null;
    }

    public boolean isOccupied(int row, int column) {
        return row >= 0 && row < LENGTH && column >= 0 && column < LENGTH && spaces[row][column] != null;
    }

    public void put(Piece piece, int row, int column) throws RuntimeException {
        if (isOccupied(row, column)) {
            throw new RuntimeException("Can't put a piece on occupied space. make sure to check with isLegit()");
        }
        spaces[row][column] = piece;
    }

    private boolean hasCommon(int[][] loc) {
        byte and = 0b1111, or = 0b0000;
        for (int[] ints : loc) {
            if (isEmpty(ints[0], ints[1])) return false;
            int type = spaces[ints[0]][ints[1]].getType();
            and &= type;
            or |= type;
        }

        return and != 0b0000 || or != 0b1111;
    }

    public boolean isAligned() {
        // 縦と横
        int[][] h_loc = new int[LENGTH][2];
        int[][] v_loc = new int[LENGTH][2];
        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < LENGTH; j++) {
                h_loc[j][0] = v_loc[j][1] = i;
                h_loc[j][1] = v_loc[j][0] = j;
            }
            if (hasCommon(h_loc) || hasCommon(v_loc)) return true;
        }

        // 斜め
        int[][] d1_loc = new int[LENGTH][2];
        int[][] d2_loc = new int[LENGTH][2];
        for (int i = 0; i < LENGTH; i++) {
            d1_loc[i][0] = d1_loc[i][1] = i;
            d2_loc[i][0] = i;
            d2_loc[i][1] = LENGTH - i - 1;
        }

        return hasCommon(d1_loc) || hasCommon(d2_loc);
    }
}
