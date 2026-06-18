package persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "rounds")
public class RoundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "winner_id", nullable = false)
    private PlayerEntity winner;

    @Column(name = "points_scored", nullable = false)
    private int pointsScored;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt;

    protected RoundEntity() {}

    public RoundEntity(int roundNumber, PlayerEntity winner, int pointsScored, Instant completedAt) {
        this.roundNumber = roundNumber;
        this.winner = winner;
        this.pointsScored = pointsScored;
        this.completedAt = completedAt;
    }

    public Long getId() {
        return id;
    }

    public GameEntity getGame() {
        return game;
    }

    void setGame(GameEntity game) {
        this.game = game;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public PlayerEntity getWinner() {
        return winner;
    }

    public int getPointsScored() {
        return pointsScored;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
