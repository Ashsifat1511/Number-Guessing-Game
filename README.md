# Task 2 · Number Guessing Game

A console game. The computer picks a random number and you guess it, getting a "Too High!" or "Too Low!" hint after each try. It includes the bonus difficulty levels.

## Requirements

- JDK 17 or newer
- Maven 3.8 or newer

## Build, test, run

```bash
cd task2-guessing-game
mvn package                                   # compiles, runs 14 unit tests, builds the jar
java -jar target/number-guessing-game-1.0.0.jar
```

Run a single test with `mvn -Dtest=RoundTest#roundIsLostAfterMaxAttempts test`.

## How to play

1. Pick a difficulty: `1` Easy, `2` Medium, `3` Hard. Press **Enter** for Medium, or type `Q` to quit.
2. Type guesses. After each one you see **Too High!**, **Too Low!** or **Correct!** and the attempt counter (`[Attempt 3 of 7]`).
3. If you run out of attempts you see **You Lost!** and the number is revealed.
4. After each round a scoreboard lists every round so far, for example `Round 1 - guessed in 3 attempts`, followed by your total score.
5. Answer `y` or `n` to **Play again?**

| Level | Range | Attempts |
|-------|-------|----------|
| Easy | 1–50 | 10 |
| Medium (default; this is the core spec) | 1–100 | 7 |
| Hard | 1–200 | 5 |

## Rules chosen where the brief is silent

- **Invalid input never costs an attempt.** This covers text, empty lines, numbers outside the range, and numbers too big to read (for example `99999999999`). The game explains the problem and asks again.
- **Score:** a win earns `(max attempts − attempts used + 1) × 10` points, and a loss earns 0. On Medium, a first-guess win is 70 points and a last-guess win is 10.
- If input closes (Ctrl+Z on Windows, Ctrl+D on Linux/macOS), the game prints the final score and exits cleanly.
- The summary line uses a plain hyphen (`Round X - guessed in Y attempts`) instead of the brief's em dash (—), so that it displays correctly in the default Windows console.

## Features (brief checklist)

| Brief item | Where |
|------------|-------|
| Random number in range at the start of each round | `Game#playRound` (`java.util.Random`) |
| Guess input through `Scanner` | `Game#readLine` |
| "Too High!", "Too Low!", "Correct!" | `GuessResult`, `Round#guess` |
| Attempt counter always visible | `[Attempt n of max]` prompt plus "Attempts left" |
| Max attempts, then "You Lost!" with the number revealed | `Round#isOver`, `Game#playRound` |
| Play Again (y/n) | `Game#askYesNo` |
| Score tracking with "Round X - guessed in Y attempts" | `Scoreboard` |
| *(Bonus)* Easy / Medium / Hard | `Difficulty` enum (values held as constants) |

## Project layout

```
src/main/java/com/oasis/guessing/
├── Main.java         entry point
├── Game.java         console loop (I/O and Random are injected, so it can be tested)
├── Round.java        pure round logic
├── Scoreboard.java   per-round results, summary lines and points
├── Difficulty.java   levels: range and attempt limit
└── GuessResult.java  Too High / Too Low / Correct
src/test/java/com/oasis/guessing/   RoundTest (8), GameSessionTest (6 scripted full sessions)
```
