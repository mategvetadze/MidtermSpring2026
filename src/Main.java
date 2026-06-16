import java.util.ArrayList;
import java.util.Random;

public class Main {
    static ArrayList<String> playerNames = new ArrayList<>();
    static ArrayList<Boolean> humanPlayers = new ArrayList<>();
    static int[] scores = new int[10];
    static boolean quiet = false;
    static Random random = new Random();

    // Game state
    static String upCard = "";
    static String calledColor = "";
    static ArrayList<ArrayList<String>> hands = new ArrayList<>();
    static int currentPlayer = 0;
    static int direction = 1;
    static Deck deck;

    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        long seed = System.currentTimeMillis();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--bots") && i + 1 < args.length) {
                bots = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--games") && i + 1 < args.length) {
                games = Integer.parseInt(args[++i]);
            } else if (args[i].equals("--human")) {
                human = true;
            } else if (args[i].equals("--quiet")) {
                quiet = true;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--help")) {
                System.out.println("Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N]");
                return;
            }
        }

        random = new Random(seed);
        setupPlayers(bots, human);

        if (playerNames.size() < 2 || playerNames.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        for (int g = 1; g <= games; g++) {
            if (!quiet) {
                System.out.println("\n=== Game " + g + " ===");
            }
            playGame();
        }

        System.out.println("\nFinal scores:");
        for (int i = 0; i < playerNames.size(); i++) {
            System.out.println(playerNames.get(i) + ": " + scores[i]);
        }
    }

    static void setupPlayers(int bots, boolean human) {
        playerNames.clear();
        humanPlayers.clear();
        if (human) {
            playerNames.add("You");
            humanPlayers.add(true);
        }
        for (int i = 1; i <= bots; i++) {
            playerNames.add("Bot" + i);
            humanPlayers.add(false);
        }
    }

    static void playGame() {
        deck = new Deck(random);
        hands.clear();
        for (int i = 0; i < playerNames.size(); i++) {
            hands.add(new ArrayList<>());
        }

        // Initialize deck and hands
        deck.initialize();
        for (int i = 0; i < playerNames.size(); i++) {
            for (int j = 0; j < 7; j++) {
                hands.get(i).add(deck.draw());
            }
        }

        // Find starting card (not a wild)
        upCard = deck.draw();
        while (upCard.startsWith("W")) {
            deck.discard(upCard);
            upCard = deck.draw();
        }
        calledColor = "";
        direction = 1;
        currentPlayer = random.nextInt(playerNames.size());

        // Main game loop
        int safety = 0;
        while (safety < 3000) {
            safety++;
            executeTurn();
        }
        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
    }

    static void executeTurn() {
        String name = playerNames.get(currentPlayer);
        ArrayList<String> hand = hands.get(currentPlayer);

        if (!quiet) {
            System.out.println("\nUp card: " + upCard + (calledColor.isEmpty() ? "" : " called " + calledColor));
            System.out.println(name + " hand: " + formatHand(hand));
        }

        int chosen = -1;
        if (humanPlayers.get(currentPlayer)) {
            chosen = askHumanCard(hand);
        } else {
            chosen = chooseBotCard(hand);
        }

        // Handle draw
        if (chosen == -1) {
            String drawn = deck.draw();
            hand.add(drawn);
            if (!quiet) {
                System.out.println(name + " draws " + drawn);
            }
            if (GameRules.isLegalPlay(drawn, upCard, calledColor)) {
                if (!humanPlayers.get(currentPlayer)) {
                    chosen = hand.size() - 1;
                } else {
                    System.out.print("Play drawn card " + drawn + "? y/n: ");
                    String ans = new java.util.Scanner(System.in).nextLine();
                    if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
                        chosen = hand.size() - 1;
                    }
                }
            }
        }

        // Play card
        if (chosen >= 0) {
            if (chosen >= hand.size()) {
                if (!quiet) {
                    System.out.println(name + " picked bad index, draws penalty.");
                }
                hand.add(deck.draw());
                nextPlayer();
                return;
            }

            String card = hand.get(chosen);
            if (!GameRules.isLegalPlay(card, upCard, calledColor)) {
                if (!quiet) {
                    System.out.println(name + " played illegal card " + card + ", draws penalty.");
                }
                hand.add(deck.draw());
                nextPlayer();
                return;
            }

            hand.remove(chosen);
            deck.discard(upCard);
            upCard = card;
            calledColor = "";
            if (!quiet) {
                System.out.println(name + " plays " + card);
            }

            // Handle wild
            if (card.equals("W") || card.equals("W4")) {
                if (humanPlayers.get(currentPlayer)) {
                    calledColor = askHumanColor();
                } else {
                    calledColor = chooseBotColor(hand);
                }
                if (!quiet) {
                    System.out.println(name + " calls " + calledColor);
                }
            }

            // Check UNO
            if (hand.size() == 1) {
                if (!quiet) {
                    System.out.println(name + " says UNO!");
                }
            }

            // Check win
            if (hand.size() == 0) {
                int won = 0;
                for (int i = 0; i < hands.size(); i++) {
                    if (i != currentPlayer) {
                        for (String c : hands.get(i)) {
                            won += GameRules.getCardPoints(c);
                        }
                    }
                }
                scores[currentPlayer] += won;
                if (!quiet) {
                    System.out.println(name + " wins, scores " + won);
                }
                return;
            }

            applyCardEffect(card);
        } else {
            nextPlayer();
        }
    }

    static void applyCardEffect(String card) {
        String effect = GameRules.getCardEffect(card);

        if (effect.equals("SKIP")) {
            nextPlayer();
            nextPlayer();
        } else if (effect.equals("REVERSE")) {
            direction *= -1;
            if (playerNames.size() == 2) {
                nextPlayer();
                nextPlayer();
            } else {
                nextPlayer();
            }
        } else if (effect.equals("DRAW_TWO")) {
            nextPlayer();
            hands.get(currentPlayer).add(deck.draw());
            hands.get(currentPlayer).add(deck.draw());
            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws two.");
            }
            nextPlayer();
        } else if (effect.equals("WILD_DRAW_FOUR")) {
            nextPlayer();
            for (int i = 0; i < 4; i++) {
                hands.get(currentPlayer).add(deck.draw());
            }
            if (!quiet) {
                System.out.println(playerNames.get(currentPlayer) + " draws four.");
            }
            nextPlayer();
        } else {
            nextPlayer();
        }
    }

    static int askHumanCard(ArrayList<String> hand) {
        java.util.Scanner in = new java.util.Scanner(System.in);
        while (true) {
            System.out.print("Choose card index/code or draw: ");
            String input = in.nextLine().trim().toUpperCase();
            if (input.equals("DRAW")) {
                return -1;
            }
            try {
                int idx = Integer.parseInt(input);
                if (idx >= 0 && idx < hand.size()) {
                    return idx;
                }
            } catch (Exception ignored) {
            }
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).equals(input)) {
                    if (GameRules.isLegalPlay(hand.get(i), upCard, calledColor)) {
                        return i;
                    }
                    System.out.println("That card is not legal.");
                    return i;  // Return to be caught by illegal play handler
                }
            }
            System.out.println("Card not found.");
        }
    }

    static String askHumanColor() {
        java.util.Scanner in = new java.util.Scanner(System.in);
        while (true) {
            System.out.print("Call color R/Y/G/B: ");
            String c = in.nextLine().trim().toUpperCase();
            if (c.equals("R") || c.equals("Y") || c.equals("G") || c.equals("B")) {
                return c;
            }
            System.out.println("Bad color.");
        }
    }

    static int chooseBotCard(ArrayList<String> hand) {
        // Prefer draw two
        for (int i = 0; i < hand.size(); i++) {
            if (GameRules.isLegalPlay(hand.get(i), upCard, calledColor) &&
                GameRules.getCardEffect(hand.get(i)).equals("DRAW_TWO")) {
                return i;
            }
        }
        // Then skip
        for (int i = 0; i < hand.size(); i++) {
            if (GameRules.isLegalPlay(hand.get(i), upCard, calledColor) &&
                GameRules.getCardEffect(hand.get(i)).equals("SKIP")) {
                return i;
            }
        }
        // Then numbers
        for (int i = 0; i < hand.size(); i++) {
            if (GameRules.isLegalPlay(hand.get(i), upCard, calledColor) &&
                GameRules.getCardRank(hand.get(i)).equals("NUMBER")) {
                return i;
            }
        }
        // Finally wild
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).startsWith("W")) {
                return i;
            }
        }
        return -1;
    }

    static String chooseBotColor(ArrayList<String> hand) {
        int r = 0, y = 0, g = 0, b = 0;
        for (String c : hand) {
            String col = GameRules.getCardColor(c);
            if (col.equals("R")) r++;
            else if (col.equals("Y")) y++;
            else if (col.equals("G")) g++;
            else if (col.equals("B")) b++;
        }
        if (r >= y && r >= g && r >= b) return "R";
        if (y >= r && y >= g && y >= b) return "Y";
        if (g >= r && g >= y && g >= b) return "G";
        return "B";
    }

    static void nextPlayer() {
        currentPlayer += direction;
        if (currentPlayer >= playerNames.size()) currentPlayer = 0;
        if (currentPlayer < 0) currentPlayer = playerNames.size() - 1;
    }

    static String formatHand(ArrayList<String> hand) {
        String out = "";
        for (int i = 0; i < hand.size(); i++) {
            out += i + ":" + hand.get(i);
            if (i < hand.size() - 1) out += " ";
        }
        return out;
    }
}
