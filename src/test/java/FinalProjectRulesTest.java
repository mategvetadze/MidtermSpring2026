import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinalProjectRulesTest {

    private GameState state;
    private Deck deck;
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        state = new GameState();
        state.setupPlayers(2, false);
        deck = new Deck();
        engine = new GameEngine(state, deck);
    }

    @Test
    void standardDeckHas108CardsWithExpectedComposition() {
        ArrayList<String> cards = Deck.buildStandardDeck();
        assertEquals(Deck.STANDARD_DECK_SIZE, cards.size());
        assertTrue(Deck.isStandardComposition(cards));
    }

    @Test
    void wildDrawFourIsIllegalWhenAnotherCardCanBePlayed() {
        ArrayList<String> hand = new ArrayList<>();
        hand.add("W4");
        hand.add("R3");
        assertFalse(GameRules.isWildDrawFourLegal("W4", hand, "R5", ""));
        assertTrue(GameRules.isWildDrawFourLegal("R3", hand, "R5", ""));
    }

    @Test
    void wildDrawFourIsLegalWhenNoOtherPlayExists() {
        ArrayList<String> hand = new ArrayList<>();
        hand.add("W4");
        hand.add("B9");
        assertTrue(GameRules.isWildDrawFourLegal("W4", hand, "R5", ""));
    }

    @Test
    void missedUnoPenaltyDrawsTwoCards() {
        state.setupPlayers(1, true);
        state.currentPlayer = 0;
        state.upCard = "R4";
        state.hands.get(0).add("R7");
        state.hands.get(0).add("R2");
        deck.getDrawPile().clear();
        deck.getDrawPile().add("G1");
        deck.getDrawPile().add("G2");

        engine.playChosenCard(0, null, new ArrayList<>());
        assertTrue(state.vulnerableUno.get(0));

        ArrayList<String> events = engine.applyMissedUnoPenalties();
        assertEquals(3, state.hands.get(0).size());
        assertFalse(state.vulnerableUno.get(0));
        assertEquals(1, events.size());
    }

    @Test
    void callingUnoPreventsPenalty() {
        state.setupPlayers(1, true);
        state.currentPlayer = 0;
        state.upCard = "R4";
        state.hands.get(0).add("R7");
        state.hands.get(0).add("R2");

        engine.playChosenCard(0, null, new ArrayList<>());
        engine.callUno(0);
        engine.applyMissedUnoPenalties();

        assertEquals(1, state.hands.get(0).size());
    }

    @Test
    void roundWinnerAddsOpponentCardValuesToScore() {
        state.currentPlayer = 0;
        state.upCard = "R5";
        state.hands.get(0).add("R3");
        state.hands.get(1).add("B9");
        state.hands.get(1).add("YS");

        GameEngine.TurnOutcome outcome = engine.playChosenCard(0, null, new ArrayList<>());

        assertEquals(GameEngine.TurnResult.WON, outcome.result);
        assertEquals(29, outcome.pointsScored);
        assertEquals(29, state.scores[0]);
    }

    @Test
    void matchEndsWhenTargetScoreReached() {
        state.targetScore = 100;
        state.scores[0] = 95;
        state.scores[1] = 20;

        state.currentPlayer = 0;
        state.upCard = "R5";
        state.hands.get(0).add("R3");
        state.hands.get(1).add("B9");

        engine.playChosenCard(0, null, new ArrayList<>());

        assertTrue(state.isMatchOver());
        assertEquals(0, state.matchWinnerIndex());
    }

    @Test
    void drawPassEndsTurnWithoutPlayingCard() {
        state.currentPlayer = 0;
        state.upCard = "R9";
        state.hands.get(0).add("B1");
        deck.getDrawPile().clear();
        deck.getDrawPile().add("B3");

        String drawn = engine.drawCard();
        state.hands.get(0).add(drawn);
        assertFalse(engine.isLegalPlay(drawn, state.hands.get(0)));

        engine.advancePlayer();
        assertEquals(1, state.currentPlayer);
        assertEquals(2, state.hands.get(0).size());
    }
}
