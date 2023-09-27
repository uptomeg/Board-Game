package assignment1;

import java.util.ArrayList;

public class BoardBackupManager {
    public ArrayList<BoardBackup> boardBackups;

    public BoardBackupManager() {
        boardBackups = new ArrayList<>();
    }

    public void addBackup(BoardBackup backup) {
        boardBackups.add(backup);
    }

    public BoardBackup getLatestBackup() {
        int latestIndex = boardBackups.size() - 1;
        BoardBackup backup = boardBackups.get(latestIndex);
        boardBackups.remove(latestIndex);
        return backup;
    }
}
