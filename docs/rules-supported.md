# Supported UNO Rules

This document maps the course UNO rule menu to this project. The local reference is `docs/rules.html` and standard Mattel UNO rules.

## Implemented Rules

| Rule feature | Status | Notes |
|--------------|--------|-------|
| Correct deck composition | Implemented | 108-card standard deck: one 0 per color, two of 1-9 per color, two each of Skip/Reverse/Draw Two per color, four Wild and four Wild Draw Four |
| Legal play validation | Implemented | Match by color, number, action type, or called color after a wild |
| Skip | Implemented | Next player loses a turn |
| Reverse | Implemented | Reverses direction; in two-player games acts like skip |
| Draw Two | Implemented | Next player draws two cards and loses a turn |
| Wild | Implemented | Player chooses the active color |
| Wild Draw Four | Implemented | Next player draws four and loses a turn; only legal when no other card can be played |
| Draw/pass behavior | Implemented | Player may draw one card and optionally play it; otherwise turn passes |
| UNO call and missed-UNO penalty | Implemented | Human types `uno` while holding one card; bots call automatically; missed call draws 2 at next turn check |
| Round scoring | Implemented | Round winner scores total point value of opponents' remaining cards |
| Multi-round game to target score | Implemented | Default match plays to 500 points (`--target N`); use `--games N` for a fixed number of rounds |

## Simplifications And Variants

- **Starting card**: If the first flipped card is wild, it is reshuffled until a non-wild card appears.
- **Wild Draw Four challenge**: Official challenge rules are not implemented; legality is enforced only by the "no other legal play" restriction.
- **Stacking**: Draw Two and Wild Draw Four stacking is not supported.
- **Official UNO scoring to 500**: Implemented as cumulative match score across rounds.
- **Illegal play penalty**: Illegal card or index causes one penalty draw and ends the turn (project behavior retained from midterm).
- **Persistence**: Optional database history from Assignment 5 remains available.

## CLI Commands Related To Rules

| Input | Meaning |
|-------|---------|
| Card index (`0`, `1`, ...) | Play that card from your hand |
| Card code (`R5`, `YS`, `W`) | Play by card code |
| `draw` | Draw one card |
| `uno` | Call UNO while you have one card |

## Match Modes

```bash
# Play until someone reaches 500 points (default)
./mvnw exec:java -Dexec.args="--bots 3 --human"

# Play to a custom target
./mvnw exec:java -Dexec.args="--bots 3 --target 200"

# Play exactly three rounds
./mvnw exec:java -Dexec.args="--bots 3 --games 3"
```

## Test Mapping (Final Project Rubric)

| Rubric item | Test class | Test method(s) |
|-------------|------------|----------------|
| Deck composition | `FinalProjectRulesTest` | `deckHas108Cards`, `deckContainsFourColorsNumberedCardsAndActionCards` |
| Legal play | `FinalProjectRulesTest`, `GameTest` | `legalPlayMatchesColorNumberAndActionType`, `wildCardsArePlayableAndIllegalCardsAreRejected` |
| Skip | `FinalProjectRulesTest`, `GameTest` | `skipMakesNextPlayerLoseTurnInThreePlayerGame` |
| Reverse | `FinalProjectRulesTest`, `GameTest` | `reverseChangesDirectionForThreePlayers`, `reverseActsLikeSkipInTwoPlayerGame` |
| Draw Two | `FinalProjectRulesTest`, `GameTest` | `drawTwoAddsTwoCardsAndSkipsNextPlayer` |
| Wild | `FinalProjectRulesTest`, `GameTest` | `wildSetsCalledColorThatAffectsLegalPlay` |
| Wild Draw Four | `FinalProjectRulesTest`, `GameTest` | `wildDrawFourIsRestrictedWhenOtherPlaysExist`, `wildDrawFourDrawsFourSkipsTurnAndSetsColor` |
| Draw/pass | `FinalProjectRulesTest`, `GameTest` | `playerCanDrawAndPlayLegalDrawnCard`, `playerCanDrawAndPassWhenDrawnCardIsNotPlayable` |
| UNO call/penalty | `FinalProjectRulesTest`, `GameTest` | `oneCardStateIsDetectedAndMissedUnoDrawsPenalty`, `callingUnoPreventsPenalty`, `botAutomaticallyCallsUnoAtOneCard` |
| Scoring/target | `FinalProjectRulesTest`, `GameTest` | `roundWinnerScoresRemainingOpponentCards`, `matchContinuesUntilTargetScoreAndDeterminesWinner` |
