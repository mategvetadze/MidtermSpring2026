# Extension-Readiness Note

## Easiest Extension: Smarter Bot

The fastest upgrade is to improve the bot logic in `Main.chooseBotCard()` and `Main.chooseBotColor()`.

Where to change:

- `src/Main.java`

Why it is easy:

- Bot decisions already live in two methods.
- `GameRules.isLegalPlay()` keeps the rules consistent.

## Another Easy Extension: Rule Variant

If you want a house rule, change `GameRules.isLegalPlay()` or add a second method and switch which one `Main` calls.

Where to change:

- `src/GameRules.java`
- `src/Main.java`

## What Still Makes Change Hard

- The game loop is still in `Main.java`, so rules and I/O are mixed. This keeps the refactor small, but big changes will still take time.

