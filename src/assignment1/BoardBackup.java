package assignment1;

public class BoardBackup {
    private Board currBoard;

    public BoardBackup(Board board) {
        currBoard = board;
    }

    public Board getSavedBoard() {
        return currBoard;
    }
}
