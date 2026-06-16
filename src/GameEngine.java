import java.util.ArrayList;
import java.util.Random;

/**
 * Game rules and turn execution without console I/O.
 */
public class GameEngine {
    public static final int SAFETY_TURN_LIMIT = 3000;

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
        return GameRules.isLegalPlay(card, state.upCard, state.calledColor);
    }

    public void advancePlayer() {
        state.currentPlayer += state.direction;
        if (state.currentPlayer >= state.playerCount()) {
            state.currentPlayer = 0;
        }
        if (state.currentPlayer < 0) {
            state.currentPlayer = state.playerCount() - 1;
        }
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
            hand.add(deck.draw());
            events.add(name + " selected an invalid index and draws a penalty card.");
            advancePlayer();
            return new TurnOutcome(TurnResult.CONTINUE, 0, events);
        }

        String card = hand.get(chosen);
        if (!isLegalPlay(card)) {
            hand.add(deck.draw());
            events.add(name + " tried illegal card " + card + " and draws a penalty card.");
            advancePlayer();
            return new TurnOutcome(TurnResult.CONTINUE, 0, events);
        }

        hand.remove(chosen);
        deck.discard(state.upCard);
        state.upCard = card;
        state.calledColor = "";
        events.add(name + " plays " + card);

        if (card.equals("W") || card.equals("W4")) {
            state.calledColor = calledColor != null ? calledColor : "";
            events.add(name + " calls " + state.calledColor);
        }

        if (hand.size() == 1) {
            events.add(name + " says UNO!");
        }

        if (hand.isEmpty()) {
            int points = scoreOpponents();
            state.scores[state.currentPlayer] += points;
            events.add(name + " wins and scores " + points);
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
            targetHand.add(deck.draw());
            targetHand.add(deck.draw());
            events.add(state.currentPlayerName() + " draws two.");
            advancePlayer();
        } else if (rank.equals("WILD_DRAW_FOUR")) {
            advancePlayer();
            ArrayList<String> targetHand = state.currentHand();
            for (int i = 0; i < 4; i++) {
                targetHand.add(deck.draw());
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
