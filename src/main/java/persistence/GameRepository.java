package persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import persistence.entity.GameEntity;
import persistence.entity.GameScoreEntity;
import persistence.entity.PlayerEntity;
import persistence.entity.RoundEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JPA repository for saving games and running history/statistics queries.
 */
public class GameRepository {

    public long saveSession(GameSessionResult session) {
        EntityManager em = PersistenceConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            Map<String, PlayerEntity> players = resolvePlayers(em, session.playerNames());
            PlayerEntity sessionWinner = determineSessionWinner(session, players);

            GameEntity game =
                    new GameEntity(
                            session.startedAt(),
                            session.completedAt(),
                            sessionWinner,
                            session.rounds().size());
            em.persist(game);

            for (int i = 0; i < session.rounds().size(); i++) {
                RoundResult round = session.rounds().get(i);
                RoundEntity roundEntity =
                        new RoundEntity(
                                i + 1,
                                players.get(round.winnerName()),
                                round.pointsScored(),
                                round.completedAt());
                game.addRound(roundEntity);
                em.persist(roundEntity);
            }

            for (int i = 0; i < session.playerNames().size(); i++) {
                GameScoreEntity scoreEntity =
                        new GameScoreEntity(players.get(session.playerNames().get(i)), session.finalScores()[i]);
                game.addScore(scoreEntity);
                em.persist(scoreEntity);
            }

            tx.commit();
            return game.getId();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<RecentGameSummary> findRecentGames(int limit) {
        EntityManager em = PersistenceConfig.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<GameEntity> query =
                    em.createQuery(
                            "SELECT g FROM GameEntity g JOIN FETCH g.winner ORDER BY g.completedAt DESC",
                            GameEntity.class);
            query.setMaxResults(limit);
            List<GameEntity> games = query.getResultList();
            List<RecentGameSummary> summaries = new ArrayList<>();
            for (GameEntity game : games) {
                summaries.add(toRecentSummary(em, game));
            }
            return summaries;
        } finally {
            em.close();
        }
    }

    public List<PlayerWinCount> findPlayerWinCounts() {
        EntityManager em = PersistenceConfig.getEntityManagerFactory().createEntityManager();
        try {
            List<Object[]> rows =
                    em.createQuery(
                                    "SELECT g.winner.name, COUNT(g) FROM GameEntity g GROUP BY g.winner.name ORDER BY COUNT(g) DESC, g.winner.name",
                                    Object[].class)
                            .getResultList();
            List<PlayerWinCount> counts = new ArrayList<>();
            for (Object[] row : rows) {
                counts.add(new PlayerWinCount((String) row[0], (Long) row[1]));
            }
            return counts;
        } finally {
            em.close();
        }
    }

    public List<HighScoreEntry> findHighestScores(int limit) {
        EntityManager em = PersistenceConfig.getEntityManagerFactory().createEntityManager();
        try {
            List<Object[]> rows =
                    em.createQuery(
                                    "SELECT gs.player.name, gs.score, g.id, g.completedAt "
                                            + "FROM GameScoreEntity gs JOIN gs.game g "
                                            + "ORDER BY gs.score DESC, g.completedAt DESC",
                                    Object[].class)
                            .setMaxResults(limit)
                            .getResultList();
            List<HighScoreEntry> entries = new ArrayList<>();
            for (Object[] row : rows) {
                entries.add(
                        new HighScoreEntry(
                                (String) row[0],
                                (Integer) row[1],
                                (Long) row[2],
                                (Instant) row[3]));
            }
            return entries;
        } finally {
            em.close();
        }
    }

    private Map<String, PlayerEntity> resolvePlayers(EntityManager em, List<String> playerNames) {
        Map<String, PlayerEntity> players = new HashMap<>();
        for (String name : playerNames) {
            List<PlayerEntity> existing =
                    em.createQuery("SELECT p FROM PlayerEntity p WHERE p.name = :name", PlayerEntity.class)
                            .setParameter("name", name)
                            .getResultList();
            PlayerEntity player = existing.isEmpty() ? new PlayerEntity(name) : existing.get(0);
            if (existing.isEmpty()) {
                em.persist(player);
            }
            players.put(name, player);
        }
        return players;
    }

    private PlayerEntity determineSessionWinner(GameSessionResult session, Map<String, PlayerEntity> players) {
        int bestScore = Integer.MIN_VALUE;
        String bestName = session.playerNames().get(0);
        for (int i = 0; i < session.playerNames().size(); i++) {
            if (session.finalScores()[i] > bestScore) {
                bestScore = session.finalScores()[i];
                bestName = session.playerNames().get(i);
            }
        }
        return players.get(bestName);
    }

    private RecentGameSummary toRecentSummary(EntityManager em, GameEntity game) {
        List<GameScoreEntity> scores =
                em.createQuery(
                                "SELECT gs FROM GameScoreEntity gs JOIN FETCH gs.player WHERE gs.game.id = :id ORDER BY gs.score DESC",
                                GameScoreEntity.class)
                        .setParameter("id", game.getId())
                        .getResultList();
        String scoreLine =
                scores.stream()
                        .map(score -> score.getPlayer().getName() + "=" + score.getScore())
                        .collect(Collectors.joining(", "));
        return new RecentGameSummary(
                game.getId(),
                game.getCompletedAt(),
                game.getWinner().getName(),
                game.getRoundsPlayed(),
                scoreLine);
    }
}
