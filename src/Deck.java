import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Manages the draw pile and discard pile.
 */
public class Deck {
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
}
