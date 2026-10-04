package com.oasis.guessing;

import java.util.Random;
import java.util.Scanner;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        new Game(new Scanner(System.in), System.out, new Random()).run();
    }
}
