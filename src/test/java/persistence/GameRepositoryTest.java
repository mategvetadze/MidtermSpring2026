package persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameRepositoryTest {

    private GameRepository repository;

    @BeforeEach
    void setUp() {
        PersistenceConfig.resetForTests(
                "jdbc:h2:mem:uno_test_" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
        repository = new GameRepository();
    }

    @AfterEach
    void tearDown() {
        PersistenceConfig.close();
    }

    @Test
    void savesSessionWithPlayersRoundsScoresAndWinner() {
        Instant started = Instant.parse("2026-03-01T12:00:00Z");
        Instant completed = Instant.parse("2026-03-01T12:30:00Z");
        GameSessionResult session =
                new GameSessionResult(
                        started,
                        completed,
                        List.of("You", "Bot1", "Bot2"),
                        new int[] {120, 45, 0},
                        List.of(
                                new RoundResult("Bot1", 45, Instant.parse("2026-03-01T12:10:00Z")),
                                new RoundResult("You", 120, Instant.parse("2026-03-01T12:25:00Z"))));

        long gameId = repository.saveSession(session);

        assertTrue(gameId > 0);
        List<RecentGameSummary> recent = repository.findRecentGames(5);
        assertEquals(1, recent.size());
        assertEquals("You", recent.get(0).winnerName());
        assertEquals(2, recent.get(0).roundsPlayed());
        assertTrue(recent.get(0).scoreLine().contains("You=120"));
    }

    @Test
    void reportsPlayerWinCounts() {
        saveSampleGame(List.of("Alice", "Bob"), new int[] {80, 20});
        saveSampleGame(List.of("Alice", "Bob"), new int[] {10, 90});
        saveSampleGame(List.of("Alice", "Bob"), new int[] {50, 30});

        List<PlayerWinCount> wins = repository.findPlayerWinCounts();
        assertFalse(wins.isEmpty());
        assertEquals("Alice", wins.get(0).playerName());
        assertEquals(2L, wins.get(0).wins());
    }

    @Test
    void reportsHighestScores() {
        saveSampleGame(List.of("Alice", "Bob"), new int[] {150, 10});
        saveSampleGame(List.of("Alice", "Bob"), new int[] {5, 200});

        List<HighScoreEntry> top = repository.findHighestScores(3);
        assertEquals(3, top.size());
        assertEquals(200, top.get(0).score());
        assertEquals("Bob", top.get(0).playerName());
    }

    private void saveSampleGame(List<String> players, int[] scores) {
        String roundWinner = players.get(0);
        repository.saveSession(
                new GameSessionResult(
                        Instant.now(),
                        Instant.now(),
                        players,
                        scores,
                        List.of(new RoundResult(roundWinner, scores[0], Instant.now()))));
    }
}
