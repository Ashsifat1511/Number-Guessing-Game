package com.oasis.guessing;

/** State and rules of a single round. Pure logic, no I/O. */
public class Round {

    private final Difficulty difficulty;
    private final int target;
    private int attemptsUsed;
    private boolean won;

    public Round(Difficulty difficulty, int target) {
        if (target < difficulty.min() || target > difficulty.max()) {
            throw new IllegalArgumentException("Target " + target + " outside " + difficulty.describe());
        }
        this.difficulty = difficulty;
        this.target = target;
    }

    /** Evaluates a guess and counts it as an attempt. The guess must be inside the range. */
    public GuessResult guess(int value) {
        if (isOver()) {
            throw new IllegalStateException("Round is already over");
        }
        if (!isInRange(value)) {
            throw new IllegalArgumentException("Guess " + value + " is outside the range");
        }
        attemptsUsed++;
        if (value > target) {
            return GuessResult.TOO_HIGH;
        }
        if (value < target) {
            return GuessResult.TOO_LOW;
        }
        won = true;
        return GuessResult.CORRECT;
    }

    public boolean isInRange(int value) {
        return value >= difficulty.min() && value <= difficulty.max();
    }

    public boolean isOver() {
        return won || attemptsUsed >= difficulty.maxAttempts();
    }

    public boolean isWon() { return won; }
    public int attemptsUsed() { return attemptsUsed; }
    public int attemptsLeft() { return difficulty.maxAttempts() - attemptsUsed; }
    public int target() { return target; }
    public Difficulty difficulty() { return difficulty; }
}
