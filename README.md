# UNO CLI

UNO card game from the midterm, converted to Maven with logging and Docker for HW4.

## Build

```bash
./mvnw compile
```

## Test

```bash
./mvnw test
```

Runs 9 unit checks from `Main --self-test` and 37 characterization tests from `GameTest`.

## Run

```bash
./mvnw exec:java -Dexec.args="--bots 3 --games 1"
```

With a human player:

```bash
./mvnw exec:java -Dexec.args="--human --bots 2 --games 1"
```

Card codes: `R5` red 5, `YS` yellow skip, `BR` blue reverse, `G+2` green draw-two, `W` wild, `W4` wild draw-four, `draw` to draw.

## Package

```bash
./mvnw package
```

Creates `target/uno-cli.jar`. Run directly:

```bash
java -jar target/uno-cli.jar --bots 3 --games 1
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

## Logging

Added `GameLog` class using `java.util.logging`. Logs game start, each player turn, cards played/drawn, invalid input, and game end. Normal console output is unchanged.
