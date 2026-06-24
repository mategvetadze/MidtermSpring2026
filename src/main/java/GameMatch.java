import java.util.ArrayList;
import java.util.Random;
import persistence.RoundResult;

/**
 * Plays UNO rounds until a target score is reached.
 */
public class GameMatch {
    private final GameState state;
    private final ConsoleGame console;

    public GameMatch(GameState state, ConsoleGame console) {
        this.state = state;
        this.console = console;
    }

    public ArrayList<RoundResult> playToTarget(Random random) {
        ArrayList<RoundResult> rounds = new ArrayList<>();
        int roundNumber = 1;
        while (!state.isMatchOver()) {
            System.out.println(
                    "\n=== Round "
                            + roundNumber
                            + " (target "
                            + state.targetScore
                            + ") ===");
            printStandings();
            GameLog.gameStart(roundNumber, state.playerCount());
            RoundResult result = console.playRound(random, roundNumber);
            if (result != null) {
                rounds.add(result);
            } else {
                break;
            }
            roundNumber++;
        }
        return rounds;
    }

    public ArrayList<RoundResult> playRounds(Random random, int roundCount) {
        ArrayList<RoundResult> rounds = new ArrayList<>();
        for (int round = 1; round <= roundCount; round++) {
            System.out.println("\n=== Round " + round + " ===");
            GameLog.gameStart(round, state.playerCount());
            RoundResult result = console.playRound(random, round);
            if (result != null) {
                rounds.add(result);
            }
        }
        return rounds;
    }

    private void printStandings() {
        System.out.println("Current standings:");
        for (int i = 0; i < state.playerNames.size(); i++) {
            System.out.println(state.playerNames.get(i) + ": " + state.scores[i]);
        }
    }
}
