package org.example;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final List<Player> players;
    private int currentPlayerIndex;
    private final Board board;

    public Game(int boardSize, List<String> playerNames) {
        this.players = new ArrayList<>();
        char[] pieces = {'X', 'O', 'A', 'B', 'C', 'D'}; // Add more pieces if needed
        for (int i = 0; i < playerNames.size(); i++) {
            players.add(new Player(playerNames.get(i), pieces[i]));
        }
        this.currentPlayerIndex = 0;
        this.board = new Board(boardSize);
    }

    public boolean makeMove(int row, int col) {
        if (row < 0 || row >= board.getSize() || col < 0 || col >= board.getSize()) {
            System.out.println("Invalid Move");
            return false;
        }
        if (board.isCellEmpty(row, col)) {
            board.placePiece(row, col, players.get(currentPlayerIndex).getPiece());
            switchPlayer();
            return true;
        }
        return false;
    }

    private void switchPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean checkWin() {
        return board.checkWin();
    }

    public boolean isBoardFull() {
        return board.isFull();
    }

    public void printBoard() {
        board.print();
    }

    public String getCurrentPlayerName() {
        return players.get(currentPlayerIndex).getName();
    }
}