# Refactoring Report

I kept the refactor small and close to the current code. The goal was to remove rule duplication without changing how the game plays.

## What I Tested First

I wrote 23 characterization tests in `src/GameTest.java`. They cover:

- card parsing (color, rank, number, points)
- legal play rules (color/number/action/wild/called color)
- bot choice order and called color choice
- draw pile reshuffle and empty-deck fallback
- edge cases like invalid index and drawn-card handling

## Biggest Problems I Found

1. Legal-play rules were repeated in three places (`playGame`, `chooseBotCard`, `isLegal`).
2. Card logic was spread across multiple helpers with no clear single home.

## What I Changed

1. Added `src/Card.java` to hold card parsing and points.
2. Added `src/GameRules.java` as the single place for legal-play checks and card accessors.
3. Updated `Main.playGame`, `Main.chooseBotCard`, and `Main.isLegal` to use `GameRules`.

## What Stayed The Same

- The 9 original `selfTest()` checks still pass.
- All 23 new tests pass.
- Gameplay rules and quirks are unchanged.

## Risks That Remain

- The game loop is still in `Main.java`, so I/O and rules are still mixed. This was intentional to keep the refactor minimal.

## Optional Next Step

- If you want a bigger change later, extract the game loop into a small class and leave `Main` as a thin runner.

