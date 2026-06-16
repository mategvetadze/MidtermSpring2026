import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int bots = 3;
        int games = 1;
        boolean human = false;
        long seed = System.currentTimeMillis();
        boolean quiet = false;

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

        Random random = new Random(seed);
        ArrayList<Player> players = setupPlayers(bots, human);

        if (players.size() < 2 || players.size() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        int[] scores = new int[10];
        ConsoleUI ui = new ConsoleUI(quiet);

        for (int g = 1; g <= games; g++) {
            ui.printGameHeader(g);
            GameState state = new GameState(players, scores, random);
            Game game = new Game(state, ui);
            game.run();
        }

        ui.printFinalScores(players, scores);
    }

    private static ArrayList<Player> setupPlayers(int bots, boolean human) {
        ArrayList<Player> players = new ArrayList<>();
        if (human) {
            players.add(new Player("You", true));
        }
        for (int i = 1; i <= bots; i++) {
            players.add(new Player("Bot" + i, false));
        }
        return players;
    }
}
