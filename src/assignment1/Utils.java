package assignment1;

import assignment1.Exceptions.*;

/**
 * Contains functions which are useful for validating user inputs
 */
public class Utils {

    public static char convertIntToLetter(int i) {
        return (char) (i + 64);
    }

    public static int convertCharIntToInt(char c) throws InvalidMoveException {
        int i = c - '0' - 1;
        if (i < 0 || i > 4) throw new InvalidMoveException("Enter a coordinate like 'A1'");
        return i;
    }

    public static int convertCharToInt(char c) throws InvalidMoveException {
        int i = c - 'A';
        if (i < 0 || i > 4) throw new InvalidMoveException("Enter a coordinate like 'A1'");
        return i;
    }

    public static Coordinate parseUserMove(String move) throws InvalidMoveException {
        if (move.length() != 2) throw new InvalidMoveException("Enter a coordinate like 'A1'");
        int row = convertCharIntToInt(move.charAt(1));
        int col = convertCharToInt(move.charAt(0));

        return new Coordinate(row, col);
    }
}
