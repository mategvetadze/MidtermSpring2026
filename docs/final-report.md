# Final Project Report

## Rubric Coverage Summary

| Rubric area | Points | Evidence |
|-------------|--------|----------|
| Correct deck composition | 5 | `Deck.buildStandardDeck()`, `Deck.isStandardComposition()`, `FinalProjectRulesTest.deckHas108Cards`, `deckContainsFourColorsNumberedCardsAndActionCards` |
| Legal play validation | 7 | `Card.isLegalOn()`, `GameRules`, `FinalProjectRulesTest.legalPlayMatchesColorNumberAndActionType`, `wildCardsArePlayableAndIllegalCardsAreRejected` |
| Skip | 5 | `GameEngine.applyCardEffect()`, `FinalProjectRulesTest.skipMakesNextPlayerLoseTurnInThreePlayerGame` |
| Reverse | 5 | `GameEngine.applyCardEffect()`, `FinalProjectRulesTest.reverseChangesDirectionForThreePlayers`, `reverseActsLikeSkipInTwoPlayerGame` |
| Draw Two | 5 | `GameEngine.applyCardEffect()`, `FinalProjectRulesTest.drawTwoAddsTwoCardsAndSkipsNextPlayer` |
| Wild | 5 | `GameEngine.playChosenCard()`, `FinalProjectRulesTest.wildSetsCalledColorThatAffectsLegalPlay` |
| Wild Draw Four | 5 | `GameRules.isWildDrawFourLegal()`, `FinalProjectRulesTest.wildDrawFourIsRestrictedWhenOtherPlaysExist`, `wildDrawFourDrawsFourSkipsTurnAndSetsColor` |
| Draw/pass behavior | 5 | `ConsoleGame`, `GameEngine.drawCard()`, `FinalProjectRulesTest.playerCanDrawAndPlayLegalDrawnCard`, `playerCanDrawAndPassWhenDrawnCardIsNotPlayable` |
| UNO call and penalty | 4 | `GameEngine.callUno()`, `applyMissedUnoPenalties()`, `FinalProjectRulesTest` UNO tests |
| Round scoring and target | 4 | `GameEngine.scoreOpponents()`, `GameState.isMatchOver()`, `GameMatch.playToTarget()`, scoring tests |
| Game design | 4 | Layered architecture below; engine tests run without CLI |
| CLI playability | 2 | `ConsoleGame` prompts, `Main --help`, invalid input handling |
| Documentation | 4 | This report, `README.md`, `docs/rules-supported.md` |

Additional characterization tests: `GameTest` (37 tests). Persistence tests: `persistence.GameRepositoryTest`.

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

### Characterization suite

`GameTest` (37 tests) documents core behavior for card parsing, legality, action cards, draw/pass, scoring, and turn effects.

### Final project JUnit suite

`FinalProjectRulesTest` (18 tests) maps directly to the final project rubric rule menu.

### Assignment 5 persistence tests

`persistence.GameRepositoryTest` covers save and query behavior with isolated in-memory H2.

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
