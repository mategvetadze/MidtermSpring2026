import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Deck {
    private ArrayList<String> draw;
    private ArrayList<String> discard;
    private Random random;

    public Deck(Random random) {
        this.draw = new ArrayList<>();
        this.discard = new ArrayList<>();
        this.random = random;
    }

    public void initialize() {
        draw.clear();
        discard.clear();

        String[] colors = {"R", "Y", "G", "B"};
        for (String col : colors) {
            draw.add(col + "0");
            for (int n = 1; n <= 9; n++) {
                draw.add(col + n);
                draw.add(col + n);
            }
            draw.add(col + "S");
            draw.add(col + "S");
            draw.add(col + "R");
            draw.add(col + "R");
            draw.add(col + "+2");
            draw.add(col + "+2");
        }
        for (int i = 0; i < 4; i++) {
            draw.add("W");
            draw.add("W4");
        }
        Collections.shuffle(draw, random);
    }

    public String draw() {
        if (draw.isEmpty()) {
            draw.addAll(discard);
            discard.clear();
            Collections.shuffle(draw, random);
        }
        if (draw.isEmpty()) {
            return "W";
        }
        return draw.removeFirst();
    }

    public void discard(String card) {
        discard.add(card);
    }

    public String peekDiscard() {
        return discard.isEmpty() ? "" : discard.getLast();
    }

    public int drawSize() {
        return draw.size();
    }

    public int discardSize() {
        return discard.size();
    }

    public void clear() {
        draw.clear();
        discard.clear();
    }
}
