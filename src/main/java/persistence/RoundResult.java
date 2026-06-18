package persistence;

import java.time.Instant;

/** Data collected for one completed UNO round within a session. */
public record RoundResult(String winnerName, int pointsScored, Instant completedAt) {}
