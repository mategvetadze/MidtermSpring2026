import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Console input/output and game-loop orchestration.
 */
public class ConsoleGame {
    private final GameState state;
    private final GameEngine engine;
    private final Scanner scanner;
    private boolean quiet;

    public ConsoleGame(GameState state, GameEngine engine, Scanner scanner) {
        this.state = state;
        this.engine = engine;
        this.scanner = scanner;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public void playGame(Random random) {
        engine.startNewGame(random);

        int guard = 0;
        while (guard < GameEngine.SAFETY_TURN_LIMIT) {
            guard++;
            GameEngine.TurnOutcome outcome = playOneTurn();
            if (outcome.result == GameEngine.TurnResult.WON) {
                return;
            }
        }

        if (!quiet) {
            System.out.println("Game stopped at safety limit.");
        }
    }

    private GameEngine.TurnOutcome playOneTurn() {
        String name = state.currentPlayerName();
        ArrayList<String> hand = state.currentHand();

        if (!quiet) {
            System.out.println(
                    "\nUp card: "
                            + state.upCard
                            + (state.calledColor.equals("") ? "" : " called " + state.calledColor));
            System.out.println(name + " hand: " + formatHand(hand));
        }

        int chosen;
        if (state.isCurrentPlayerHuman()) {
            chosen = askHuman(hand);
        } else {
            chosen = BotPlayer.chooseCard(hand, state.upCard, state.calledColor);
        }

        if (chosen == -1) {
            String drawn = engine.drawCard();
            hand.add(drawn);
            if (!quiet) {
                System.out.println(name + " draws " + drawn);
            }

            if (engine.isLegalPlay(drawn)) {
                if (state.isCurrentPlayerHuman()) {
                    System.out.print("Play drawn card " + drawn + "? y/n: ");
                    String answer = scanner.nextLine();
                    if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                        chosen = hand.size() - 1;
                    }
                } else {
                    chosen = hand.size() - 1;
                }
            }
        }

        if (chosen >= 0) {
            String calledColor = null;
            if (chosen < hand.size()) {
                String card = hand.get(chosen);
                if ((card.equals("W") || card.equals("W4")) && engine.isLegalPlay(card)) {
                    calledColor =
                            state.isCurrentPlayerHuman()
                                    ? askColor()
                                    : BotPlayer.chooseColor(hand);
                }
            }
            GameEngine.TurnOutcome outcome = engine.playChosenCard(chosen, calledColor, new ArrayList<>());
            if (!quiet) {
                for (String event : outcome.events) {
                    System.out.println(event);
                }
            }
            return outcome;
        }

        engine.advancePlayer();
        return new GameEngine.TurnOutcome(GameEngine.TurnResult.CONTINUE, 0, new ArrayList<>());
    }

    private int askHuman(ArrayList<String> hand) {
        while (true) {
            System.out.print("Choose card index/code or draw: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("DRAW")) {
                return -1;
            }
            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < hand.size()) {
                    return index;
                }
            } catch (Exception ignored) {
            }
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).equals(input)) {
                    if (engine.isLegalPlay(hand.get(i))) {
                        return i;
                    }
                    System.out.println("That card is not legal.");
                }
            }
            System.out.println("Card not found.");
        }
    }

    private String askColor() {
        while (true) {
            System.out.print("Call color R/Y/G/B: ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("R")) {
                return "R";
            }
            if (input.equals("Y")) {
                return "Y";
            }
            if (input.equals("G")) {
                return "G";
            }
            if (input.equals("B")) {
                return "B";
            }
            System.out.println("Bad color.");
        }
    }

    static String formatHand(ArrayList<String> cards) {
        String out = "";
        for (int i = 0; i < cards.size(); i++) {
            out += i + ":" + cards.get(i);
            if (i < cards.size() - 1) {
                out += " ";
            }
        }
        return out;
    }
}
