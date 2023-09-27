package assignment1;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import assignment1.Observer;

public class Board {
    public int size = 5;
    // 2D Array of Cells for representation of the game board
    public final Cell[][] board = new Cell[size][size];

    private Piece.Type turn;
    private Piece.Type winner;

    /**
     * Create a Board with the current player turn set.
     */
    public Board() {
        this.loadBoard("Boards/Starter.txt");
    }

    /**
     * Create a Board with the current player turn set and a specified board.
     * @param boardFilePath The path to the board file to import (e.g. "Boards/Starter.txt")
     */
    public Board(String boardFilePath) {
        this.loadBoard(boardFilePath);
    }

    /**
     * Creates a Board copy of the given board.
     * @param board Board to copy
     */
    public Board(Board board) {
        this.size = board.size;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                this.board[row][col] = new Cell(board.board[row][col]);
            }
        }
        this.turn = board.turn;
        this.winner = board.winner;
    }

    /**
     * @return the Piece.Type (Musketeer or Guard) of the current turn
     */
    public Piece.Type getTurn() {
        return turn;
    }

    public void setTurn(Piece.Type turn) {
        this.turn = turn;
    }

    public Cell getCell(Coordinate coordinate) {
        return this.board[coordinate.row][coordinate.col];
    }

    public Piece.Type getWinner() {
        return winner;
    }

    /**
     * Gets all the Musketeer cells on the board.
     * @return List of cells
     */
    public List<Cell> getMusketeerCells() {
        return getAllCells()
                .stream()
                .filter(cell -> cell.hasPiece() && cell.getPiece().getType() == Piece.Type.MUSKETEER)
                .toList();
    }

    /**
     * Gets all the Guard cells on the board.
     * @return List of cells
     */
    public List<Cell> getGuardCells() {
        return getAllCells()
                .stream()
                .filter(cell -> cell.hasPiece() && cell.getPiece().getType() == Piece.Type.GUARD)
                .toList();
    }

    /**
     * Executes the given move on the board.
     * @param move a valid move
     */
    public void move(Move move) {
        Piece piece = move.fromCell.getPiece();
        move.toCell.setPiece(piece);
        move.fromCell.removePiece();
        changeTurn();
        checkDangerous();
        notifyObservers();
    }

    /**
     * Undo the move given.
     * @param move Copy of a move that was done and needs to be undone. The move copy has the correct piece info in the
     *             from and to cell fields.
     */
    public void undoMove(Move move) {
        Cell fromCell = getCell(move.fromCell.getCoordinate());
        Cell toCell = getCell(move.toCell.getCoordinate());
        fromCell.setPiece(move.fromCell.getPiece());
        toCell.setPiece(move.toCell.getPiece());
        changeTurn();
        checkDangerous();
        notifyObservers();
    }

    /**
     * Checks if the given move is valid.
     * @param move a move
     * @return     True, if the move is valid, false otherwise
     */
    public Boolean isValidMove(Move move) {
        Cell fromCell = move.fromCell;
        Coordinate fromCoordinate = fromCell.getCoordinate();
        Coordinate toCoordinate = move.toCell.getCoordinate();

        if (!isNextTo(fromCoordinate, toCoordinate)) return false;
        if (!onBoard(toCoordinate)) return false;

        return fromCell.getPiece().canMoveOnto(move.toCell);
    }

    /**
     * Get all the possible cells that have pieces that can be moved this turn.
     * @return      Cells that can be moved from the given cells
     */
    public List<Cell> getPossibleCells() {
        List<Cell> allCellsThisTurn = getTurn() == Piece.Type.MUSKETEER ? getMusketeerCells() : getGuardCells();
        List<Cell> possibleCells = new ArrayList<>();
        for (Cell cell : allCellsThisTurn) {
            if (!getPossibleDestinations(cell).isEmpty())
                possibleCells.add(cell);
        }
        return possibleCells;
    }

    /**
     * Get all the possible cell destinations that is possible to move to from the fromCell.
     * @param fromCell The cell that has the piece that is going to be moved
     * @return List of cells that are possible to get to
     */
    public List<Cell> getPossibleDestinations(Cell fromCell) {
        List<Cell> destinations = new ArrayList<>();
        int[][] possibleMoves = {{-1,0}, {0,1}, {1,0}, {0,-1}};

        for (int[] move: possibleMoves) {
            Coordinate oldCoordinate = fromCell.getCoordinate();
            int row = move[0] + oldCoordinate.row;
            int col = move[1] + oldCoordinate.col;
            Coordinate newCoordinate = new Coordinate(row, col);
            if (!onBoard(newCoordinate)) continue;

            Cell toCell = getCell(newCoordinate);
            if (isValidMove(new Move(fromCell, toCell)))
                destinations.add(toCell);
        }
        return destinations;
    }

    /**
     * Get all the possible moves that can be made this turn.
     * @return List of moves that can be made this turn
     */
    public List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        List<Cell> possibleCells = this.getPossibleCells();
        for (Cell fromCell: possibleCells) {
            List<Cell> possibleDestinations = this.getPossibleDestinations(fromCell);
            for (Cell toCell : possibleDestinations) {
                moves.add(new Move(fromCell, toCell));
            }
        }
        return moves;
    }

    /**
     * Checks if the game is over and sets the winner if there is one.
     * @return True, if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        if (inSameRowOrSameCol(getMusketeerCells())) {
            winner = Piece.Type.GUARD;
            return true;
        }
        if (getPossibleCells().isEmpty()) {
            winner = Piece.Type.MUSKETEER;
            return true;
        }
        return false;
    }

    /**
     * Saves the current board state to the boards directory
     */
    public void saveBoard() { ;
        String filePath = String.format("boards/%s.txt",
                new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
        File file = new File(filePath);

        try {
            file.createNewFile();
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(turn.getType() + "\n");
            for (Cell[] row: board) {
                StringBuilder line = new StringBuilder();
                for (Cell cell: row) {
                    if (cell.getPiece() != null) {
                        line.append(cell.getPiece().getSymbol());
                    } else {
                        line.append("_");
                    }
                    line.append(" ");
                }
                writer.write(line.toString().strip() + "\n");
            }
            writer.close();
            System.out.printf("Saved board to %s.\n", filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.printf("Failed to save board to %s.\n", filePath);
        }
    }

    @Override
    public String toString() {
        StringBuilder boardStr = new StringBuilder("  | A B C D E\n");
        boardStr.append("--+----------\n");
        for (int i = 0; i < size; i++) {
            boardStr.append(i + 1).append(" | ");
            for (int j = 0; j < size; j++) {
                Cell cell = board[i][j];
                boardStr.append(cell).append(" ");
            }
            boardStr.append("\n");
        }
        return boardStr.toString();
    }

    private List<Cell> getAllCells() {
        return Arrays.stream(board).flatMap(Arrays::stream).collect(Collectors.toList());
    }

    private Boolean onBoard(Coordinate coordinate) {
        return 0 <= coordinate.col && coordinate.col < this.size &&
                0 <= coordinate.row && coordinate.row < this.size;
    }

    private Boolean isNextTo(Coordinate fromCoordinate, Coordinate toCoordinate) {
        int xDiff = Math.abs(fromCoordinate.col - toCoordinate.col);
        int yDiff = Math.abs(fromCoordinate.row - toCoordinate.row);
        return (xDiff == 0 && yDiff == 1) || (xDiff == 1 && yDiff == 0) ;
    }

    private Boolean inSameRowOrSameCol(List<Cell> cells) {
        long numRows = cells.stream().map(cell -> cell.getCoordinate().row).distinct().count();
        long numCols = cells.stream().map(cell -> cell.getCoordinate().col).distinct().count();
        return numRows == 1 || numCols == 1;
    }

    private void changeTurn() {
        setTurn(getTurn() == Piece.Type.MUSKETEER ? Piece.Type.GUARD : Piece.Type.MUSKETEER);
    }

    /**
     * Loads a board file from a file path.
     * @param filePath The path to the board file to load (e.g. "Boards/Starter.txt")
     */
    private void loadBoard(String filePath) {
        File file = new File(filePath);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.printf("File at %s not found.", filePath);
            System.exit(1);
        }

        turn = Piece.Type.valueOf(scanner.nextLine().toUpperCase());

        int row = 0, col = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] pieces = line.trim().split(" ");
            for (String piece: pieces) {
                Cell cell = new Cell(new Coordinate(row, col));
                switch (piece) {
                    case "O" -> cell.setPiece(new Guard());
                    case "X" -> cell.setPiece(new Musketeer());
                    default -> cell.setPiece(null);
                }
                this.board[row][col] = cell;
                col += 1;
            }
            col = 0;
            row += 1;
        }
        scanner.close();
        System.out.printf("Loaded board from %s.\n", file.getPath());
    }

    private List<Observer> observers = new ArrayList<Observer>();

    public void checkDangerous() {
        List<Cell> allMusk = this.getMusketeerCells();
        Cell m1 = allMusk.get(0);
        Cell m2 = allMusk.get(1);
        Cell m3 = allMusk.get(2);
        detachDangerous(m1.getPiece());
        detachDangerous(m2.getPiece());
        detachDangerous(m3.getPiece());

        if(m1.getCoordinate().row == m2.getCoordinate().row || m1.getCoordinate().col == m2.getCoordinate().col){
            attachDangerous(m1.getPiece());
            attachDangerous(m2.getPiece());
        }else if(m1.getCoordinate().row == m3.getCoordinate().row || m1.getCoordinate().col == m3.getCoordinate().col){
            attachDangerous(m1.getPiece());
            attachDangerous(m3.getPiece());
        }else if(m2.getCoordinate().row == m3.getCoordinate().row || m2.getCoordinate().col == m3.getCoordinate().col){
            attachDangerous(m2.getPiece());
            attachDangerous(m3.getPiece());
        }
    }

    public void attachDangerous(Piece musk) {
        observers.add(musk);
    }

    public void detachDangerous(Piece musk) {
        observers.remove((Observer) musk);
        musk.update("X");
    }

    public void notifyObservers() {
        for(Observer o:observers) {
            o.update("D");
        }
    }
}
