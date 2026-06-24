import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Rubric-aligned JUnit tests for the final project rule menu.
 * Characterization coverage also exists in {@link GameTest}.
 */
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

    // --- 1.1 Correct Deck Composition (5 pts) ---

    @Test
    void deckHas108Cards() {
        ArrayList<String> cards = Deck.buildStandardDeck();
        assertEquals(Deck.STANDARD_DECK_SIZE, cards.size());
    }

    @Test
    void deckContainsFourColorsNumberedCardsAndActionCards() {
        ArrayList<String> cards = Deck.buildStandardDeck();
        assertTrue(Deck.isStandardComposition(cards));
        assertEquals(4, cards.stream().filter(c -> c.equals("W")).count());
        assertEquals(4, cards.stream().filter(c -> c.equals("W4")).count());
        assertEquals(8, cards.stream().filter(c -> c.endsWith("S") && c.length() == 2).count());
        assertEquals(8, cards.stream().filter(c -> c.endsWith("R") && c.length() == 2).count());
        assertEquals(8, cards.stream().filter(c -> c.endsWith("+2")).count());
        for (String color : new String[] {"R", "Y", "G", "B"}) {
            assertTrue(cards.contains(color + "0"));
            assertTrue(cards.contains(color + "5"));
        }
    }

    // --- 1.2 Legal Play Validation (7 pts) ---

    @Test
    void legalPlayMatchesColorNumberAndActionType() {
        assertTrue(GameRules.isLegalPlay("R5", "R9", ""));
        assertTrue(GameRules.isLegalPlay("G9", "R9", ""));
        assertTrue(GameRules.isLegalPlay("BS", "RS", ""));
        assertTrue(GameRules.isLegalPlay("B3", "W", "B"));
    }

    @Test
    void wildCardsArePlayableAndIllegalCardsAreRejected() {
        assertTrue(GameRules.isLegalPlay("W", "R5", ""));
        assertTrue(GameRules.isLegalPlay("W4", "G+2", ""));
        assertFalse(GameRules.isLegalPlay("R5", "B9", ""));

        state.currentPlayer = 0;
        state.upCard = "R5";
        state.hands.get(0).add("B3");
        state.hands.get(0).add("R2");
        deck.getDrawPile().clear();
        deck.getDrawPile().add("G1");
        int handBefore = state.hands.get(0).size();

        engine.playChosenCard(0, null, new ArrayList<>());

        assertEquals(handBefore + 1, state.hands.get(0).size());
        assertEquals(1, state.currentPlayer);
    }

    // --- 1.3 Skip (5 pts) ---

    @Test
    void skipMakesNextPlayerLoseTurnInThreePlayerGame() {
        state.setupPlayers(3, false);
        state.currentPlayer = 0;
        state.upCard = "R4";
        state.hands.get(0).add("RS");
        state.hands.get(0).add("R2");

        engine.playChosenCard(0, null, new ArrayList<>());

        assertEquals(2, state.currentPlayer);
    }

    // --- 1.4 Reverse (5 pts) ---

    @Test
    void reverseChangesDirectionForThreePlayers() {
        state.setupPlayers(3, false);
        state.currentPlayer = 1;
        state.direction = 1;
        state.upCard = "Y4";
        state.hands.get(1).add("YR");
        state.hands.get(1).add("G2");

        engine.playChosenCard(0, null, new ArrayList<>());

        assertEquals(-1, state.direction);
        assertEquals(0, state.currentPlayer);
    }

    @Test
    void reverseActsLikeSkipInTwoPlayerGame() {
        state.currentPlayer = 0;
        state.upCard = "R4";
        state.hands.get(0).add("RR");
        state.hands.get(0).add("R2");

        engine.playChosenCard(0, null, new ArrayList<>());

        assertEquals(0, state.currentPlayer);
    }

    // --- 1.5 Draw Two (5 pts) ---

    @Test
    void drawTwoAddsTwoCardsAndSkipsNextPlayer() {
        state.setupPlayers(3, false);
        state.currentPlayer = 0;
        state.upCard = "R4";
        state.hands.get(0).add("R+2");
        state.hands.get(0).add("R3");
        int nextHandBefore = state.hands.get(1).size();

        engine.playChosenCard(0, null, new ArrayList<>());

        assertEquals(nextHandBefore + 2, state.hands.get(1).size());
        assertEquals(2, state.currentPlayer);
    }

    // --- 1.6 Wild (5 pts) ---

    @Test
    void wildSetsCalledColorThatAffectsLegalPlay() {
        state.currentPlayer = 0;
        state.upCard = "R4";
        state.hands.get(0).add("W");
        state.hands.get(0).add("B2");

        engine.playChosenCard(0, "G", new ArrayList<>());

        assertEquals("G", state.calledColor);
        assertTrue(GameRules.isLegalPlay("G5", state.upCard, state.calledColor));
        assertFalse(GameRules.isLegalPlay("R5", state.upCard, state.calledColor));
    }

    // --- 1.7 Wild Draw Four (5 pts) ---

    @Test
    void wildDrawFourIsRestrictedWhenOtherPlaysExist() {
        ArrayList<String> hand = new ArrayList<>();
        hand.add("W4");
        hand.add("R3");
        assertFalse(GameRules.isWildDrawFourLegal("W4", hand, "R5", ""));
    }

    @Test
    void wildDrawFourDrawsFourSkipsTurnAndSetsColor() {
        state.setupPlayers(3, false);
        deck.getDrawPile().clear();
        for (int i = 0; i < 4; i++) {
            deck.getDrawPile().add("G" + i);
        }
        state.currentPlayer = 0;
        state.upCard = "R4";
        state.hands.get(0).add("W4");
        state.hands.get(0).add("B9");
        int nextHandBefore = state.hands.get(1).size();

        engine.playChosenCard(0, "B", new ArrayList<>());

        assertEquals(nextHandBefore + 4, state.hands.get(1).size());
        assertEquals(2, state.currentPlayer);
        assertEquals("B", state.calledColor);
    }

    // --- 1.8 Draw/Pass Behavior (5 pts) ---

    @Test
    void playerCanDrawAndPlayLegalDrawnCard() {
        state.currentPlayer = 0;
        state.upCard = "Y4";
        state.hands.get(0).add("B1");
        deck.getDrawPile().clear();
        deck.getDrawPile().add("Y8");

        String drawn = engine.drawCard();
        state.hands.get(0).add(drawn);
        assertTrue(engine.isLegalPlay(drawn, state.hands.get(0)));

        engine.playChosenCard(state.hands.get(0).size() - 1, null, new ArrayList<>());

        assertEquals("Y8", state.upCard);
    }

    @Test
    void playerCanDrawAndPassWhenDrawnCardIsNotPlayable() {
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

    // --- 1.9 UNO Call And Penalty (4 pts) ---

    @Test
    void oneCardStateIsDetectedAndMissedUnoDrawsPenalty() {
        state.setupPlayers(1, true);
        state.currentPlayer = 0;
        state.upCard = "R4";
        state.hands.get(0).add("R7");
        state.hands.get(0).add("R2");
        deck.getDrawPile().clear();
        deck.getDrawPile().add("G1");
        deck.getDrawPile().add("G2");

        engine.playChosenCard(0, null, new ArrayList<>());
        assertEquals(1, state.hands.get(0).size());
        assertTrue(state.vulnerableUno.get(0));

        engine.applyMissedUnoPenalties();
        assertEquals(3, state.hands.get(0).size());
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
    void botAutomaticallyCallsUnoAtOneCard() {
        state.currentPlayer = 0;
        state.upCard = "R4";
        state.hands.get(0).add("R7");
        state.hands.get(0).add("R2");

        ArrayList<String> events = new ArrayList<>();
        engine.playChosenCard(0, null, events);

        assertFalse(state.vulnerableUno.get(0));
        assertTrue(events.stream().anyMatch(e -> e.contains("says UNO!")));
    }

    // --- 1.10 Round Scoring And Multi-Round Target (4 pts) ---

    @Test
    void roundWinnerScoresRemainingOpponentCards() {
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
    void matchContinuesUntilTargetScoreAndDeterminesWinner() {
        state.targetScore = 100;
        state.scores[0] = 80;
        state.scores[1] = 40;

        state.currentPlayer = 0;
        state.upCard = "R5";
        state.hands.get(0).add("R3");
        state.hands.get(1).add("B9");
        state.hands.get(1).add("YS");

        engine.playChosenCard(0, null, new ArrayList<>());

        assertTrue(state.isMatchOver());
        assertEquals(0, state.matchWinnerIndex());
        assertEquals(109, state.scores[0]);
    }
}
