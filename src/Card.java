/**
 * Represents a single UNO card.
 * Encapsulates card parsing and behavior.
 */
public class Card {
    private final String code;

    public Card(String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Card code cannot be null or empty");
        }
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Returns the color of this card (R, Y, G, B, or empty for wildcards).
     */
    public String color() {
        if (code.startsWith("R")) return "R";
        if (code.startsWith("Y")) return "Y";
        if (code.startsWith("G")) return "G";
        if (code.startsWith("B")) return "B";
        return "";
    }

    /**
     * Returns the rank of this card (NUMBER, SKIP, REVERSE, DRAW_TWO, WILD, WILD_DRAW_FOUR).
     */
    public String rank() {
        if (code.equals("W")) return "WILD";
        if (code.equals("W4")) return "WILD_DRAW_FOUR";
        if (code.endsWith("S")) return "SKIP";
        if (code.endsWith("R")) return "REVERSE";
        if (code.endsWith("+2")) return "DRAW_TWO";
        return "NUMBER";
    }

    /**
     * Returns the numeric value of number cards, or -1 for non-number cards.
     */
    public int number() {
        if (rank().equals("NUMBER")) {
            return Integer.parseInt(code.substring(1));
        }
        return -1;
    }

    /**
     * Returns the point value of this card in the context of game scoring.
     */
    public int points() {
        String r = rank();
        if (r.equals("NUMBER")) {
            return number();
        }
        if (r.equals("SKIP") || r.equals("REVERSE") || r.equals("DRAW_TWO")) {
            return 20;
        }
        if (r.equals("WILD") || r.equals("WILD_DRAW_FOUR")) {
            return 50;
        }
        return 0;
    }

    /**
     * Determines if this card can be played on the given up card,
     * considering an optional called color for wild cards.
     *
     * @param upCard the card currently on the table
     * @param calledColor the color called when a wild was played (empty if none)
     * @return true if this card is a legal play, false otherwise
     */
    public boolean isLegalOn(Card upCard, String calledColor) {
        // Wildcards are always legal
        if (code.startsWith("W")) {
            return true;
        }

        // Match by color
        if (color().equals(upCard.color())) {
            return true;
        }

        // Match called color (for played wildcards)
        if (!calledColor.isEmpty() && color().equals(calledColor)) {
            return true;
        }

        // Match by rank (for action cards)
        if (rank().equals(upCard.rank()) && !rank().equals("NUMBER")) {
            return true;
        }

        // Match by number
        if (rank().equals("NUMBER") && upCard.rank().equals("NUMBER") && number() == upCard.number()) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return code.equals(card.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}

