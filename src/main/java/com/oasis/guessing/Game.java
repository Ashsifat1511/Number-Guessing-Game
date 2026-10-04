package com.oasis.guessing;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

/**
 * Console front end. Input, output and randomness are injected so a whole session can be
 * scripted in tests. Invalid input (non-numbers, out-of-range guesses) never costs an attempt.
 */
public class Game {

    private final Scanner in;
    private final PrintStream out;
    private final Random random;
    private final Scoreboard scoreboard = new Scoreboard();

    public Game(Scanner in, PrintStream out, Random random) {
        this.in = in;
        this.out = out;
        this.random = random;
    }

    public void run() {
        out.println("==========================================");
        out.println("        NUMBER GUESSING GAME");
        out.println("==========================================");
        out.println("I pick a number, you guess it. I'll say Too High! or Too Low!");
        try {
            boolean again = true;
            while (again) {
                Optional<Difficulty> difficulty = askDifficulty();
                if (difficulty.isEmpty()) {
                    break;
                }
                playRound(difficulty.get());
                printScoreboard();
                again = askYesNo("Play again? (y/n): ");
            }
        } catch (EndOfInput e) {
            out.println();
            out.println("(input closed)");
        }
        out.println();
        out.println("Thanks for playing! Final score: " + scoreboard.totalPoints() + " pts over "
                + scoreboard.roundsPlayed() + " round(s).");
    }

    Scoreboard scoreboard() {
        return scoreboard;
    }

    private Optional<Difficulty> askDifficulty() {
        out.println();
        out.println("Choose a difficulty:");
        Difficulty[] levels = Difficulty.values();
        for (int i = 0; i < levels.length; i++) {
            out.println("  " + (i + 1) + ". " + levels[i].describe());
        }
        out.println("  Q. Quit");
        while (true) {
            out.print("Your choice [1-" + levels.length + ", Enter = Medium]: ");
            String line = readLine().trim();
            if (line.isEmpty()) {
                return Optional.of(Difficulty.MEDIUM);
            }
            if (line.equalsIgnoreCase("q")) {
                return Optional.empty();
            }
            Integer n = parseInt(line);
            if (n != null && n >= 1 && n <= levels.length) {
                return Optional.of(levels[n - 1]);
            }
            out.println("Please enter a number from 1 to " + levels.length + ", or Q.");
        }
    }

    private void playRound(Difficulty d) {
        int target = d.min() + random.nextInt(d.max() - d.min() + 1);
        Round round = new Round(d, target);
        out.println();
        out.printf("Round %d - %s. I'm thinking of a number between %d and %d.%n",
                scoreboard.roundsPlayed() + 1, d.label(), d.min(), d.max());

        while (!round.isOver()) {
            out.printf("[Attempt %d of %d] Enter your guess: ",
                    round.attemptsUsed() + 1, d.maxAttempts());
            String raw = readLine().trim();
            Integer guess = parseInt(raw);
            if (guess == null) {
                out.printf(isDigits(raw)
                        ? "That number is far too big - pick between %d and %d (no attempt used).%n"
                        : "That's not a whole number - pick between %d and %d (no attempt used).%n",
                        d.min(), d.max());
                continue;
            }
            if (!round.isInRange(guess)) {
                out.printf("Out of range - pick between %d and %d (no attempt used).%n", d.min(), d.max());
                continue;
            }
            GuessResult result = round.guess(guess);
            out.print(result.message());
            if (result != GuessResult.CORRECT && !round.isOver()) {
                out.print("  Attempts left: " + round.attemptsLeft());
            }
            out.println();
        }

        Scoreboard.Entry e = scoreboard.record(round);
        if (round.isWon()) {
            out.printf("You got it! The number was %d. Guessed in %d attempt(s), +%d pts.%n",
                    round.target(), round.attemptsUsed(), e.points());
        } else {
            out.println("You Lost! The number was " + round.target() + ".");
        }
    }

    private void printScoreboard() {
        out.println();
        out.println("---------------- Scoreboard ----------------");
        for (Scoreboard.Entry e : scoreboard.entries()) {
            out.println(e.summaryLine());
        }
        out.printf("Won %d of %d round(s) | Total score: %d pts%n",
                scoreboard.roundsWon(), scoreboard.roundsPlayed(), scoreboard.totalPoints());
        out.println("--------------------------------------------");
    }

    private boolean askYesNo(String prompt) {
        while (true) {
            out.print(prompt);
            String line = readLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes")) {
                return true;
            }
            if (line.equals("n") || line.equals("no")) {
                return false;
            }
            out.println("Please answer y or n.");
        }
    }

    private String readLine() {
        if (!in.hasNextLine()) {
            throw new EndOfInput();
        }
        return in.nextLine();
    }

    private static boolean isDigits(String s) {
        return s.matches("[+-]?\\d+");
    }

    private static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Thrown when stdin closes (Ctrl+D / Ctrl+Z or piped input runs out). */
    private static final class EndOfInput extends RuntimeException {
        EndOfInput() {
            super(null, null, false, false);
        }
    }
}
