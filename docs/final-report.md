# Final Project Report

## Implemented UNO Rules

The final project builds on the midterm refactor, Assignment 4 tooling, and Assignment 5 persistence.

Implemented rule features:

- **Deck**: Standard 108-card UNO deck with validated composition
- **Legal play**: Color, number, action-type, and called-color matching
- **Action cards**: Skip, Reverse, Draw Two
- **Wild cards**: Wild and Wild Draw Four with color choice
- **Wild Draw Four restriction**: Playable only when no other legal card is available
- **Draw/pass**: Draw one card, optionally play it, otherwise pass turn
- **UNO call**: Human players type `uno`; bots call automatically
- **Missed UNO penalty**: Two-card penalty if UNO is not called before the next turn check
- **Round scoring**: Winner scores opponents' remaining card values
- **Target-score match**: Play continues across rounds until a player reaches the target (default 500)

See `docs/rules-supported.md` for the full rule checklist and simplifications.

## How To Play From The CLI

```bash
./mvnw exec:java -Dexec.args="--human --bots 2"
```

Common options:

- `--human` include a human player
- `--bots N` number of bot players (2-4 total players)
- `--target N` play until someone reaches `N` points (default 500)
- `--games N` play exactly `N` rounds instead of target mode
- `--seed N` reproducible shuffle
- `--quiet` reduce console output

During your turn, enter:

- a card index or code to play
- `draw` to draw
- `uno` to call UNO when you have one card

View saved history (Assignment 5):

```bash
./mvnw exec:java -Dexec.args="--report recent"
./mvnw exec:java -Dexec.args="--report wins"
./mvnw exec:java -Dexec.args="--report top-scores"
```

## Architecture: Game Logic vs CLI

The project separates concerns into layers:

| Layer | Classes | Responsibility |
|-------|---------|----------------|
| Rules | `Card`, `GameRules`, `Deck` | Card parsing, legality, scoring values, deck composition |
| Engine | `GameEngine`, `GameState` | Turn execution, penalties, action effects, round outcome |
| Match flow | `GameMatch` | Multi-round play to target score |
| CLI | `ConsoleGame`, `Main` | Input/output, prompts, reports |
| Bots | `BotPlayer` | Automated card/color choice |
| Persistence | `persistence.*` | Optional game history (Assignment 5) |

`GameEngine` has no `Scanner` or `System.out` usage. Tests call `GameEngine`, `GameRules`, and `Deck` directly without the CLI.

## Tests Added

### Existing characterization suite

`GameTest` (37 tests) documents core behavior for card parsing, legality, action cards, draw/pass, scoring, and turn effects.

### Assignment 5 persistence tests

`persistence.GameRepositoryTest` covers save and query behavior with isolated in-memory H2.

### Final project JUnit tests

`finalproject.FinalProjectRulesTest` adds focused tests for:

- standard deck composition (108 cards)
- Wild Draw Four legality restriction
- UNO call and missed-UNO penalty
- round scoring
- target-score match completion
- draw/pass turn ending

Run all tests:

```bash
./mvnw test
```

## Limitations

- No Draw Two / Wild Draw Four stacking
- No official Wild Draw Four challenge flow
- Bots use a simple heuristic strategy
- Human UNO enforcement is turn-based, not real-time between players
- Maximum four players
- Illegal plays cause a one-card penalty (project convention from midterm)

## Build And Run

```bash
./mvnw test
./mvnw package
java -jar target/uno-cli.jar --human --bots 2 --target 500
```

Docker:

```bash
docker build -t uno-cli .
docker run --rm -it uno-cli --human --bots 2 --games 1
```
