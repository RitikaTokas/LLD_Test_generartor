package org.example;

public class Player {
    private final String name;
    private final char piece;

    public Player(String name, char piece) {
        this.name = name;
        this.piece = piece;
    }

    public String getName() {
        return name;
    }

    public char getPiece() {
        return piece;
    }
}
