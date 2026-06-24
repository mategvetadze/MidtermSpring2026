import java.util.ArrayList;

/**
 * Bot card and color selection strategy.
 */
public class BotPlayer {

    public static int chooseCard(ArrayList<String> hand, String upCard, String calledColor) {
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (GameRules.isWildDrawFourLegal(card, hand, upCard, calledColor)
                    && GameRules.getCardRank(card).equals("DRAW_TWO")) {
                return i;
            }
        }

        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (GameRules.isWildDrawFourLegal(card, hand, upCard, calledColor)
                    && GameRules.getCardRank(card).equals("SKIP")) {
                return i;
            }
        }

        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (GameRules.isWildDrawFourLegal(card, hand, upCard, calledColor)
                    && GameRules.getCardRank(card).equals("NUMBER")) {
                return i;
            }
        }

        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (GameRules.isWildDrawFourLegal(card, hand, upCard, calledColor) && card.equals("W")) {
                return i;
            }
        }

        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (GameRules.isWildDrawFourLegal(card, hand, upCard, calledColor) && card.equals("W4")) {
                return i;
            }
        }

        return -1;
    }

    public static String chooseColor(ArrayList<String> hand) {
        int r = 0;
        int y = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i < hand.size(); i++) {
            String c = GameRules.getCardColor(hand.get(i));
            if (c.equals("R")) {
                r++;
            } else if (c.equals("Y")) {
                y++;
            } else if (c.equals("G")) {
                g++;
            } else if (c.equals("B")) {
                b++;
            }
        }
        if (r >= y && r >= g && r >= b) {
            return "R";
        } else if (y >= r && y >= g && y >= b) {
            return "Y";
        } else if (g >= r && g >= y && g >= b) {
            return "G";
        } else {
            return "B";
        }
    }
}
