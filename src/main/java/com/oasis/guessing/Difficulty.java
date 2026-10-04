package com.oasis.guessing;

/** Difficulty levels from the brief's bonus item. MEDIUM matches the core spec (1–100, 7 attempts). */
public enum Difficulty {
    EASY("Easy", 1, 50, 10),
    MEDIUM("Medium", 1, 100, 7),
    HARD("Hard", 1, 200, 5);

    private final String label;
    private final int min;
    private final int max;
    private final int maxAttempts;

    Difficulty(String label, int min, int max, int maxAttempts) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.maxAttempts = maxAttempts;
    }

    public String label() { return label; }
    public int min() { return min; }
    public int max() { return max; }
    public int maxAttempts() { return maxAttempts; }

    public String describe() {
        return String.format("%s (%d-%d, %d attempts)", label, min, max, maxAttempts);
    }
}
