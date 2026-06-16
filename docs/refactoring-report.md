# Refactoring Report

## Summary

Refactored the UNO CLI game to separate concerns, improve encapsulation, and make the codebase more testable and extensible. Major architectural improvements extracted game state, game loop, player management, and UI concerns into dedicated classes.

## What Changed

### Phase 1: Card & Rule Extraction (Completed)

1. **src/Card.java**
   - Encapsulates card parsing (color, rank, number, points)
   - Single `isLegalOn()` method for all play validation

2. **src/GameRules.java**
   - Central authority for all rule checks
   - Eliminates duplication
   - Static API for easy access

3. **src/Deck.java**
   - Manages draw and discard piles
   - Handles reshuffle when draw is empty
   - Centralized deck operations

### Phase 2: Architecture Refactoring (New)

4. **src/Player.java** (new)
   - Encapsulates player state: name, hand, human flag
   - Methods: `drawCard()`, `playCard()`, `handSize()`, `handScore()`
   - Replaces primitive obsession with object-oriented design

5. **src/GameState.java** (new)
   - Encapsulates mutable game state: players, deck, up card, current player, direction, scores
   - Provides clean API for state management
   - Separates state from logic

6. **src/Game.java** (new)
   - Contains the main game loop and turn execution
   - Orchestrates `GameState` and `ConsoleUI`
   - Methods: `run()`, `executeTurn()`, `applyCardEffect()`
   - Bot decision logic encapsulated in private methods

7. **src/ConsoleUI.java** (new)
   - All console I/O and user interaction
   - Methods: `showTurn()`, `askHumanCard()`, `askHumanColor()`, `printMessage()`
   - Separates UI from game logic
   - Respects `--quiet` flag

8. **src/Main.java** (refactored)
   - Now a thin entry point: ~60 lines
   - Argument parsing
   - Player setup
   - Game initialization and loop management
   - Delegates gameplay to `Game` class

### Phase 3: Testing (New)

9. **src/RuleTest.java** (new)
   - 12 tests for special card effects and game behavior
   - Tests: skip, reverse, draw two, wild draw four
   - Tests: player operations, game state, deck reshuffling
   - Run with: `java -ea RuleTest`

10. **scripts/test.sh** (updated)
    - Runs both `GameTest` and `RuleTest` with assertions enabled
    - Comprehensive test coverage

## Architecture

### Before

```
Main.java (900+ lines)
├─ Game state (global statics)
├─ Game loop
├─ Turn execution
├─ Player management (ArrayList of hands)
├─ Console I/O (Scanner, System.out)
└─ Bot logic
```

### After

```
Main.java (thin entry point)
├─ Argument parsing
├─ Player setup
└─ Game initialization

Game.java (game loop & turn execution)
├─ executeTurn()
├─ applyCardEffect()
└─ Bot decision logic

GameState.java (encapsulated state)
├─ players
├─ deck
├─ up card & called color
├─ current player & direction
└─ scores

Player.java (player abstraction)
├─ name & hand
├─ drawCard(), playCard()
└─ handScore()

ConsoleUI.java (all I/O)
├─ showTurn()
├─ askHumanCard()
├─ printMessage()
└─ printFinalScores()

Card.java, GameRules.java, Deck.java (utilities)
```

## Benefits

- **Separation of Concerns**: Game logic, state management, and UI are separate
- **Encapsulation**: State is managed by dedicated classes, not global variables
- **Testability**: Each class has a clear responsibility and can be tested independently
- **Readability**: `Game.run()` is clearer than 900-line `Main`
- **Extensibility**: Easy to add new features without touching existing code
- **Object-Oriented Design**: `Player`, `GameState`, `Game` are natural domain objects

## Tests Pass

All 24 characterization tests pass.
All 12 rule tests pass.
Original gameplay and scoring unchanged.

## Risk Mitigation

- Game behavior is identical to before refactoring
- All tests pass with assertions enabled
- Tests document expected behavior
- Gradual extraction reduced refactoring risk

## Ready for Future Work

- Advanced bot strategies
- Multiple human players
- New game variants
- Network multiplayer
- Save/load game state
- Undo/redo moves
