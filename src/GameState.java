import java.util.ArrayList;

/**
 * Mutable state for an in-progress or completed UNO session.
 */
public class GameState {
    public final ArrayList<String> playerNames = new ArrayList<>();
    public final ArrayList<Boolean> humanPlayers = new ArrayList<>();
    public final ArrayList<ArrayList<String>> hands = new ArrayList<>();
    public int[] scores = new int[10];
    public int currentPlayer = 0;
    public int direction = 1;
    public String upCard = "";
    public String calledColor = "";

    public void setupPlayers(int bots, boolean human) {
        playerNames.clear();
        humanPlayers.clear();
        hands.clear();
        if (human) {
            playerNames.add("You");
            humanPlayers.add(Boolean.TRUE);
            hands.add(new ArrayList<>());
        }
        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(Boolean.FALSE);
            hands.add(new ArrayList<>());
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

    public void clearHands() {
        for (ArrayList<String> hand : hands) {
            hand.clear();
        }
    }
}
