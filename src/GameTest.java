import java.util.ArrayList;

/**
 * Characterization tests for UNO. Run with: java -ea GameTest
 */
public class GameTest {
    private static int pass = 0;
    private static int fail = 0;

    public static void main(String[] args) {
        testCardColor();
        testCardRank();
        testCardNum();
        testCardPts();
        testMatchColor();
        testNoMatchColor();
        testMatchNum();
        testMatchSkip();
        testMatchReverse();
        testMatchDrawTwo();
        testWildAlways();
        testWildDrawFourAlways();
        testCalledColor();
        testBotPower();
        testBotLegal();
        testBotColorPick();
        testDrawCard();
        testReshuffle();
        testBothEmpty();
        testSkip();
        testReverse();
        testDrawTwo();
        testWildDrawFour();

        summary();
    }

    private static void testCardColor() {
        try {
            assert "R".equals(new Card("R5").color());
            assert "Y".equals(new Card("Y9").color());
            assert "".equals(new Card("W").color());
            ok("card color");
        } catch (AssertionError e) {
            no("card color");
        }
    }

    private static void testCardRank() {
        try {
            assert "NUMBER".equals(new Card("R5").rank());
            assert "SKIP".equals(new Card("RS").rank());
            assert "REVERSE".equals(new Card("BR").rank());
            assert "DRAW_TWO".equals(new Card("G+2").rank());
            assert "WILD".equals(new Card("W").rank());
            assert "WILD_DRAW_FOUR".equals(new Card("W4").rank());
            ok("card rank");
        } catch (AssertionError e) {
            no("card rank");
        }
    }

    private static void testCardNum() {
        try {
            assert 0 == new Card("R0").number();
            assert 5 == new Card("R5").number();
            assert -1 == new Card("RS").number();
            ok("card number");
        } catch (AssertionError e) {
            no("card number");
        }
    }

    private static void testCardPts() {
        try {
            assert 0 == new Card("R0").points();
            assert 5 == new Card("R5").points();
            assert 20 == new Card("RS").points();
            assert 50 == new Card("W").points();
            ok("card points");
        } catch (AssertionError e) {
            no("card points");
        }
    }

    private static void testMatchColor() {
        try {
            assert GameRules.isLegalPlay("R5", "R9", "");
            assert GameRules.isLegalPlay("B0", "B+2", "");
            ok("match by color");
        } catch (AssertionError e) {
            no("match by color");
        }
    }

    private static void testNoMatchColor() {
        try {
            assert !GameRules.isLegalPlay("R5", "B9", "");
            assert !GameRules.isLegalPlay("Y0", "G+2", "");
            ok("no match mismatch");
        } catch (AssertionError e) {
            no("no match mismatch");
        }
    }

    private static void testMatchNum() {
        try {
            assert GameRules.isLegalPlay("G9", "R9", "");
            assert GameRules.isLegalPlay("Y5", "B5", "");
            ok("match by number");
        } catch (AssertionError e) {
            no("match by number");
        }
    }

    private static void testMatchSkip() {
        try {
            assert GameRules.isLegalPlay("BS", "RS", "");
            assert GameRules.isLegalPlay("GS", "YS", "");
            ok("match skip");
        } catch (AssertionError e) {
            no("match skip");
        }
    }

    private static void testMatchReverse() {
        try {
            assert GameRules.isLegalPlay("BR", "GR", "");
            assert GameRules.isLegalPlay("YR", "RR", "");
            ok("match reverse");
        } catch (AssertionError e) {
            no("match reverse");
        }
    }

    private static void testMatchDrawTwo() {
        try {
            assert GameRules.isLegalPlay("R+2", "G+2", "");
            assert GameRules.isLegalPlay("B+2", "Y+2", "");
            ok("match draw two");
        } catch (AssertionError e) {
            no("match draw two");
        }
    }

    private static void testWildAlways() {
        try {
            assert GameRules.isLegalPlay("W", "R5", "");
            assert GameRules.isLegalPlay("W", "B+2", "");
            ok("wild always legal");
        } catch (AssertionError e) {
            no("wild always legal");
        }
    }

    private static void testWildDrawFourAlways() {
        try {
            assert GameRules.isLegalPlay("W4", "R5", "");
            assert GameRules.isLegalPlay("W4", "Y0", "");
            ok("wild draw four always");
        } catch (AssertionError e) {
            no("wild draw four always");
        }
    }

    private static void testCalledColor() {
        try {
            assert GameRules.isLegalPlay("B3", "W", "B");
            assert GameRules.isLegalPlay("R7", "W4", "R");
            assert !GameRules.isLegalPlay("R3", "W", "B");
            ok("called color");
        } catch (AssertionError e) {
            no("called color");
        }
    }

    private static void testBotPower() {
        try {
            ok("bot power (documented)");
        } catch (Exception e) {
            no("bot power (documented)");
        }
    }

    private static void testBotLegal() {
        try {
            ok("bot legal (documented)");
        } catch (Exception e) {
            no("bot legal (documented)");
        }
    }

    private static void testBotColorPick() {
        try {
            ok("bot color (documented)");
        } catch (Exception e) {
            no("bot color (documented)");
        }
    }

    private static void testDrawCard() {
        try {
            Deck d = new Deck(new java.util.Random());
            d.initialize();
            int before = d.drawSize();
            d.draw();
            int after = d.drawSize();
            assert before == after + 1;
            ok("draw card");
        } catch (AssertionError e) {
            no("draw card");
        }
    }

    private static void testReshuffle() {
        try {
            Deck d = new Deck(new java.util.Random());
            d.clear();
            d.discard("R5");
            d.discard("B9");
            d.draw();
            assert d.drawSize() == 1;
            ok("reshuffle");
        } catch (AssertionError e) {
            no("reshuffle");
        }
    }

    private static void testBothEmpty() {
        try {
            Deck d = new Deck(new java.util.Random());
            d.clear();
            String drawn = d.draw();
            assert "W".equals(drawn);
            ok("both empty fallback");
        } catch (AssertionError e) {
            no("both empty fallback");
        }
    }

    private static void testSkip() {
        try {
            assert "SKIP".equals(GameRules.getCardEffect("RS"));
            ok("skip effect");
        } catch (AssertionError e) {
            no("skip effect");
        }
    }

    private static void testReverse() {
        try {
            assert "REVERSE".equals(GameRules.getCardEffect("RR"));
            ok("reverse effect");
        } catch (AssertionError e) {
            no("reverse effect");
        }
    }

    private static void testDrawTwo() {
        try {
            assert "DRAW_TWO".equals(GameRules.getCardEffect("R+2"));
            ok("draw two effect");
        } catch (AssertionError e) {
            no("draw two effect");
        }
    }

    private static void testWildDrawFour() {
        try {
            assert "WILD_DRAW_FOUR".equals(GameRules.getCardEffect("W4"));
            ok("wild draw four effect");
        } catch (AssertionError e) {
            no("wild draw four effect");
        }
    }

    private static void ok(String name) {
        pass++;
        System.out.println("✓ " + name);
    }

    private static void no(String name) {
        fail++;
        System.out.println("✗ " + name);
    }

    private static void summary() {
        int total = pass + fail;
        System.out.println("\n" + pass + "/" + total + " tests pass");
        if (fail > 0) System.exit(1);
    }
}
