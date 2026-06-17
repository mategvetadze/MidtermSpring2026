import java.util.logging.Logger;

// logging helper for game events - added for HW4
public class GameLog {

    private static final Logger log = Logger.getLogger("uno");

    private GameLog() {}

    public static void gameStart(int gameNum, int players) {
        log.info("game " + gameNum + " started with " + players + " players");
    }

    public static void playerTurn(String name) {
        log.info(name + "'s turn");
    }

    public static void cardPlayed(String name, String card) {
        log.info(name + " played " + card);
    }

    public static void cardDrawn(String name, String card) {
        log.info(name + " drew " + card);
    }

    public static void invalidInput(String name, String reason) {
        log.warning(name + " invalid input: " + reason);
    }

    public static void gameEnd(String winner, int points) {
        log.info(winner + " wins, scored " + points + " points");
    }

    public static void sessionEnd() {
        log.info("session ended");
    }
}
