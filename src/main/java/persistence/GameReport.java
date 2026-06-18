package persistence;

import java.util.List;

/** Prints persisted game history and statistics to the console. */
public final class GameReport {

    private final GameRepository repository;

    public GameReport(GameRepository repository) {
        this.repository = repository;
    }

    public void printRecentGames(int limit) {
        List<RecentGameSummary> games = repository.findRecentGames(limit);
        if (games.isEmpty()) {
            System.out.println("No saved games yet.");
            return;
        }
        System.out.println("Recent games (newest first):");
        for (RecentGameSummary game : games) {
            System.out.println(
                    "#"
                            + game.gameId()
                            + " completed "
                            + game.completedAt()
                            + " | winner "
                            + game.winnerName()
                            + " | rounds "
                            + game.roundsPlayed()
                            + " | scores "
                            + game.scoreLine());
        }
    }

    public void printPlayerWinCounts() {
        List<PlayerWinCount> counts = repository.findPlayerWinCounts();
        if (counts.isEmpty()) {
            System.out.println("No saved games yet.");
            return;
        }
        System.out.println("Player win counts:");
        for (PlayerWinCount count : counts) {
            System.out.println(count.playerName() + ": " + count.wins());
        }
    }

    public void printHighestScores(int limit) {
        List<HighScoreEntry> entries = repository.findHighestScores(limit);
        if (entries.isEmpty()) {
            System.out.println("No saved games yet.");
            return;
        }
        System.out.println("Highest scores:");
        for (HighScoreEntry entry : entries) {
            System.out.println(
                    entry.playerName()
                            + " scored "
                            + entry.score()
                            + " in game #"
                            + entry.gameId()
                            + " ("
                            + entry.completedAt()
                            + ")");
        }
    }
}
