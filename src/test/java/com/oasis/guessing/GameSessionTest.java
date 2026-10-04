package com.oasis.guessing;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/** Drives full console sessions with scripted input and a Random that returns a fixed value. */
class GameSessionTest {

    /** Random whose nextInt(bound) always returns a fixed offset, so target = min + offset. */
    private static Random fixed(int... offsets) {
        return new Random() {
            int i = 0;
            @Override
            public int nextInt(int bound) {
                return offsets[Math.min(i++, offsets.length - 1)];
            }
        };
    }

    private record Result(Game game, String output) {}

    private static Result play(String input, Random random) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Game g = new Game(new Scanner(input), new PrintStream(buf, true, StandardCharsets.UTF_8), random);
        g.run();
        return new Result(g, buf.toString(StandardCharsets.UTF_8));
    }

    @Test
    void winThenLoseAcrossTwoRoundsWithSummary() {
        // Round 1: Medium, target 42. Round 2: Hard, target 100, never guessed.
        String input = String.join("\n",
                "2", "50", "25", "42", "y",
                "3", "1", "1", "1", "1", "1", "n") + "\n";
        Result r = play(input, fixed(41, 99));

        assertTrue(r.output().contains("Too High!"));
        assertTrue(r.output().contains("Too Low!"));
        assertTrue(r.output().contains("Correct!"));
        assertTrue(r.output().contains("[Attempt 3 of 7]"));
        assertTrue(r.output().contains("Attempts left: 6"));
        assertTrue(r.output().contains("You Lost! The number was 100."));
        assertTrue(r.output().contains("Round 1 - guessed in 3 attempts"));
        assertTrue(r.output().contains("Round 2 - lost (the number was 100)"));
        assertTrue(r.output().contains("Won 1 of 2 round(s)"));
        assertEquals(2, r.game().scoreboard().roundsPlayed());
        assertEquals(50, r.game().scoreboard().totalPoints());
    }

    @Test
    void invalidInputDoesNotUseAttempts() {
        String input = String.join("\n", "1", "abc", "0", "51", "", "7", "n") + "\n";
        Result r = play(input, fixed(6)); // Easy, target 7
        assertTrue(r.output().contains("not a whole number"));
        assertTrue(r.output().contains("Out of range"));
        assertTrue(r.output().contains("Round 1 - guessed in 1 attempt "));
    }

    @Test
    void hugeNegativeAndPaddedInputsAreHandled() {
        // Medium, target 50
        String input = String.join("\n", "2", "99999999999999", "-5", "  50  ", "n") + "\n";
        Result r = play(input, fixed(49));
        assertTrue(r.output().contains("far too big"));
        assertTrue(r.output().contains("Out of range"));
        assertTrue(r.output().contains("Round 1 - guessed in 1 attempt "));
    }

    @Test
    void everyTargetStaysInsideTheLevelRange() {
        for (Difficulty d : Difficulty.values()) {
            int span = d.max() - d.min() + 1;
            // lowest and highest possible draws
            for (int offset : new int[]{0, span - 1}) {
                Result r = play(d.ordinal() + 1 + "\n" + (d.min() + offset) + "\nn\n", fixed(offset));
                assertTrue(r.output().contains("Correct!"), d + " offset " + offset);
            }
        }
    }

    @Test
    void emptyDifficultyDefaultsToMediumAndBadMenuInputIsReprompted() {
        String input = String.join("\n", "9", "", "1", "maybe", "n") + "\n";
        Result r = play(input, fixed(0)); // target 1
        assertTrue(r.output().contains("Please enter a number from 1 to 3"));
        assertTrue(r.output().contains("Medium. I'm thinking of a number between 1 and 100"));
        assertTrue(r.output().contains("Please answer y or n."));
    }

    @Test
    void quitFromMenuAndClosedInputEndCleanly() {
        assertTrue(play("q\n", fixed(0)).output().contains("Thanks for playing!"));
        Result closed = play("2\n50\n", fixed(10));
        assertTrue(closed.output().contains("(input closed)"));
        assertTrue(closed.output().contains("Thanks for playing!"));
    }
}
