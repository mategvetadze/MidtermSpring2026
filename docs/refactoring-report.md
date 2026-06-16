# Refactoring Report

## Summary

Refactored the UNO CLI game to improve code organization and testability. The main changes remove rule duplication and extract card logic into dedicated classes.

## What Changed

1. **src/Card.java** (new)
   - Encapsulates card parsing (color, rank, number, points)
   - Single `isLegalOn()` method for all play validation

2. **src/GameRules.java** (new)
   - Central authority for all rule checks
   - Eliminates duplication previously in three places
   - Static API for easy access

3. **src/Deck.java** (new)
   - Manages draw and discard piles
   - Handles reshuffle when draw is empty
   - Provides `draw()`, `discard()`, `peekDiscard()` interface

4. **src/Main.java** (refactored)
   - Still contains game loop (intentional; minimal refactor)
   - Delegates rule checks to `GameRules`
   - Delegates deck operations to `Deck`
   - Removed duplication in `isLegal()`, `playGame()`, `chooseBotCard()`

5. **src/GameTest.java** (new)
   - 24 characterization tests
   - Tests card parsing, legal play rules, bot strategy, deck behavior
   - Tests turn effects: skip, reverse, draw two, wild draw four
   - Run with: `java -ea GameTest`

## Tests Pass

All 24 tests pass. Original gameplay and scoring unchanged.

## Risk Mitigation

- Game loop stays in `Main.java` to minimize refactoring scope
- All rule logic goes through `GameRules` as single source of truth
- Tests document current behavior before further changes

## Ready for Future Work

- Extract game loop into `GameEngine` class (next refactor)
- Separate console I/O into `ConsoleUI` class
- Bot strategy improvements
- Additional rule variants
