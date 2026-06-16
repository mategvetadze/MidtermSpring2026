# Extension-Readiness Note

## Best-Supported Extension: Smarter Bot Strategy

The design now supports a **smarter bot** as the easiest next change.

### Where to implement

- **`src/BotPlayer.java`** — replace or extend `chooseCard()` and `chooseColor()`
- Optionally introduce a `PlayerStrategy` interface if multiple bot personalities are needed

### Why it is easy

- Bot decisions are no longer mixed into the game loop or `Main`.
- `BotPlayer.chooseCard()` already receives the hand, up card, and called color.
- `GameRules.isLegalPlay()` remains the single authority for legality, so a new bot cannot accidentally invent different rules.
- `GameEngine` does not care how a card index was chosen; it only executes the play.

### Example change

A smarter bot could prefer playing cards that leave fewer opponents, hold wilds longer, or count remaining colors. All of that logic stays in `BotPlayer` without touching `ConsoleGame` or `GameEngine`.

---

## Second-Easiest Extension: New Card Effect or Rule Variant

### Where to implement

- **New card effect**: `GameEngine.applyCardEffect()` and `GameRules.getCardEffect()`
- **House rule / variant**: `GameRules.isLegalPlay()` or a sibling method selected at startup in `Main`

### Why it is plausible

- Turn effects (skip, reverse, draw two, wild draw four) are already isolated in `GameEngine.applyCardEffect()`.
- `GameTest` includes direct tests for each effect via `GameEngine`, so a new effect can get tests without running the CLI.
- Legal-play rules live in one place (`GameRules` / `Card`), not scattered across the loop.

### Example change

A “draw three” variant would add a rank branch in `applyCardEffect()`, a parsing case in `Card`, and a new characterization test — without rewriting `ConsoleGame`.

---

## Third Extension: Replay Log or Better CLI View

### Where to implement

- **Replay log**: collect `GameEngine.TurnOutcome.events` in `ConsoleGame` or a new `GameLogger` class
- **Better view**: replace or wrap output in `ConsoleGame` (formatting hands, hiding opponent cards, etc.)

### Why it is plausible

- `GameEngine` already returns event strings (plays, draws, UNO, wins) separate from printing.
- `ConsoleGame` owns all `System.out` / `Scanner` usage, so a different view does not require rule changes.

---

## What Still Makes Change Hard

1. **Human draw-and-decide flow** — After drawing, a human is prompted whether to play the card. That logic lives in `ConsoleGame.playOneTurn()` and is awkward to test without mocking `Scanner`.
2. **String card codes everywhere** — Hands and piles are `ArrayList<String>`. A full card object model through the engine would be a larger migration, though `Card` already encapsulates parsing.
3. **Single console front-end** — There is no abstraction over “view” yet; swapping to a GUI would mean rewriting `ConsoleGame`, even though `GameEngine` is reusable.
4. **Global scoring in `GameState`** — Multi-round tournament rules or per-hand history would need more structure around `GameState.scores` and session lifecycle in `Main`.

---

## Summary

| Extension | Primary file(s) | Difficulty |
|-----------|-----------------|------------|
| Smarter bot | `BotPlayer.java` | Low |
| New card effect | `GameEngine.java`, `Card.java`, `GameTest.java` | Low–medium |
| Rule variant | `GameRules.java` | Low–medium |
| Replay log | `ConsoleGame.java` or new logger | Medium |
| GUI / new view | New view class; keep `GameEngine` | Medium–high |

The refactor’s main win is that **rules and turn effects are testable without the CLI**. `GameTest` exercises `GameEngine` directly for skip, reverse, draw two, wild draw four, scoring, and draw quirks. The main remaining coupling is the **interactive human draw prompt** in `ConsoleGame`.
