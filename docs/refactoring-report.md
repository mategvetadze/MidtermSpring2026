# Refactoring Report

## What Behavior I Characterized Before Refactoring

I added and ran characterization tests in `src/GameTest.java` before and during refactoring. The tests describe **this implementation**, not ideal UNO rules. They are run with:

```bash
scripts/test.sh
```

or, on Windows from the project root:

```powershell
javac -d out src\*.java
java -cp out Main --self-test
java -ea -cp out GameTest
```

The suite now has **37 tests** (plus 9 legacy checks in `Main.selfTest()`). Coverage includes:

- **Card parsing**: color, rank, number, and point values (`Card` / `GameRules`)
- **Legal play**: match by color, number, action type (skip, reverse, draw two), wild/wild draw four, called color, and wild top-card edge cases
- **Bot strategy**: prefers draw two over skip over numbers over wild; chooses the most common color in hand
- **Deck mechanics**: draw from pile, reshuffle discard when empty, return `W` when both piles are empty
- **Turn effects** (testable without the CLI): skip, reverse (3-player and 2-player), draw two, wild draw four, normal advance, UNO announcement, wild color call
- **Scoring**: end-of-hand scoring sums opponents' card points when a player wins
- **Game setup**: each player receives 7 cards; starting up card is not a wild
- **Documented quirks**: illegal index or illegal card causes penalty draw and turn loss; human may play or decline a drawn legal card; bot auto-plays a legal drawn card; draw without a legal play ends the turn

Turn-effect and scoring tests use `GameEngine` and a small `TestGameFixture` helper so full rule behavior can be verified without console input.

## Worst Design Problems I Found

1. **Everything lived in `Main.java`**: game loop, mutable global state, deck management, bot logic, and all console I/O were in one class with static fields.
2. **Duplicated legal-play logic**: the same matching rules appeared in the turn loop, bot selection, and helper methods before `GameRules` centralized them.
3. **Console mixed with rules**: prompts, printing, and turn resolution were interleaved, so rule behavior could only be verified by playing the full CLI game.
4. **Deck and discard were global lists**: draw, reshuffle, and discard behavior had no clear owner and were hard to test in isolation.

## Refactorings I Performed

Work was done in small, behavior-preserving steps. After each step, `Main --self-test` and `GameTest` were rerun.

| Step | Change |
|------|--------|
| 1 | Added `Card.java` and `GameRules.java` to centralize card parsing and legal-play checks |
| 2 | Added characterization tests for rules, bots, deck, and edge cases |
| 3 | Extracted `Deck.java` for build/shuffle, draw, discard, and reshuffle |
| 4 | Extracted `GameState.java` for mutable session state (players, hands, scores, turn order) |
| 5 | Extracted `GameEngine.java` for setup, `playChosenCard`, and card effects with no console I/O |
| 6 | Extracted `BotPlayer.java` for bot card and color selection |
| 7 | Extracted `ConsoleGame.java` for the game loop, prompts, and output |
| 8 | Reduced `Main.java` to argument parsing, wiring, and the original 9-check `selfTest()` |
| 9 | Updated `scripts/test.sh` to run `java -ea -cp out GameTest` |
| 10 | Added turn-effect, scoring, setup, and draw-behavior tests that exercise `GameEngine` directly |

### Class responsibilities after refactoring

- **`Main`**: CLI entry point and argument handling
- **`ConsoleGame`**: console input/output and turn orchestration
- **`GameEngine`**: rule execution and turn effects (no I/O)
- **`GameState`**: mutable game state
- **`Deck`**: draw pile and discard pile
- **`BotPlayer`**: bot decision strategy
- **`GameRules` / `Card`**: card representation and legal-play rules
- **`GameTest`**: characterization tests

## Behavior I Intentionally Preserved

All documented quirks from `docs/rules.html` remain unchanged:

- All hands are visible in the terminal
- Humans may type `draw` even when holding a legal card
- Illegal card index or illegal play causes a penalty draw and ends the turn
- Bots automatically play a legally drawn card
- Skip advances two players; reverse flips direction (and in a 2-player game acts like skip)
- Draw two and wild draw four make the next player draw and skip their turn
- Wild cards require a color call; bot picks the most common color in hand
- Safety turn limit of 3000 still applies
- Scoring and end-of-hand logic unchanged

Verification: `Main --self-test` (9 checks) and `GameTest` (37 checks) both pass. `scripts/test.sh` runs both. A manual CLI game with `--human --bots 2` runs correctly.

## Prior Evaluation Feedback Addressed

An earlier submission scored 64/100. These specific limitations were fixed:

| Prior limitation | Fix |
|------------------|-----|
| `GameTest` not run by `scripts/test.sh` | `test.sh` now runs `java -ea -cp out GameTest` |
| `Main.java` owned state, I/O, turns, deck, scoring | Extracted `GameState`, `GameEngine`, `ConsoleGame`, `Deck`, `BotPlayer` |
| Console I/O not separated from rule execution | `ConsoleGame` handles I/O; `GameEngine` handles rules and effects |
| Limited test coverage for turn effects | Direct `GameEngine` tests for skip, reverse, draw two, wild draw four, scoring, and draw quirks |

## Rubric Alignment

| Rubric area | How this submission addresses it |
|-------------|----------------------------------|
| Characterization tests | 37 focused tests plus `scripts/test.sh`; quirks and turn effects covered without the CLI |
| Incremental refactoring | Small extractions with tests rerun after each step; no behavior rewrite |
| Design improvement | `ConsoleGame` vs `GameEngine` separates I/O from rules; `Deck` and `GameState` have clear homes |
| Code quality | Short classes with named responsibilities; no superficial MVC renaming |
| Report and extension readiness | This report and `docs/extension-readiness.md` map changes to realistic next steps |

## Risks That Remain

1. **`ConsoleGame` still coordinates the human draw-then-maybe-play prompt** between I/O and engine calls. That interactive step still requires the CLI or a mocked `Scanner` for a full end-to-end test.
2. **Bot strategy is simple and fixed** in `BotPlayer`. Smarter bots would need new strategy code but should not require engine changes.
3. **Card codes are still strings** (`"R5"`, `"W4"`). A richer card model would touch several classes but is localized mainly in `Card` and `GameRules`.
4. **No replay or logging layer** exists yet. Adding one would hook into `ConsoleGame` or sit beside `GameEngine` event output.

## How To Run Checks

```bash
scripts/test.sh
```

```powershell
cd MidtermSpring2026
javac -d out src\*.java
java -cp out Main --self-test
java -ea -cp out GameTest
```
