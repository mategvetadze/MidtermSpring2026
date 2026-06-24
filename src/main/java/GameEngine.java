import java.util.ArrayList;
import java.util.Random;

/**
 * Game rules and turn execution without console I/O.
 */
public class GameEngine {
    public static final int SAFETY_TURN_LIMIT = 3000;
    public static final int MISSED_UNO_PENALTY_CARDS = 2;

    public enum TurnResult {
        CONTINUE,
        WON
    }

    public static class TurnOutcome {
        public final TurnResult result;
        public final int pointsScored;
        public final ArrayList<String> events;

        public TurnOutcome(TurnResult result, int pointsScored, ArrayList<String> events) {
            this.result = result;
            this.pointsScored = pointsScored;
            this.events = events;
        }
    }

    private final GameState state;
    private final Deck deck;

    public GameEngine(GameState state, Deck deck) {
        this.state = state;
        this.deck = deck;
    }

    public GameState getState() {
        return state;
    }

    public Deck getDeck() {
        return deck;
    }

    public void startNewRound(Random random) {
        startNewGame(random);
    }

    public void startNewGame(Random random) {
        deck.setRandom(random);
        deck.buildAndShuffle();
        state.clearHands();

        for (int i = 0; i < state.playerCount(); i++) {
            for (int j = 0; j < 7; j++) {
                state.hands.get(i).add(deck.draw());
            }
        }

        state.upCard = deck.draw();
        while (state.upCard.startsWith("W")) {
            deck.discard(state.upCard);
            state.upCard = deck.draw();
        }
        state.calledColor = "";
        state.direction = 1;
        state.currentPlayer = random.nextInt(state.playerCount());
    }

    public String drawCard() {
        return deck.draw();
    }

    public boolean isLegalPlay(String card) {
        return isLegalPlay(card, state.currentHand());
    }

    public boolean isLegalPlay(String card, ArrayList<String> hand) {
        return GameRules.isWildDrawFourLegal(card, hand, state.upCard, state.calledColor);
    }

    public void advancePlayer() {
        state.currentPlayer += state.direction;
        if (state.currentPlayer >= state.playerCount()) {
            state.currentPlayer = 0;
        }
        if (state.currentPlayer < 0) {
            state.currentPlayer = state.currentPlayer + state.playerCount();
        }
    }

    public void callUno(int playerIndex) {
        if (playerIndex >= 0
                && playerIndex < state.playerCount()
                && state.hands.get(playerIndex).size() == 1) {
            state.vulnerableUno.set(playerIndex, Boolean.FALSE);
        }
    }

    public ArrayList<String> applyMissedUnoPenalties() {
        ArrayList<String> events = new ArrayList<>();
        for (int i = 0; i < state.playerCount(); i++) {
            if (state.vulnerableUno.get(i).booleanValue() && state.hands.get(i).size() == 1) {
                for (int c = 0; c < MISSED_UNO_PENALTY_CARDS; c++) {
                    String drawn = deck.draw();
                    state.hands.get(i).add(drawn);
                    GameLog.cardDrawn(state.playerNames.get(i), drawn);
                }
                state.vulnerableUno.set(i, Boolean.FALSE);
                events.add(
                        state.playerNames.get(i)
                                + " forgot UNO and draws "
                                + MISSED_UNO_PENALTY_CARDS
                                + ".");
            }
        }
        return events;
    }

    /**
     * Plays a card already in the current player's hand.
     */
    public TurnOutcome playChosenCard(int chosen, String calledColor, ArrayList<String> events) {
        if (events == null) {
            events = new ArrayList<>();
        }

        String name = state.currentPlayerName();
        ArrayList<String> hand = state.currentHand();

        if (chosen >= hand.size()) {
            String penalty = deck.draw();
            hand.add(penalty);
            GameLog.invalidInput(name, "invalid card index " + chosen);
            GameLog.cardDrawn(name, penalty);
            events.add(name + " selected an invalid index and draws a penalty card.");
            advancePlayer();
            return new TurnOutcome(TurnResult.CONTINUE, 0, events);
        }

        String card = hand.get(chosen);
        if (!isLegalPlay(card, hand)) {
            String penalty = deck.draw();
            hand.add(penalty);
            GameLog.invalidInput(name, "illegal card " + card);
            GameLog.cardDrawn(name, penalty);
            events.add(name + " tried illegal card " + card + " and draws a penalty card.");
            advancePlayer();
            return new TurnOutcome(TurnResult.CONTINUE, 0, events);
        }

        hand.remove(chosen);
        deck.discard(state.upCard);
        state.upCard = card;
        state.calledColor = "";
        GameLog.cardPlayed(name, card);
        events.add(name + " plays " + card);

        if (card.equals("W") || card.equals("W4")) {
            state.calledColor = calledColor != null ? calledColor : "";
            events.add(name + " calls " + state.calledColor);
        }

        if (hand.size() == 1) {
            if (state.isHuman(state.currentPlayer)) {
                state.vulnerableUno.set(state.currentPlayer, Boolean.TRUE);
            } else {
                state.vulnerableUno.set(state.currentPlayer, Boolean.FALSE);
                events.add(name + " says UNO!");
            }
        }

        if (hand.isEmpty()) {
            int points = scoreOpponents();
            state.scores[state.currentPlayer] += points;
            events.add(name + " wins the round and scores " + points);
            return new TurnOutcome(TurnResult.WON, points, events);
        }

        applyCardEffect(card, events);
        return new TurnOutcome(TurnResult.CONTINUE, 0, events);
    }

    public void applyCardEffect(String card, ArrayList<String> events) {
        String rank = GameRules.getCardRank(card);
        if (rank.equals("SKIP")) {
            advancePlayer();
            advancePlayer();
        } else if (rank.equals("REVERSE")) {
            state.direction = state.direction * -1;
            if (state.playerCount() == 2) {
                advancePlayer();
                advancePlayer();
            } else {
                advancePlayer();
            }
        } else if (rank.equals("DRAW_TWO")) {
            advancePlayer();
            ArrayList<String> targetHand = state.currentHand();
            String drawOne = deck.draw();
            targetHand.add(drawOne);
            GameLog.cardDrawn(state.currentPlayerName(), drawOne);
            String drawTwo = deck.draw();
            targetHand.add(drawTwo);
            GameLog.cardDrawn(state.currentPlayerName(), drawTwo);
            events.add(state.currentPlayerName() + " draws two.");
            advancePlayer();
        } else if (rank.equals("WILD_DRAW_FOUR")) {
            advancePlayer();
            ArrayList<String> targetHand = state.currentHand();
            for (int i = 0; i < 4; i++) {
                String drawn = deck.draw();
                targetHand.add(drawn);
                GameLog.cardDrawn(state.currentPlayerName(), drawn);
            }
            events.add(state.currentPlayerName() + " draws four.");
            advancePlayer();
        } else {
            advancePlayer();
        }
    }

    private int scoreOpponents() {
        int points = 0;
        for (int i = 0; i < state.hands.size(); i++) {
            if (i != state.currentPlayer) {
                for (int j = 0; j < state.hands.get(i).size(); j++) {
                    points += GameRules.getCardPoints(state.hands.get(i).get(j));
                }
            }
        }
        return points;
    }
}
