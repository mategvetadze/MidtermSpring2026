package persistence;

import java.time.Instant;

/** Summary row for recent-games report. */
public record RecentGameSummary(
        long gameId,
        Instant completedAt,
        String winnerName,
        int roundsPlayed,
        String scoreLine) {}
