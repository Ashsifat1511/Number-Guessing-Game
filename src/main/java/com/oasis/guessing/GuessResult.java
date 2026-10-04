package com.oasis.guessing;

/** The three responses the brief requires after each guess. */
public enum GuessResult {
    TOO_HIGH("Too High!"),
    TOO_LOW("Too Low!"),
    CORRECT("Correct!");

    private final String message;

    GuessResult(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
