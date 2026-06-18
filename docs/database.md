# UNO Database Persistence

Assignment 5 adds structured game history using **H2** and **JPA (Hibernate)**.

## Database

- Engine: [H2](https://www.h2database.com/) file database
- Default location: `./data/uno.mv.db` (created automatically on first save)
- Credentials are **not** stored in source code. Configure with environment variables or `src/main/resources/persistence.properties`.

| Setting | Environment variable | Default |
|---------|---------------------|---------|
| JDBC URL | `UNO_DB_URL` | `jdbc:h2:file:./data/uno` |
| User | `UNO_DB_USER` | `sa` |
| Password | `UNO_DB_PASSWORD` | *(empty)* |

Example override:

```bash
export UNO_DB_URL='jdbc:h2:file:./data/uno'
export UNO_DB_USER='sa'
export UNO_DB_PASSWORD=''
```

## ORM / Persistence Framework

- **JPA 3** with **Hibernate 6**
- Configuration: `src/main/resources/META-INF/persistence.xml`
- Entity classes: `src/main/java/persistence/entity/`
- Repository: `src/main/java/persistence/GameRepository.java`

Hibernate creates/updates tables automatically (`hibernate.hbm2ddl.auto=update`). The documented schema is also in `src/main/resources/db/schema.sql`.

## Schema

| Table | Purpose |
|-------|---------|
| `players` | Player names (unique) |
| `games` | One play session: start/end timestamp, winner, rounds played |
| `rounds` | Each UNO round within a session: round number, winner, points, timestamp |
| `game_scores` | Final cumulative score per player for each session |

Relationships:

- A **game** has many **rounds** and many **game_scores**
- Each **round** and **score** references a **player**
- The **game** winner is the player with the highest final session score

## What Gets Persisted

After each CLI session (unless `--no-persist` is used), the app saves:

- player names
- session start and completion timestamps
- number of rounds played (`--games N`)
- per-player final scores
- round winners and points scored
- overall session winner

## View Game History / Statistics

Report commands read from the database without starting a new game:

```bash
./mvnw exec:java -Dexec.args="--report recent 5"
./mvnw exec:java -Dexec.args="--report wins"
./mvnw exec:java -Dexec.args="--report top-scores 10"
```

Or with the packaged JAR:

```bash
java -jar target/uno-cli.jar --report recent
java -jar target/uno-cli.jar --report wins
java -jar target/uno-cli.jar --report top-scores
```

### Play and persist

```bash
./mvnw exec:java -Dexec.args="--bots 3 --games 2"
```

Skip persistence:

```bash
./mvnw exec:java -Dexec.args="--bots 3 --games 1 --no-persist"
```

## Run Persistence Tests

Persistence tests use an isolated in-memory H2 database (`jdbc:h2:mem:uno_test`) and do not depend on local files:

```bash
./mvnw test
```

The JUnit class `persistence.GameRepositoryTest` covers save, recent games, win counts, and highest scores.

## Docker note

The default file database path is relative to the working directory. For Docker, mount a volume if you want history to survive container restarts:

```bash
docker run --rm -v "$(pwd)/data:/app/data" uno-cli --bots 3 --games 1
```
