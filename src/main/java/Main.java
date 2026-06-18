import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.LogManager;
import persistence.GameReport;
import persistence.GameRepository;
import persistence.GameSessionResult;
import persistence.PersistenceConfig;
import persistence.RoundResult;

public class Main {
    static boolean quiet = false;
    static boolean verboseTests = false;

    public static void main(String[] args) {
        configureLogging();

        if (handleReportCommand(args)) {
            return;
        }

        int bots = 3;
        int games = 1;
        boolean human = false;
        boolean persist = true;
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
            } else if (args[i].equals("--no-persist")) {
                persist = false;
            } else if (args[i].equals("--seed") && i + 1 < args.length) {
                seed = Long.parseLong(args[++i]);
            } else if (args[i].equals("--self-test")) {
                selfTest();
                return;
            } else if (args[i].equals("--help")) {
                printHelp();
                return;
            }
        }

        Random random = new Random(seed);
        GameState state = new GameState();
        Deck deck = new Deck();
        GameEngine engine = new GameEngine(state, deck);
        Scanner scanner = new Scanner(System.in);
        ConsoleGame console = new ConsoleGame(state, engine, scanner);
        console.setQuiet(quiet);

        state.setupPlayers(bots, human);

        if (state.playerCount() < 2 || state.playerCount() > 4) {
            System.out.println("UNO needs 2 to 4 players.");
            return;
        }

        Instant sessionStart = Instant.now();
        ArrayList<RoundResult> roundResults = new ArrayList<>();

        for (int g = 1; g <= games; g++) {
            if (!quiet) {
                System.out.println("\n=== Game " + g + " ===");
            }
            GameLog.gameStart(g, state.playerCount());
            RoundResult roundResult = console.playGame(random, g);
            if (roundResult != null) {
                roundResults.add(roundResult);
            }
        }

        System.out.println("\nFinal scores:");
        for (int i = 0; i < state.playerNames.size(); i++) {
            System.out.println(state.playerNames.get(i) + ": " + state.scores[i]);
        }
        GameLog.sessionEnd();

        if (persist && !roundResults.isEmpty()) {
            try {
                GameRepository repository = new GameRepository();
                int[] finalScores = new int[state.playerNames.size()];
                for (int i = 0; i < finalScores.length; i++) {
                    finalScores[i] = state.scores[i];
                }
                GameSessionResult session =
                        new GameSessionResult(
                                sessionStart,
                                Instant.now(),
                                new ArrayList<>(state.playerNames),
                                finalScores,
                                roundResults);
                long gameId = repository.saveSession(session);
                if (!quiet) {
                    System.out.println("Saved game history as record #" + gameId + ".");
                }
            } catch (RuntimeException ex) {
                System.err.println("Could not save game history: " + ex.getMessage());
            } finally {
                PersistenceConfig.close();
            }
        }
    }

    private static boolean handleReportCommand(String[] args) {
        if (args.length == 0 || !args[0].equals("--report")) {
            return false;
        }

        GameReport report = new GameReport(new GameRepository());
        try {
            if (args.length < 2) {
                System.out.println("Usage: --report recent|wins|top-scores [limit]");
                return true;
            }

            int limit = 10;
            if (args.length >= 3) {
                limit = Integer.parseInt(args[2]);
            }

            switch (args[1]) {
                case "recent" -> report.printRecentGames(limit);
                case "wins" -> report.printPlayerWinCounts();
                case "top-scores" -> report.printHighestScores(limit);
                default -> System.out.println("Unknown report: " + args[1]);
            }
        } finally {
            PersistenceConfig.close();
        }
        return true;
    }

    private static void printHelp() {
        System.out.println(
                "Usage: scripts/run.sh [--bots N] [--games N] [--human] [--quiet] [--seed N] [--no-persist]");
        System.out.println("Reports: scripts/run.sh --report recent|wins|top-scores [limit]");
    }

    private static void configureLogging() {
        try (InputStream in = Main.class.getResourceAsStream("/logging.properties")) {
            if (in != null) {
                LogManager.getLogManager().readConfiguration(in);
            }
        } catch (IOException ignored) {
        }
    }

    static void selfTest() {
        int passed = 0;
        if (GameRules.getCardColor("R5").equals("R")) passed++; else fail("color R5");
        if (GameRules.getCardRank("G+2").equals("DRAW_TWO")) passed++; else fail("rank +2");
        if (GameRules.getCardPoints("W4") == 50) passed++; else fail("wild points");
        if (GameRules.isLegalPlay("R2", "R9", "")) passed++; else fail("same color");
        if (GameRules.isLegalPlay("G9", "R9", "")) passed++; else fail("same number");
        if (GameRules.isLegalPlay("B3", "W", "B")) passed++; else fail("called color");
        if (!GameRules.isLegalPlay("B3", "R9", "")) passed++; else fail("illegal mismatch");

        java.util.ArrayList<String> h = new java.util.ArrayList<>();
        h.add("B3");
        h.add("R4");
        h.add("W");
        if (BotPlayer.chooseCard(h, "R9", "") == 1) passed++; else fail("bot normal before wild");

        java.util.ArrayList<String> h2 = new java.util.ArrayList<>();
        h2.add("B1");
        h2.add("B2");
        h2.add("R3");
        if (BotPlayer.chooseColor(h2).equals("B")) passed++; else fail("bot color");

        System.out.println("Passed " + passed + " characterization checks.");
    }

    static void fail(String name) {
        throw new RuntimeException("Failed: " + name);
    }
}
