import java.util.ArrayList;

/**
 * Characterization tests for the UNO game.
 * These tests document the current behavior before refactoring.
 */
public class GameTest {
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static boolean verboseTests = false;

    public static void main(String[] args) {
        System.out.println("Running characterization tests...");
        int failures = runTests();
        printSummary();
        if (failures > 0) {
            System.exit(1);
        }
    }

    /**
     * Runs all characterization tests and returns the failure count.
     * Used by the Maven test suite without terminating the JVM.
     */
    public static int runTests() {
        passedTests = 0;
        failedTests = 0;
        runAllTests();
        return failedTests;
    }

    private static void runAllTests() {
        // Card representation and parsing
        testCardColor();
        testCardRank();
        testCardNumber();
        testCardPoints();

        // Legal play matching - by color
        testLegalMatchColor();
        testIllegalMismatchColor();

        // Legal play matching - by number
        testLegalMatchNumber();
        testLegalMatchNumberOnWild();

        // Legal play matching - by action type
        testLegalMatchSkip();
        testLegalMatchReverse();
        testLegalMatchDrawTwo();

        // Wild card behavior
        testWildIsAlwaysLegal();
        testWildDrawFourIsAlwaysLegal();
        testCalledColorMatchesWildCard();

        // Bot card selection strategy
        testBotPrefersPowerCards();
        testBotSelectsFirstLegalCard();
        testBotColorChoicePrefersMostCommon();

        // Deck and draw mechanics
        testDrawFromDeck();
        testDrawWhenDeckEmpty();
        testDrawWhenBothDeckAndDiscardEmpty();

        // Full turn effects
        testSkipAdvancesTwoPlayers();
        testReverseChangesDirection();
        testReverseInTwoPlayerGameSkipsOpponent();
        testDrawTwoAddsCardsAndSkipsTurn();
        testWildDrawFourAddsFourCardsAndSkipsTurn();
        testWinningHandScoresOpponents();
        testIllegalCardCausesPenalty();
        testNormalCardAdvancesOnePlayer();
        testUnoSaidWithOneCardRemaining();
        testWildPlaySetsCalledColor();
        testBotAutomaticallyPlaysLegalDrawnCard();
        testDrawWithoutLegalPlayEndsTurn();
        testHumanCanPlayDrawnCard();
        testHumanDeclinesLegalDrawnCard();
        testStartNewGameDealsSevenCards();
        testPlayedCardMovesPreviousUpCardToDiscard();

        // Game state edge cases
        testIllegalIndexOutOfBounds();
    }

    // ===== Card Representation Tests =====

    private static void testCardColor() {
        try {
            assert "R".equals(GameRules.getCardColor("R5")) : "Red 5 should be R color";
            assert "Y".equals(GameRules.getCardColor("Y9")) : "Yellow 9 should be Y color";
            assert "G".equals(GameRules.getCardColor("G+2")) : "Green draw two should be G color";
            assert "B".equals(GameRules.getCardColor("BS")) : "Blue skip should be B color";
            assert "".equals(GameRules.getCardColor("W")) : "Wild should have empty color";
            assert "".equals(GameRules.getCardColor("W4")) : "Wild draw four should have empty color";
            pass("Card color parsing");
        } catch (AssertionError e) {
            fail("Card color parsing: " + e.getMessage());
        }
    }

    private static void testCardRank() {
        try {
            assert "NUMBER".equals(GameRules.getCardRank("R5")) : "R5 should be NUMBER rank";
            assert "SKIP".equals(GameRules.getCardRank("RS")) : "RS should be SKIP rank";
            assert "REVERSE".equals(GameRules.getCardRank("BR")) : "BR should be REVERSE rank";
            assert "DRAW_TWO".equals(GameRules.getCardRank("G+2")) : "G+2 should be DRAW_TWO rank";
            assert "WILD".equals(GameRules.getCardRank("W")) : "W should be WILD rank";
            assert "WILD_DRAW_FOUR".equals(GameRules.getCardRank("W4")) : "W4 should be WILD_DRAW_FOUR rank";
            pass("Card rank parsing");
        } catch (AssertionError e) {
            fail("Card rank parsing: " + e.getMessage());
        }
    }

    private static void testCardNumber() {
        try {
            assert 0 == GameRules.getCardNumber("R0") : "R0 should have number 0";
            assert 5 == GameRules.getCardNumber("R5") : "R5 should have number 5";
            assert 9 == GameRules.getCardNumber("Y9") : "Y9 should have number 9";
            assert -1 == GameRules.getCardNumber("RS") : "RS (skip) should have number -1";
            assert -1 == GameRules.getCardNumber("W") : "W (wild) should have number -1";
            pass("Card number extraction");
        } catch (AssertionError e) {
            fail("Card number extraction: " + e.getMessage());
        }
    }

    private static void testCardPoints() {
        try {
            assert 0 == GameRules.getCardPoints("R0") : "R0 should be worth 0 points";
            assert 5 == GameRules.getCardPoints("R5") : "R5 should be worth 5 points";
            assert 9 == GameRules.getCardPoints("Y9") : "Y9 should be worth 9 points";
            assert 20 == GameRules.getCardPoints("RS") : "RS (skip) should be worth 20 points";
            assert 20 == GameRules.getCardPoints("GR") : "GR (reverse) should be worth 20 points";
            assert 20 == GameRules.getCardPoints("B+2") : "B+2 (draw two) should be worth 20 points";
            assert 50 == GameRules.getCardPoints("W") : "W (wild) should be worth 50 points";
            assert 50 == GameRules.getCardPoints("W4") : "W4 (wild draw four) should be worth 50 points";
            pass("Card point values");
        } catch (AssertionError e) {
            fail("Card point values: " + e.getMessage());
        }
    }

    // ===== Legal Play Tests =====

    private static void testLegalMatchColor() {
        try {
            assert GameRules.isLegalPlay("R5", "R9", "") : "R5 should be legal on R9 (same color)";
            assert GameRules.isLegalPlay("B0", "B+2", "") : "B0 should be legal on B+2 (same color)";
            pass("Legal play by color match");
        } catch (AssertionError e) {
            fail("Legal play by color match: " + e.getMessage());
        }
    }

    private static void testIllegalMismatchColor() {
        try {
            assert !GameRules.isLegalPlay("R5", "B9", "") : "R5 should not be legal on B9 (no match)";
            assert !GameRules.isLegalPlay("Y0", "G+2", "") : "Y0 should not be legal on G+2 (no match)";
            pass("Illegal play detects color mismatch");
        } catch (AssertionError e) {
            fail("Illegal play detects color mismatch: " + e.getMessage());
        }
    }

    private static void testLegalMatchNumber() {
        try {
            assert GameRules.isLegalPlay("G9", "R9", "") : "G9 should be legal on R9 (same number)";
            assert GameRules.isLegalPlay("Y5", "B5", "") : "Y5 should be legal on B5 (same number)";
            pass("Legal play by number match");
        } catch (AssertionError e) {
            fail("Legal play by number match: " + e.getMessage());
        }
    }

    private static void testLegalMatchNumberOnWild() {
        try {
            assert !GameRules.isLegalPlay("G9", "W", "")
                    : "Number cards should not match a wild top card without called color";
            assert GameRules.isLegalPlay("R9", "W", "R")
                    : "A called color should allow matching number cards to be played";
            pass("Legal play number handling on wild top card");
        } catch (AssertionError e) {
            fail("Legal play number handling on wild top card: " + e.getMessage());
        }
    }

    private static void testLegalMatchSkip() {
        try {
            assert GameRules.isLegalPlay("BS", "RS", "") : "BS should be legal on RS (same action type)";
            assert GameRules.isLegalPlay("GS", "YS", "") : "GS should be legal on YS (same action type)";
            pass("Legal play by skip match");
        } catch (AssertionError e) {
            fail("Legal play by skip match: " + e.getMessage());
        }
    }

    private static void testLegalMatchReverse() {
        try {
            assert GameRules.isLegalPlay("BR", "GR", "") : "BR should be legal on GR (same action type)";
            assert GameRules.isLegalPlay("YR", "RR", "") : "YR should be legal on RR (same action type)";
            pass("Legal play by reverse match");
        } catch (AssertionError e) {
            fail("Legal play by reverse match: " + e.getMessage());
        }
    }

    private static void testLegalMatchDrawTwo() {
        try {
            assert GameRules.isLegalPlay("R+2", "G+2", "") : "R+2 should be legal on G+2 (same action type)";
            assert GameRules.isLegalPlay("B+2", "Y+2", "") : "B+2 should be legal on Y+2 (same action type)";
            pass("Legal play by draw two match");
        } catch (AssertionError e) {
            fail("Legal play by draw two match: " + e.getMessage());
        }
    }

    private static void testWildIsAlwaysLegal() {
        try {
            assert GameRules.isLegalPlay("W", "R5", "") : "W should be legal on any card";
            assert GameRules.isLegalPlay("W", "B+2", "") : "W should be legal on action cards";
            assert GameRules.isLegalPlay("W", "W4", "R") : "W should be legal even when wild drawn four is out";
            pass("Wild card is always legal");
        } catch (AssertionError e) {
            fail("Wild card is always legal: " + e.getMessage());
        }
    }

    private static void testWildDrawFourIsAlwaysLegal() {
        try {
            assert GameRules.isLegalPlay("W4", "R5", "") : "W4 should be legal on any card";
            assert GameRules.isLegalPlay("W4", "Y0", "") : "W4 should be legal on number cards";
            pass("Wild draw four is always legal");
        } catch (AssertionError e) {
            fail("Wild draw four is always legal: " + e.getMessage());
        }
    }

    private static void testCalledColorMatchesWildCard() {
        try {
            assert GameRules.isLegalPlay("B3", "W", "B") : "B3 should be legal on W when B is called";
            assert GameRules.isLegalPlay("R7", "W4", "R") : "R7 should be legal on W4 when R is called";
            assert !GameRules.isLegalPlay("R3", "W", "B") : "R3 should not be legal on W when B is called";
            pass("Called color matching works");
        } catch (AssertionError e) {
            fail("Called color matching: " + e.getMessage());
        }
    }

    // ===== Bot Strategy Tests =====

    private static void testBotPrefersPowerCards() {
        try {
            ArrayList<String> hand = new ArrayList<>();
            hand.add("B3");
            hand.add("R4");
            hand.add("R+2");

            int chosen = BotPlayer.chooseCard(hand, "R9", "");
            assert chosen == 2 : "Bot should prefer draw two at index 2, got " + chosen;
            pass("Bot prefers power cards");
        } catch (AssertionError e) {
            fail("Bot prefers power cards: " + e.getMessage());
        }
    }

    private static void testBotSelectsFirstLegalCard() {
        try {
            ArrayList<String> hand = new ArrayList<>();
            hand.add("B3");
            hand.add("R4");
            hand.add("W");

            int chosen = BotPlayer.chooseCard(hand, "R9", "");
            assert chosen == 1 : "Bot should select R4 (index 1) for color match, got " + chosen;
            pass("Bot selects legal cards");
        } catch (AssertionError e) {
            fail("Bot selects legal cards: " + e.getMessage());
        }
    }

    private static void testBotColorChoicePrefersMostCommon() {
        try {
            ArrayList<String> hand = new ArrayList<>();
            hand.add("B1");
            hand.add("B2");
            hand.add("R3");

            String chosen = BotPlayer.chooseColor(hand);
            assert "B".equals(chosen) : "Bot should choose B (most common), got " + chosen;
            pass("Bot color choice prefers most common");
        } catch (AssertionError e) {
            fail("Bot color choice prefers most common: " + e.getMessage());
        }
    }

    // ===== Deck Management Tests =====

    private static void testDrawFromDeck() {
        try {
            Deck deck = new Deck();
            deck.getDrawPile().add("R5");
            deck.getDrawPile().add("B9");

            String drawn = deck.draw();
            assert "R5".equals(drawn) : "Should draw R5, got " + drawn;
            assert 1 == deck.drawPileSize() : "Deck should have 1 card left";
            pass("Draw from deck works");
        } catch (AssertionError e) {
            fail("Draw from deck: " + e.getMessage());
        }
    }

    private static void testDrawWhenDeckEmpty() {
        try {
            Deck deck = new Deck();
            deck.getDiscardPile().add("R5");
            deck.getDiscardPile().add("B9");
            deck.getDiscardPile().add("G0");

            deck.draw();
            assert deck.drawPileSize() == 2 : "After draw, deck should have 2 cards (discard - 1)";
            assert deck.discardPileSize() == 0 : "Discard should be empty after refill";
            pass("Draw refills from discard pile");
        } catch (AssertionError e) {
            fail("Draw refills from discard: " + e.getMessage());
        }
    }

    private static void testDrawWhenBothDeckAndDiscardEmpty() {
        try {
            Deck deck = new Deck();

            String drawn = deck.draw();
            assert "W".equals(drawn) : "When both empty, should return 'W', got " + drawn;
            pass("Draw returns W when both piles empty");
        } catch (AssertionError e) {
            fail("Draw returns W when both piles empty: " + e.getMessage());
        }
    }

    // ===== Turn Effect Tests =====

    private static void testSkipAdvancesTwoPlayers() {
        try {
            TestGameFixture fixture = new TestGameFixture(3);
            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R5";
            fixture.state.hands.get(0).add("RS");
            fixture.state.hands.get(0).add("R2");

            fixture.engine.playChosenCard(0, null, new ArrayList<>());

            assert fixture.state.currentPlayer == 2 : "Skip should advance past player 1 to player 2";
            assert "RS".equals(fixture.state.upCard) : "Up card should be the played skip";
            pass("Skip advances two players");
        } catch (AssertionError e) {
            fail("Skip advances two players: " + e.getMessage());
        }
    }

    private static void testReverseChangesDirection() {
        try {
            TestGameFixture fixture = new TestGameFixture(3);
            fixture.state.currentPlayer = 1;
            fixture.state.direction = 1;
            fixture.state.upCard = "G3";
            fixture.state.hands.get(1).add("GR");
            fixture.state.hands.get(1).add("G1");

            fixture.engine.playChosenCard(0, null, new ArrayList<>());

            assert fixture.state.direction == -1 : "Reverse should flip direction to -1";
            assert fixture.state.currentPlayer == 0 : "Reverse should advance to previous player";
            pass("Reverse changes direction");
        } catch (AssertionError e) {
            fail("Reverse changes direction: " + e.getMessage());
        }
    }

    private static void testReverseInTwoPlayerGameSkipsOpponent() {
        try {
            TestGameFixture fixture = new TestGameFixture(2);
            fixture.state.currentPlayer = 0;
            fixture.state.direction = 1;
            fixture.state.upCard = "B4";
            fixture.state.hands.get(0).add("BR");
            fixture.state.hands.get(0).add("B2");

            fixture.engine.playChosenCard(0, null, new ArrayList<>());

            assert fixture.state.direction == -1 : "Reverse should flip direction";
            assert fixture.state.currentPlayer == 0 : "Two-player reverse should return turn to same player";
            pass("Reverse in two-player game skips opponent");
        } catch (AssertionError e) {
            fail("Reverse in two-player game skips opponent: " + e.getMessage());
        }
    }

    private static void testDrawTwoAddsCardsAndSkipsTurn() {
        try {
            TestGameFixture fixture = new TestGameFixture(3);
            fixture.deck.getDrawPile().clear();
            fixture.deck.getDrawPile().add("Y1");
            fixture.deck.getDrawPile().add("Y2");

            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R4";
            fixture.state.hands.get(0).add("R+2");
            fixture.state.hands.get(0).add("R3");
            int nextHandSizeBefore = fixture.state.hands.get(1).size();

            fixture.engine.playChosenCard(0, null, new ArrayList<>());

            assert fixture.state.hands.get(1).size() == nextHandSizeBefore + 2
                    : "Next player should draw two cards";
            assert fixture.state.currentPlayer == 2 : "Draw two should skip the next player";
            pass("Draw two adds cards and skips turn");
        } catch (AssertionError e) {
            fail("Draw two adds cards and skips turn: " + e.getMessage());
        }
    }

    private static void testWildDrawFourAddsFourCardsAndSkipsTurn() {
        try {
            TestGameFixture fixture = new TestGameFixture(3);
            fixture.deck.getDrawPile().clear();
            for (int i = 0; i < 4; i++) {
                fixture.deck.getDrawPile().add("G" + i);
            }

            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R4";
            fixture.state.hands.get(0).add("W4");
            fixture.state.hands.get(0).add("R3");
            int nextHandSizeBefore = fixture.state.hands.get(1).size();

            fixture.engine.playChosenCard(0, "B", new ArrayList<>());

            assert fixture.state.hands.get(1).size() == nextHandSizeBefore + 4
                    : "Next player should draw four cards";
            assert fixture.state.currentPlayer == 2 : "Wild draw four should skip the next player";
            assert "B".equals(fixture.state.calledColor) : "Wild draw four should set called color";
            pass("Wild draw four adds four cards and skips turn");
        } catch (AssertionError e) {
            fail("Wild draw four adds four cards and skips turn: " + e.getMessage());
        }
    }

    private static void testWinningHandScoresOpponents() {
        try {
            TestGameFixture fixture = new TestGameFixture(2);
            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R5";
            fixture.state.hands.get(0).add("R3");
            fixture.state.hands.get(1).add("B9");
            fixture.state.hands.get(1).add("YS");
            fixture.state.hands.get(1).add("W");

            GameEngine.TurnOutcome outcome =
                    fixture.engine.playChosenCard(0, null, new ArrayList<>());

            assert outcome.result == GameEngine.TurnResult.WON : "Playing last card should win";
            assert outcome.pointsScored == 79 : "Should score 9 + 20 + 50 = 79, got " + outcome.pointsScored;
            assert fixture.state.scores[0] == 79 : "Winner score should be updated";
            pass("Winning hand scores opponents' cards");
        } catch (AssertionError e) {
            fail("Winning hand scores opponents' cards: " + e.getMessage());
        }
    }

    private static void testIllegalCardCausesPenalty() {
        try {
            TestGameFixture fixture = new TestGameFixture(2);
            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R5";
            fixture.state.hands.get(0).add("B3");
            fixture.state.hands.get(0).add("R2");
            fixture.deck.getDrawPile().clear();
            fixture.deck.getDrawPile().add("G1");
            int handSizeBefore = fixture.state.hands.get(0).size();

            fixture.engine.playChosenCard(0, null, new ArrayList<>());

            assert fixture.state.hands.get(0).size() == handSizeBefore + 1
                    : "Illegal card should add a penalty card";
            assert fixture.state.currentPlayer == 1 : "Illegal card should end the turn";
            pass("Illegal card causes penalty card");
        } catch (AssertionError e) {
            fail("Illegal card causes penalty card: " + e.getMessage());
        }
    }

    private static void testNormalCardAdvancesOnePlayer() {
        try {
            TestGameFixture fixture = new TestGameFixture(3);
            fixture.state.currentPlayer = 1;
            fixture.state.upCard = "Y4";
            fixture.state.hands.get(1).add("Y7");
            fixture.state.hands.get(1).add("G2");

            fixture.engine.playChosenCard(0, null, new ArrayList<>());

            assert fixture.state.currentPlayer == 2 : "Normal play should advance one player";
            assert "Y7".equals(fixture.state.upCard) : "Played card should become up card";
            pass("Normal card advances one player");
        } catch (AssertionError e) {
            fail("Normal card advances one player: " + e.getMessage());
        }
    }

    private static void testUnoSaidWithOneCardRemaining() {
        try {
            TestGameFixture fixture = new TestGameFixture(2);
            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R4";
            fixture.state.hands.get(0).add("R7");
            fixture.state.hands.get(0).add("R2");

            ArrayList<String> events = new ArrayList<>();
            fixture.engine.playChosenCard(0, null, events);

            boolean saidUno = false;
            for (String event : events) {
                if (event.contains("says UNO!")) {
                    saidUno = true;
                }
            }
            assert saidUno : "Playing down to one card should announce UNO";
            pass("UNO announced with one card left");
        } catch (AssertionError e) {
            fail("UNO announced with one card left: " + e.getMessage());
        }
    }

    private static void testWildPlaySetsCalledColor() {
        try {
            TestGameFixture fixture = new TestGameFixture(2);
            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R4";
            fixture.state.hands.get(0).add("W");
            fixture.state.hands.get(0).add("B2");

            fixture.engine.playChosenCard(0, "G", new ArrayList<>());

            assert "G".equals(fixture.state.calledColor) : "Wild should store called color";
            pass("Wild play sets called color");
        } catch (AssertionError e) {
            fail("Wild play sets called color: " + e.getMessage());
        }
    }

    private static void testBotAutomaticallyPlaysLegalDrawnCard() {
        try {
            TestGameFixture fixture = new TestGameFixture(2);
            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R9";
            fixture.state.hands.get(0).add("B1");
            fixture.deck.getDrawPile().clear();
            fixture.deck.getDrawPile().add("R5");

            String drawn = fixture.engine.drawCard();
            fixture.state.hands.get(0).add(drawn);
            assert fixture.engine.isLegalPlay(drawn) : "Drawn card should be legal for bot auto-play";

            fixture.engine.playChosenCard(fixture.state.hands.get(0).size() - 1, null, new ArrayList<>());

            assert "R5".equals(fixture.state.upCard) : "Bot should play the drawn card";
            assert fixture.state.currentPlayer == 1 : "Turn should advance after playing drawn card";
            pass("Bot automatically plays legal drawn card");
        } catch (AssertionError e) {
            fail("Bot automatically plays legal drawn card: " + e.getMessage());
        }
    }

    private static void testDrawWithoutLegalPlayEndsTurn() {
        try {
            TestGameFixture fixture = new TestGameFixture(2);
            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R9";
            fixture.state.hands.get(0).add("B1");
            fixture.deck.getDrawPile().clear();
            fixture.deck.getDrawPile().add("B3");

            String drawn = fixture.engine.drawCard();
            fixture.state.hands.get(0).add(drawn);
            assert !fixture.engine.isLegalPlay(drawn) : "Drawn card should be illegal";

            fixture.engine.advancePlayer();

            assert fixture.state.currentPlayer == 1 : "Draw without play should end turn";
            assert fixture.state.hands.get(0).size() == 2 : "Illegal drawn card should stay in hand";
            pass("Draw without legal play ends turn");
        } catch (AssertionError e) {
            fail("Draw without legal play ends turn: " + e.getMessage());
        }
    }

    private static void testHumanCanPlayDrawnCard() {
        try {
            TestGameFixture fixture = new TestGameFixture(2);
            fixture.state.setupPlayers(1, true);
            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "Y4";
            fixture.state.hands.get(0).add("B1");
            fixture.deck.getDrawPile().clear();
            fixture.deck.getDrawPile().add("Y8");

            String drawn = fixture.engine.drawCard();
            fixture.state.hands.get(0).add(drawn);

            GameEngine.TurnOutcome outcome =
                    fixture.engine.playChosenCard(fixture.state.hands.get(0).size() - 1, null, new ArrayList<>());

            assert outcome.result == GameEngine.TurnResult.CONTINUE : "Human may play a drawn legal card";
            assert "Y8".equals(fixture.state.upCard) : "Drawn card should be on the table";
            pass("Human can play a drawn legal card");
        } catch (AssertionError e) {
            fail("Human can play a drawn legal card: " + e.getMessage());
        }
    }

    private static void testHumanDeclinesLegalDrawnCard() {
        try {
            TestGameFixture fixture = new TestGameFixture(2);
            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R9";
            fixture.state.hands.get(0).add("B1");
            fixture.deck.getDrawPile().clear();
            fixture.deck.getDrawPile().add("R5");

            String drawn = fixture.engine.drawCard();
            fixture.state.hands.get(0).add(drawn);
            assert fixture.engine.isLegalPlay(drawn) : "Drawn card should be legal";

            fixture.engine.advancePlayer();

            assert fixture.state.currentPlayer == 1 : "Declining to play drawn card should end turn";
            assert "R9".equals(fixture.state.upCard) : "Up card should be unchanged";
            assert fixture.state.hands.get(0).size() == 2 : "Drawn card should remain in hand";
            pass("Human can decline to play a drawn legal card");
        } catch (AssertionError e) {
            fail("Human can decline to play a drawn legal card: " + e.getMessage());
        }
    }

    private static void testPlayedCardMovesPreviousUpCardToDiscard() {
        try {
            TestGameFixture fixture = new TestGameFixture(2);
            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R5";
            fixture.state.hands.get(0).add("R7");
            fixture.state.hands.get(0).add("G2");
            fixture.deck.getDiscardPile().clear();

            fixture.engine.playChosenCard(0, null, new ArrayList<>());

            assert fixture.deck.getDiscardPile().size() == 1 : "Discard pile should receive replaced up card";
            assert "R5".equals(fixture.deck.getDiscardPile().get(0)) : "Previous up card should be discarded";
            pass("Played card replaces up card and discards previous up card");
        } catch (AssertionError e) {
            fail("Played card replaces up card and discards previous up card: " + e.getMessage());
        }
    }

    private static void testStartNewGameDealsSevenCards() {
        try {
            TestGameFixture fixture = new TestGameFixture(3);
            java.util.Random random = new java.util.Random(42);
            fixture.engine.startNewGame(random);

            for (int i = 0; i < fixture.state.playerCount(); i++) {
                assert fixture.state.hands.get(i).size() == 7
                        : "Each player should start with 7 cards";
            }
            assert !fixture.state.upCard.startsWith("W")
                    : "Starting up card should not be a wild";
            pass("New game deals seven cards and non-wild up card");
        } catch (AssertionError e) {
            fail("New game deals seven cards and non-wild up card: " + e.getMessage());
        }
    }

    // ===== Game State Edge Cases =====

    private static void testIllegalIndexOutOfBounds() {
        try {
            TestGameFixture fixture = new TestGameFixture(2);
            fixture.state.currentPlayer = 0;
            fixture.state.upCard = "R5";
            fixture.state.hands.get(0).add("R3");
            fixture.deck.getDrawPile().clear();
            fixture.deck.getDrawPile().add("B1");
            int handSizeBefore = fixture.state.hands.get(0).size();

            fixture.engine.playChosenCard(5, null, new ArrayList<>());

            assert fixture.state.hands.get(0).size() == handSizeBefore + 1
                    : "Invalid index should add a penalty card";
            assert fixture.state.currentPlayer == 1 : "Invalid index should end the turn";
            pass("Illegal index causes penalty card");
        } catch (AssertionError e) {
            fail("Illegal index edge case: " + e.getMessage());
        }
    }

    // ===== Test Utilities =====

    private static class TestGameFixture {
        final GameState state = new GameState();
        final Deck deck = new Deck();
        final GameEngine engine;

        TestGameFixture(int players) {
            state.setupPlayers(players, false);
            engine = new GameEngine(state, deck);
        }
    }

    private static void pass(String testName) {
        passedTests++;
        if (verboseTests) {
            System.out.println("✓ " + testName);
        }
    }

    private static void fail(String testName) {
        failedTests++;
        System.out.println("✗ " + testName);
    }

    private static void printSummary() {
        int total = passedTests + failedTests;
        System.out.println("\nCharacterization Tests: " + passedTests + "/" + total + " passed");
        if (failedTests > 0) {
            System.exit(1);
        }
    }
}
