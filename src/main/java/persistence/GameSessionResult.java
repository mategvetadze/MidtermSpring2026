package persistence;

import java.time.Instant;
import java.util.List;

/** Final session data persisted after all rounds finish. */
public record GameSessionResult(
        Instant startedAt,
        Instant completedAt,
        List<String> playerNames,
        int[] finalScores,
        List<RoundResult> rounds) {}
