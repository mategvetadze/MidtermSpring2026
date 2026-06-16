# Extension-Readiness Guide

The refactored architecture makes it easy to extend the game in many directions.

## Easy Extensions

### Improve Bot Strategy

**File:** `Game.java` → `chooseBotCard()` and `chooseBotColor()`

**Why:** Bot decisions are already isolated. Easy to add new heuristics.

**Example:**
```java
// Prefer cards that leave you with one card (blocking opponents)
// Consider opponent hand sizes
// Play defensively vs. aggressively based on position
```

---

### Add New Rule Variants

**File:** `GameRules.java` → add `isLegalPlayHouseRules()`

**Why:** `GameRules` is the single source of truth. Toggle variants in `GameState`.

**Example:**
```java
// 7-0 rule (play 7, rotate; play 0, reverse order)
// Jump-in (play same card as up card out of turn)
// Stacking draw cards (stack +2 on +2 for +4)
```

---

### Track Game Statistics

**File:** `GameState.java` → add fields

**Why:** State is already encapsulated. Add counters naturally.

**Example:**
```java
private int turnsPlayed;
private int[] wildCardsPlayed;
private int[] penaltyCardsDrawn;
```

---

## Moderate Extensions

### Separate Console I/O Fully

**File:** Create `CLI.java` and `BatchRunner.java`

**Why:** `ConsoleUI` already handles I/O. Split interactive vs. programmatic.

**Example:**
```java
ConsoleUI ui = new ConsoleUI(quiet);  // human-interactive
BatchRunner runner = new BatchRunner(ui);  // testing/statistics
```

---

### Add Human vs. Human Games

**File:** `ConsoleUI.java` → `askHumanCard()` already supports multiple humans

**Why:** No refactoring needed. Just set `human=true` for multiple players.

**Change:** Remove check that limits one human player.

---

### Implement Move Objects

**File:** Create `Move.java` hierarchy

**Why:** Encapsulates turn actions for undo/redo.

**Example:**
```java
public abstract class Move {
    abstract void execute(GameState state);
    abstract void undo(GameState state);
}

class PlayCardMove extends Move { }
class DrawCardMove extends Move { }
class PassMove extends Move { }
```

---

## Harder Extensions

### Save / Load Game State

**File:** Add `GameState.serialize()` and `GameState.deserialize()`

**Why:** `GameState` holds all state. JSON serialization is straightforward.

**Example:**
```java
// Save:
String json = GameStateSerializer.toJson(state);
Files.write(Path.of("game.json"), json);

// Load:
GameState state = GameStateSerializer.fromJson(content);
game.run();
```

---

### Network Multiplayer

**File:** Create `GameServer.java` and `GameClient.java`

**Why:** `Game` logic is independent of I/O. Network I/O is just another `ConsoleUI`.

**Example:**
```java
// Server sends game state to all clients
// Clients send moves back to server
// Server updates state and broadcasts
```

---

### Undo / Redo

**File:** Use `Move` objects (see above) + `GameHistory`

**Why:** With move capture, undo/redo becomes trivial.

**Example:**
```java
private ArrayList<Move> history;

public void undo() {
    Move last = history.remove(history.size() - 1);
    last.undo(state);
}
```

---

## What Stays Stable

These are unlikely to change and can be depended on:

- `Card` class parsing rules
- `GameRules.isLegalPlay()` core contract
- `Deck` reshuffle behavior
- Scoring formula
- Player turn rotation
- Action card effects (skip, reverse, draw two, wild draw four)

---

## Design Principles

1. **State is centralized** → All game state lives in `GameState`
2. **Rules are centralized** → All rules live in `GameRules`
3. **I/O is separate** → `ConsoleUI` handles all user interaction
4. **Game logic is pure** → `Game.executeTurn()` uses state and rules
5. **Tests document behavior** → Tests in `GameTest.java` and `RuleTest.java`

Follow these principles when extending:

- Add new features to the class responsible for that concern
- Add new tests when adding new behavior
- Keep `Main.java` thin
- Avoid adding logic to `ConsoleUI`
- Route all rule changes through `GameRules`
