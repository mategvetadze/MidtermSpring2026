import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Manages the draw pile and discard pile.
 */
public class Deck {
    public static final int STANDARD_DECK_SIZE = 108;
    private final ArrayList<String> drawPile = new ArrayList<>();
    private final ArrayList<String> discardPile = new ArrayList<>();
    private Random random = new Random();

    public void setRandom(Random random) {
        this.random = random;
    }

    public void buildAndShuffle() {
        drawPile.clear();
        String[] colors = {"R", "Y", "G", "B"};
        for (int c = 0; c < colors.length; c++) {
            drawPile.add(colors[c] + "0");
            for (int n = 1; n <= 9; n++) {
                drawPile.add(colors[c] + n);
                drawPile.add(colors[c] + n);
            }
            drawPile.add(colors[c] + "S");
            drawPile.add(colors[c] + "S");
            drawPile.add(colors[c] + "R");
            drawPile.add(colors[c] + "R");
            drawPile.add(colors[c] + "+2");
            drawPile.add(colors[c] + "+2");
        }
        for (int i = 0; i < 4; i++) {
            drawPile.add("W");
            drawPile.add("W4");
        }
        Collections.shuffle(drawPile, random);
        discardPile.clear();
    }

    public String draw() {
        if (drawPile.isEmpty()) {
            drawPile.addAll(discardPile);
            discardPile.clear();
            Collections.shuffle(drawPile, random);
        }
        if (drawPile.isEmpty()) {
            return "W";
        }
        return drawPile.remove(0);
    }

    public void discard(String card) {
        discardPile.add(card);
    }

    public void clear() {
        drawPile.clear();
        discardPile.clear();
    }

    public ArrayList<String> getDrawPile() {
        return drawPile;
    }

    public ArrayList<String> getDiscardPile() {
        return discardPile;
    }

    public int drawPileSize() {
        return drawPile.size();
    }

    public int discardPileSize() {
        return discardPile.size();
    }

    /** Builds an unshuffled deck for composition checks. */
    public static ArrayList<String> buildStandardDeck() {
        Deck deck = new Deck();
        deck.buildAndShuffle();
        ArrayList<String> cards = new ArrayList<>(deck.getDrawPile());
        return cards;
    }

    public static boolean isStandardComposition(ArrayList<String> cards) {
        if (cards.size() != STANDARD_DECK_SIZE) {
            return false;
        }
        int[] colorNumbers = new int[40];
        int[] colorActions = new int[24];
        int wilds = 0;
        int wildDrawFours = 0;
        for (String card : cards) {
            if (card.equals("W")) {
                wilds++;
            } else if (card.equals("W4")) {
                wildDrawFours++;
            } else {
                String color = GameRules.getCardColor(card);
                String rank = GameRules.getCardRank(card);
                int colorIndex = switch (color) {
                    case "R" -> 0;
                    case "Y" -> 1;
                    case "G" -> 2;
                    case "B" -> 3;
                    default -> -1;
                };
                if (colorIndex < 0) {
                    return false;
                }
                if (rank.equals("NUMBER")) {
                    colorNumbers[colorIndex * 10 + GameRules.getCardNumber(card)]++;
                } else {
                    colorActions[colorIndex * 6 + actionIndex(rank)]++;
                }
            }
        }
        if (wilds != 4 || wildDrawFours != 4) {
            return false;
        }
        for (int color = 0; color < 4; color++) {
            if (colorNumbers[color * 10] != 1) {
                return false;
            }
            for (int number = 1; number <= 9; number++) {
                if (colorNumbers[color * 10 + number] != 2) {
                    return false;
                }
            }
            for (int action = 0; action < 3; action++) {
                if (colorActions[color * 6 + action] != 2) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int actionIndex(String rank) {
        return switch (rank) {
            case "SKIP" -> 0;
            case "REVERSE" -> 1;
            case "DRAW_TWO" -> 2;
            default -> -1;
        };
    }
}
