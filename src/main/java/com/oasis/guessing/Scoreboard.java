package com.oasis.guessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Keeps the result of every finished round and produces the multi-round summary. */
public class Scoreboard {

    /** Points for a win = (attempts left before the winning guess) x this value. */
    public static final int POINTS_PER_SPARE_ATTEMPT = 10;

    public record Entry(int roundNumber, Difficulty difficulty, boolean won, int attempts, int target, int points) {
        public String summaryLine() {
            String outcome = won
                    ? "guessed in " + attempts + (attempts == 1 ? " attempt" : " attempts")
                    : "lost (the number was " + target + ")";
            return String.format("Round %d - %s  [%s, %d pts]", roundNumber, outcome, difficulty.label(), points);
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    public Entry record(Round round) {
        if (!round.isOver()) {
            throw new IllegalStateException("Round is not over yet");
        }
        int points = round.isWon() ? pointsFor(round.difficulty(), round.attemptsUsed()) : 0;
        Entry e = new Entry(entries.size() + 1, round.difficulty(), round.isWon(), round.attemptsUsed(),
                round.target(), points);
        entries.add(e);
        return e;
    }

    /** First-guess win scores maxAttempts x 10; a win on the last attempt scores 10. */
    public static int pointsFor(Difficulty d, int attemptsUsed) {
        return (d.maxAttempts() - attemptsUsed + 1) * POINTS_PER_SPARE_ATTEMPT;
    }

    public List<Entry> entries() { return Collections.unmodifiableList(entries); }
    public int roundsPlayed() { return entries.size(); }
    public long roundsWon() { return entries.stream().filter(Entry::won).count(); }
    public int totalPoints() { return entries.stream().mapToInt(Entry::points).sum(); }
}
