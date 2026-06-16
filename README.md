# Midterm UNO CLI

This is a standalone CLI UNO-like game.

The code was originally written as feature-grown Java in one `Main` class. It has been refactored into separate classes for game state, rules, deck management, console I/O, and bot strategy while preserving behavior.

## Compile

```bash
scripts/compile.sh
```

## Run Bot Games

```bash
scripts/run.sh --bots 3 --games 5 --quiet
```

## Run Interactive Game

```bash
scripts/run.sh --human --bots 2 --games 1
```

Card input examples:

```text
R5   red 5
YS   yellow skip
BR   blue reverse
G+2  green draw two
W    wild
W4   wild draw four
draw draw a card
```

## Characterization Checks

```bash
scripts/test.sh
```

On Windows:

```bat
scripts\test.bat
```

This compiles the project, runs the 9 legacy checks in `Main --self-test`, then runs all **37** characterization tests in `GameTest` with assertions enabled (`-ea`).

## Project Structure

| Class | Responsibility |
|-------|----------------|
| `Main` | CLI entry point and argument parsing |
| `ConsoleGame` | Console input/output and turn orchestration |
| `GameEngine` | Rule execution, turn effects, and scoring (no I/O) |
| `GameState` | Mutable session state |
| `Deck` | Draw pile and discard pile |
| `BotPlayer` | Bot card and color selection |
| `Card` / `GameRules` | Card representation and legal-play rules |
| `GameTest` | Characterization tests runnable without the interactive CLI |

## Submission

Submit your work through GitHub:

1. Fork this repository to your GitHub account.
2. Clone your fork locally.
3. Complete the midterm work in your fork.
4. Commit your changes with clear commit messages.
5. Push your branch to GitHub.
6. Open a pull request from your fork back to the original repository.

Your pull request must include:

* refactored source code
* characterization tests
* `docs/refactoring-report.md`
* `docs/extension-readiness.md`

Do not submit a zip file instead of a pull request unless the instructor explicitly asks for it.

## Rules

See `docs/rules.html` for the implemented game rules.

## Midterm Materials

* `docs/midterm-exam.md`: midterm brief
* `docs/rubric.md`: grading rubric
* `docs/refactoring-guide.md`: suggested refactoring path
