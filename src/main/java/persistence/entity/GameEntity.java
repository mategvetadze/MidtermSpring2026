package persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "winner_id", nullable = false)
    private PlayerEntity winner;

    @Column(name = "rounds_played", nullable = false)
    private int roundsPlayed;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoundEntity> rounds = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameScoreEntity> scores = new ArrayList<>();

    protected GameEntity() {}

    public GameEntity(Instant startedAt, Instant completedAt, PlayerEntity winner, int roundsPlayed) {
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.winner = winner;
        this.roundsPlayed = roundsPlayed;
    }

    public Long getId() {
        return id;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public PlayerEntity getWinner() {
        return winner;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public List<RoundEntity> getRounds() {
        return rounds;
    }

    public List<GameScoreEntity> getScores() {
        return scores;
    }

    public void addRound(RoundEntity round) {
        rounds.add(round);
        round.setGame(this);
    }

    public void addScore(GameScoreEntity score) {
        scores.add(score);
        score.setGame(this);
    }
}
