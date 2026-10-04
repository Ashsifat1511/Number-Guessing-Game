package com.oasis.guessing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoundTest {

    @Test
    void responsesAreTooHighTooLowCorrect() {
        Round r = new Round(Difficulty.MEDIUM, 42);
        assertEquals(GuessResult.TOO_HIGH, r.guess(80));
        assertEquals(GuessResult.TOO_LOW, r.guess(10));
        assertEquals(GuessResult.CORRECT, r.guess(42));
        assertTrue(r.isWon());
        assertTrue(r.isOver());
        assertEquals(3, r.attemptsUsed());
    }

    @Test
    void messagesMatchTheBrief() {
        assertEquals("Too High!", GuessResult.TOO_HIGH.message());
        assertEquals("Too Low!", GuessResult.TOO_LOW.message());
        assertEquals("Correct!", GuessResult.CORRECT.message());
    }

    @Test
    void roundIsLostAfterMaxAttempts() {
        Round r = new Round(Difficulty.MEDIUM, 50);
        for (int i = 0; i < Difficulty.MEDIUM.maxAttempts(); i++) {
            assertFalse(r.isOver());
            r.guess(1);
        }
        assertTrue(r.isOver());
        assertFalse(r.isWon());
        assertEquals(0, r.attemptsLeft());
        assertThrows(IllegalStateException.class, () -> r.guess(50));
    }

    @Test
    void winningOnLastAttemptCountsAsWin() {
        Round r = new Round(Difficulty.HARD, 7);
        for (int i = 0; i < 4; i++) r.guess(100);
        assertEquals(GuessResult.CORRECT, r.guess(7));
        assertTrue(r.isWon());
    }

    @Test
    void outOfRangeGuessIsRejectedWithoutUsingAttempt() {
        Round r = new Round(Difficulty.EASY, 5);
        assertThrows(IllegalArgumentException.class, () -> r.guess(51));
        assertThrows(IllegalArgumentException.class, () -> r.guess(0));
        assertEquals(0, r.attemptsUsed());
    }

    @Test
    void targetMustBeInRange() {
        assertThrows(IllegalArgumentException.class, () -> new Round(Difficulty.EASY, 51));
    }

    @Test
    void difficultyLevelsMatchTheBrief() {
        assertEquals(50, Difficulty.EASY.max());
        assertEquals(10, Difficulty.EASY.maxAttempts());
        assertEquals(100, Difficulty.MEDIUM.max());
        assertEquals(7, Difficulty.MEDIUM.maxAttempts());
        assertEquals(200, Difficulty.HARD.max());
        assertEquals(5, Difficulty.HARD.maxAttempts());
        for (Difficulty d : Difficulty.values()) assertEquals(1, d.min());
    }

    @Test
    void scoringRewardsFewerAttempts() {
        assertEquals(70, Scoreboard.pointsFor(Difficulty.MEDIUM, 1));
        assertEquals(10, Scoreboard.pointsFor(Difficulty.MEDIUM, 7));
        Scoreboard sb = new Scoreboard();
        Round won = new Round(Difficulty.MEDIUM, 3);
        won.guess(50); won.guess(3);
        Round lost = new Round(Difficulty.HARD, 3);
        for (int i = 0; i < 5; i++) lost.guess(200);
        sb.record(won);
        sb.record(lost);
        assertEquals("Round 1 - guessed in 2 attempts  [Medium, 60 pts]", sb.entries().get(0).summaryLine());
        assertEquals("Round 2 - lost (the number was 3)  [Hard, 0 pts]", sb.entries().get(1).summaryLine());
        assertEquals(60, sb.totalPoints());
        assertEquals(1, sb.roundsWon());
    }
}
