import java.util.ArrayList;

/**
 * GameRules encapsulates all the rules of the UNO game.
 * This centralizes card-play validation logic that was previously duplicated
 * in three places: playGame() loop, chooseBotCard(), and isLegal().
 */
public class GameRules {

    /**
     * Determines if the given card is a legal play on the up card,
     * considering an optional called color for wild cards.
     *
     * This is the single authoritative rule checker, replacing previous duplication.
     *
     * @param card the card being played
     * @param upCard the card currently on the table
     * @param calledColor the color called when a wild was played (empty if none)
     * @return true if the play is legal, false otherwise
     */
    public static boolean isLegalPlay(String card, String upCard, String calledColor) {
        return new Card(card).isLegalOn(new Card(upCard), calledColor);
    }

    /**
     * Determines if the given card is a legal play on the up card.
     * Overload without calledColor for simpler cases.
     */
    public static boolean isLegalPlay(String card, String upCard) {
        return isLegalPlay(card, upCard, "");
    }

    /**
     * Finds all legal moves in the given hand that can be played on the up card.
     *
     * @param hand the player's hand
     * @param upCard the card currently on the table
     * @param calledColor the color called when a wild was played (empty if none)
     * @return an array of indices of legal cards in the hand
     */
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

    /**
     * Determines the type of card effect when played.
     * This encapsulates the effects of different card types.
     *
     * @param card the card being played
     * @return the effect type: NORMAL, SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR
     */
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

    /**
     * Returns the score value of a card for end-of-hand scoring.
     */
    public static int getCardPoints(String card) {
        return new Card(card).points();
    }

    /**
     * Returns the color of a card.
     */
    public static String getCardColor(String card) {
        return new Card(card).color();
    }

    /**
     * Returns the rank of a card.
     */
    public static String getCardRank(String card) {
        return new Card(card).rank();
    }

    /**
     * Returns the number value of a card (for number cards only).
     */
    public static int getCardNumber(String card) {
        return new Card(card).number();
    }

    /**
     * Wild Draw Four is only legal when the player has no other playable card.
     */
    public static boolean isWildDrawFourLegal(String card, ArrayList<String> hand, String upCard, String calledColor) {
        if (!card.equals("W4")) {
            return isLegalPlay(card, upCard, calledColor);
        }
        if (!isLegalPlay(card, upCard, calledColor)) {
            return false;
        }
        for (String candidate : hand) {
            if (!candidate.equals("W4") && isLegalPlay(candidate, upCard, calledColor)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasLegalPlay(ArrayList<String> hand, String upCard, String calledColor) {
        for (String card : hand) {
            if (isWildDrawFourLegal(card, hand, upCard, calledColor)) {
                return true;
            }
        }
        return false;
    }
}

