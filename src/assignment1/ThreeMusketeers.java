package assignment1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import assignment1.Exceptions.InvalidMoveException;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class ThreeMusketeers {

    private Board board;
    private Agent musketeerAgent, guardAgent;
    private final Scanner scanner = new Scanner(System.in);
    private List<String> agents = new ArrayList<>();
    private BoardBackupManager backupManager = new BoardBackupManager();
    private static ThreeMusketeers instance = null;
    private Collection<String> apple = new ArrayList<>();
    private Boolean a = false;

    // All possible game modes
    public enum GameMode {
        Human("Human vs Human"),
        HumanRandom("Human vs Computer (Random)"),
        HumanGreedy("Human vs Computer (Greedy)"),
        HumanGrowth("Human vs Computer (Growth)");

        private final String gameMode;
        GameMode(final String gameMode) {
            this.gameMode = gameMode;
        }
    }

    public static ThreeMusketeers getInstance() {
        if (instance == null) {
            instance = new ThreeMusketeers();
        }
        return instance;
    }

    public static ThreeMusketeers getInstance(String boardFilePath) {
        if (instance == null) {
            instance = new ThreeMusketeers(boardFilePath);
        }
        return instance;
    }

    /**
     * Default constructor to load Starter board
     */
    private ThreeMusketeers() {
        this.board = new Board();
    }

    /**
     * Constructor to load custom board
     * @param boardFilePath filepath of custom board
     */
    private ThreeMusketeers(String boardFilePath) {
        this.board = new Board(boardFilePath);
    }

    /**
     * Play game with human input mode selector
     */
    public void play(){
        System.out.println("Welcome! \n");
        System.out.println("Do you want to log in or play as a vistor? If you want to log in, enter'L', if not, enter 'N':");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine().strip();
        while (!answer.trim().equals("L") & !answer.trim().equals("N")) {
            System.out.print("Invalid option. Enter 'L' or 'N': ");
            answer = scanner.next();
        }
        if(answer.trim().equals("L")) {
        	System.out.println("Enter your name:");
        	String name = scanner.nextLine();
        	System.out.println("Enter your password:");
        	String password = scanner.nextLine();
        	while ((!name.trim().equals("applepear" ))| (!password.trim().equals("110"))) {
                System.out.print("Invalid name or password, please try again.");
                System.out.println("Enter your name:");
                name = scanner.next();
                System.out.println("Enter your password:");
                password = scanner.next();
            }
        	a = true;
        	getsource("Boards/source.txt");
        	ConcreteVisitor visitor = new ConcreteVisitor();
        	visitor.visitCollection(this.apple);
        	int numbers = visitor.getIt();
        	System.out.println("You have played " + numbers + " times of games.");
        	final GameMode mode = getModeInput();
            System.out.println("Playing " + mode.gameMode);
            play(mode);
        }
        else if(answer.trim().equals("N")) {
            final GameMode mode = getModeInput();
            System.out.println("Playing " + mode.gameMode);
            play(mode);}
    }

    private void getsource(String filePath) {
        File file = new File(filePath);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.printf("File at %s not found.", filePath);
            System.exit(1);
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().strip();
            apple.add(line);
        }
        scanner.close();
    }

    /**
     * Play game without human input mode selector
     * @param mode the GameMode to run
     */
    public void play(GameMode mode){
        selectMode(mode);
        runGame();
    }

    /**
     * Mode selector sets the correct agents based on the given GameMode
     * @param mode the selected GameMode
     */
    private void selectMode(GameMode mode) {
        switch (mode) {
            case Human -> {
                musketeerAgent = new HumanAgent(board);
                guardAgent = new HumanAgent(board);
            }
            case HumanRandom -> {
                String side = getSideInput();
                
                // The following statement may look weird, but it's what is known as a ternary statement.
                // Essentially, it sets musketeerAgent equal to a new HumanAgent if the value M is entered,
                // Otherwise, it sets musketeerAgent equal to a new RandomAgent
                musketeerAgent = side.equals("M") ? new HumanAgent(board) : new RandomAgent(board);
                
                guardAgent = side.equals("G") ? new HumanAgent(board) : new RandomAgent(board);
            }
            case HumanGreedy -> {
                String side = getSideInput();
                musketeerAgent = side.equals("M") ? new HumanAgent(board) : new GreedyAgent(board);
                guardAgent = side.equals("G") ? new HumanAgent(board) : new GreedyAgent(board);
            }
            case HumanGrowth -> {
                String side = getSideInput();
                musketeerAgent = side.equals("M") ? new HumanAgent(board) : new GrowthingAgent(board, 0);
                guardAgent = side.equals("G") ? new HumanAgent(board) : new GrowthingAgent(board, 0);
            }
        }
    }

    /**
     * Runs the game, handling human input for move actions
     * Handles moves for different agents based on current turn 
     */
    private void runGame() {
        while(!board.isGameOver()) {
          Count round = new Count(agents, "MUSKETEER");
          int count = round.count();

          System.out.println("\n" + board);

          Agent currentAgent;
          if (board.getTurn() == Piece.Type.MUSKETEER)
              currentAgent = musketeerAgent;
          else
              currentAgent = guardAgent;

          if (currentAgent instanceof HumanAgent) // Human move
                switch (getInputOption()) {
                    case "M":
                    	agents.add(board.getTurn().toString());
                        move(currentAgent);
                        break;
                    case "U":
                        if (backupManager.boardBackups.size() == 0) {
                            System.out.println("No moves to undo.");
                            continue;
                        }
                        else if (backupManager.boardBackups.size() == 1 || isHumansPlaying()) {
                            undoMove();
                        }
                        else {
                            undoMove();
                            undoMove();
                        }
                        break;
                    case "S":
                        board.saveBoard();
                        break;
                    case "C":
                        Count c = new Count(agents, "GUARD");
                        String s = String.valueOf(c.count());
                        System.out.println("You are in the " + s +  " round.");
                        break;
                    case "R":
                    	int min = 0;
                        int max = 2;
                        int storeAgent;
                        int ran_int = (int)Math.floor(Math.random()*(max-min+1)+min);
                        
                        if (currentAgent.equals(musketeerAgent)) {
                        	storeAgent = 0;
                        }else {
                        	storeAgent = 1;
                        }
                        
                        if (ran_int == 0) {
                        	currentAgent = new RandomAgent(currentAgent.board);
                    	}
                    	else if (ran_int == 1) {
                    		currentAgent = new GreedyAgent(currentAgent.board);
                    	}
                    	else if (ran_int == 2) {
                    		currentAgent = new GrowthingAgent(currentAgent.board, 0);
                    	}
                        System.out.printf("[%s] Calculating move...\n", currentAgent.getClass().getSimpleName());
                        agents.add(board.getTurn().toString());
                        if(currentAgent instanceof GrowthingAgent) {
                        	move(new GrowthingAgent(board, count));
                        }else {
                        	move(currentAgent);
                        }

                        if (storeAgent == 0) {
                        	musketeerAgent = new HumanAgent(currentAgent.board);
                        }else {
                        	guardAgent = new HumanAgent(currentAgent.board);
                        }

                }
            else { // Computer move
                System.out.printf("[%s] Calculating move...\n", currentAgent.getClass().getSimpleName());

                agents.add(board.getTurn().toString());
                if(currentAgent instanceof GrowthingAgent) {
                	move(new GrowthingAgent(board, count));
                }else {
                	move(currentAgent);
                }
            }
        }

        System.out.println(board);
        System.out.printf("\n%s won!%n", board.getWinner().getType());
        if(a == true) {
        	try{
        	      String data = "A";

        	      File file =new File("Boards/source.txt");

        	      //true = append file
        	      FileWriter fileWritter = new FileWriter(file.getName(),true);
        	      fileWritter.write(data+"\t\n");
        	      fileWritter.close();

        	      System.out.println("Your times of play has been recorded.");

        	     }catch(IOException e){
        	      e.printStackTrace();
        	     }
        	    }

    }

    /**
     * Gets a move from the given agent, adds a copy of the move using the copy constructor to the moves stack, and
     * does the move on the board.
     * @param agent Agent to get the move from.
     */
    protected void move(final Agent agent) {
        final Move move = agent.getMove();
        backupManager.addBackup(backupBoard());
        board.move(move);
    }

    public BoardBackup backupBoard() {
        BoardBackup backup = new BoardBackup(new Board(board));
        return backup;
    }

    /**
     * Removes a move from the top of the moves stack and undoes the move on the board.
     */
    private void undoMove() {
        BoardBackup latestState = backupManager.getLatestBackup();
        restoreFromBackup(latestState);
        System.out.println("Undid the previous move.");
    }

    public void restoreFromBackup(BoardBackup backup) {
        board = backup.getSavedBoard();
        board.checkDangerous();
    }

    /**
     * Get human input for move action
     * @return the selected move action, 'M': move, 'U': undo, and 'S': save
     */
    private String getInputOption() {
        System.out.printf("[%s] Enter 'M' to move, 'R' to let robot help moving, 'U' to undo, 'C' to count, and 'S' to save: ", board.getTurn().getType());
        while (!scanner.hasNext("[MRUSCmrusc]")) {
            System.out.print("Invalid option. Enter 'M', 'R', 'U', 'C', or 'S': ");
            scanner.next();
        }
        return scanner.next().toUpperCase();
    }

    /**
     * Returns whether both sides are human players
     * @return True if both sides are Human, False if one of the sides is a computer
     */
    private boolean isHumansPlaying() {
        return musketeerAgent instanceof HumanAgent && guardAgent instanceof HumanAgent;
    }

    /**
     * Get human input for side selection
     * @return the selected Human side for Human vs Computer games,  'M': Musketeer, G': Guard
     */
    private String getSideInput() {
        System.out.print("Enter 'M' to be a Musketeer or 'G' to be a Guard: ");
        while (!scanner.hasNext("[MGmg]")) {
            System.out.println("Invalid option. Enter 'M' or 'G': ");
            scanner.next();
        }
        return scanner.next().toUpperCase();
    }

    /**
     * Get human input for selecting the game mode
     * @return the chosen GameMode
     */
    private GameMode getModeInput() {
        System.out.println("""
                    0: Human vs Human
                    1: Human vs Computer (Random)
                    2: Human vs Computer (Greedy)
                    3: Human vs Computer (Growth)""");
        System.out.print("Choose a game mode to play i.e. enter a number: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid option. Enter 0, 1, or 2: ");
            scanner.next();
        }
        final int mode = scanner.nextInt();
        if (mode < 0 || mode > 3) {
            System.out.println("Invalid option.");
            return getModeInput();
        }
        return GameMode.values()[mode];
    }

    public static void main(String[] args) {
        String boardFileName = "Boards/Starter.txt";
        ThreeMusketeers game = ThreeMusketeers.getInstance(boardFileName);
        game.play();
    }
}
