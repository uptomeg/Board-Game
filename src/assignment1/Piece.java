package assignment1;

public abstract class Piece implements Observer{

    /**
     * All possible Piece types
     * Musketeer and Guard
     */
    public enum Type {
        MUSKETEER("MUSKETEER"),
        GUARD("GUARD");

        private final String type;
        Type(final String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
    }

    private String symbol;
    private Type type;

    /**
     * Construct a new Piece
     * @param symbol to represent the Piece
     * @param type a Type of Piece (Musketeer or Guard)
     */
    public Piece(String symbol, Type type) {
        this.symbol = symbol;
        this.type = type;
    }

    /**
     * @return symbol representation of Piece
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @return the Type of piece (Musketeer or Guard)
     */
    public Type getType() {
        return type;
    }

    /**
     * @param cell a Cell to check conditions of
     * @return whether this Piece can move onto a given cell
     */
    public abstract boolean canMoveOnto(Cell cell);

    public void update(String sendtype) {
        this.symbol = sendtype;
    }
}
