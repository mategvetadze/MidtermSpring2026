import java.util.ArrayList;

/**
 * Centralizes all UNO game rules.
 */
public class GameRules {

    public static boolean isLegalPlay(String card, String upCard, String calledColor) {
        return new Card(card).isLegalOn(new Card(upCard), calledColor);
    }

    public static boolean isLegalPlay(String card, String upCard) {
        return isLegalPlay(card, upCard, "");
    }

    public static int[] findLegalMoves(ArrayList<String> hand, String upCard, String calledColor) {
        ArrayList<Integer> legalIndices = new ArrayList<>();
        for (int i = 0; i < hand.size(); i++) {
            if (isLegalPlay(hand.get(i), upCard, calledColor)) {
                legalIndices.add(i);
            }
        }
        int[] result = new int[legalIndices.size()];
        for (int i = 0; i < legalIndices.size(); i++) {
            result[i] = legalIndices.get(i);
        }
        return result;
    }

    public static String getCardEffect(String card) {
        Card c = new Card(card);
        String rank = c.rank();

        if (rank.equals("SKIP")) return "SKIP";
        if (rank.equals("REVERSE")) return "REVERSE";
        if (rank.equals("DRAW_TWO")) return "DRAW_TWO";
        if (rank.equals("WILD")) return "WILD";
        if (rank.equals("WILD_DRAW_FOUR")) return "WILD_DRAW_FOUR";

        return "NORMAL";
    }

    public static int getCardPoints(String card) {
        return new Card(card).points();
    }

    public static String getCardColor(String card) {
        return new Card(card).color();
    }

    public static String getCardRank(String card) {
        return new Card(card).rank();
    }

    public static int getCardNumber(String card) {
        return new Card(card).number();
    }
}
