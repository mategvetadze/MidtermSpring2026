package persistence;

import java.time.Instant;

/** Highest-score row across all persisted games. */
public record HighScoreEntry(String playerName, int score, long gameId, Instant completedAt) {}
