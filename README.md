# UNO CLI

Full UNO card game project: midterm refactor, Maven/Docker/logging (HW4), persistence (HW5), and final-project rules.

- [docs/rules-supported.md](docs/rules-supported.md) — implemented UNO rules
- [docs/final-report.md](docs/final-report.md) — architecture, tests, limitations
- [docs/database.md](docs/database.md) — persistence setup and reports

## Build

```bash
./mvnw compile
```

## Test

```bash
./mvnw test
```

Runs characterization tests (`GameTest`), 18 rubric-aligned final project tests (`FinalProjectRulesTest`), and persistence tests.

## Run

Play to 500 points (default):

```bash
./mvnw exec:java -Dexec.args="--bots 3"
```

With a human player:

```bash
./mvnw exec:java -Dexec.args="--human --bots 2"
```

Play exactly N rounds:

```bash
./mvnw exec:java -Dexec.args="--bots 3 --games 3"
```

Custom target score:

```bash
./mvnw exec:java -Dexec.args="--bots 3 --target 200"
```

During your turn: card index/code, `draw`, or `uno`.

Card codes: `R5` red 5, `YS` yellow skip, `BR` blue reverse, `G+2` green draw-two, `W` wild, `W4` wild draw-four.

Skip saving game history:

```bash
./mvnw exec:java -Dexec.args="--bots 3 --games 1 --no-persist"
```

View saved statistics:

```bash
./mvnw exec:java -Dexec.args="--report recent 5"
./mvnw exec:java -Dexec.args="--report wins"
./mvnw exec:java -Dexec.args="--report top-scores 10"
```

## Package

```bash
./mvnw package
```

Creates `target/uno-cli.jar`. Run directly:

```bash
java -jar target/uno-cli.jar --human --bots 2
java -jar target/uno-cli.jar --report recent
```

## Docker build

```bash
docker build -t uno-cli .
```

## Docker run

```bash
docker run --rm uno-cli
```

Interactive game:

```bash
docker run --rm -it uno-cli --human --bots 2 --games 1
```

Persist history outside the container:

```bash
docker run --rm -v "$(pwd)/data:/app/data" uno-cli --bots 3 --games 1
```

## Logging

`GameLog` uses `java.util.logging` for game events. Normal console output is unchanged.
