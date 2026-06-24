import java.util.ArrayList;

/**
 * Mutable state for an in-progress or completed UNO session.
 */
public class GameState {
    public static final int DEFAULT_TARGET_SCORE = 500;

    public final ArrayList<String> playerNames = new ArrayList<>();
    public final ArrayList<Boolean> humanPlayers = new ArrayList<>();
    public final ArrayList<ArrayList<String>> hands = new ArrayList<>();
    public int[] scores = new int[10];
    public int currentPlayer = 0;
    public int direction = 1;
    public String upCard = "";
    public String calledColor = "";
    public int targetScore = DEFAULT_TARGET_SCORE;
  /** Player has one card and has not called UNO since reaching one card. */
    public final ArrayList<Boolean> vulnerableUno = new ArrayList<>();

    public void setupPlayers(int bots, boolean human) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();
        vulnerableUno.clear();
        if (human) {
            playerNames.add("You");
            humanPlayers.add(Boolean.TRUE);
            hands.add(new ArrayList<>());
            vulnerableUno.add(Boolean.FALSE);
        }
        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(Boolean.FALSE);
            hands.add(new ArrayList<>());
            vulnerableUno.add(Boolean.FALSE);
        }
    }

    public int playerCount() {
        return playerNames.size();
    }

    public String currentPlayerName() {
        return playerNames.get(currentPlayer);
    }

    public ArrayList<String> currentHand() {
        return hands.get(currentPlayer);
    }

    public boolean isCurrentPlayerHuman() {
        return humanPlayers.get(currentPlayer).booleanValue();
    }

    public boolean isHuman(int playerIndex) {
        return humanPlayers.get(playerIndex).booleanValue();
    }

    public void clearHands() {
        for (ArrayList<String> hand : hands) {
            hand.clear();
        }
        for (int i = 0; i < vulnerableUno.size(); i++) {
            vulnerableUno.set(i, Boolean.FALSE);
        }
    }

    public int matchWinnerIndex() {
        for (int i = 0; i < playerCount(); i++) {
            if (scores[i] >= targetScore) {
                return i;
            }
        }
        return -1;
    }

    public boolean isMatchOver() {
        return matchWinnerIndex() >= 0;
    }
}
