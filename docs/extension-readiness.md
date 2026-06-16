# Extension-Readiness

## Easiest: Improve Bot Strategy

Modify `Main.chooseBotCard()` and `Main.chooseBotColor()` methods.

**Why:** Bot decisions are already isolated in two methods. `GameRules.isLegalPlay()` ensures consistency.

## Next Easy: Add Rule Variants

Create a second rule-checking method in `GameRules.java` and toggle between them in `Main.playGame()`.

**Why:** `GameRules` is now the single source of truth for validation.

## Moderate: Separate I/O from Rules

Create `ConsoleUI` class (similar to existing design sketches) and move all `System.out.println()` and `Scanner` code there.

**Why:** `Main.java` will become smaller. Rules stay pure.

## After That: Extract Game Loop

Move `Main.playGame()` and related state into a separate `GameEngine` class.

**Why:** Allows testing game logic without I/O. Makes Main a thin runner.

## What Stays Stable

- `Card` class parsing rules (unlikely to change)
- `GameRules.isLegalPlay()` core contract (everything depends on it)
- Deck reshuffle behavior (tested and reliable)
- Scoring formula
