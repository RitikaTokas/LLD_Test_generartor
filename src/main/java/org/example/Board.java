package org.example;

public class Board {
    private static final int SIZE = 3;
    private final char[][] grid;

    public Board() {
        grid = new char[SIZE][SIZE];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = '-';
            }
        }
    }

    public boolean isCellEmpty(int row, int col) {
        return grid[row - 1][col - 1] == '-';
    }

    public void placePiece(int row, int col, char piece) {
        grid[row - 1][col - 1] = piece;
    }

    public boolean checkWin() {
        // Check rows, columns, and diagonals for a win
        for (int i = 0; i < SIZE; i++) {
            if (grid[i][0] != '-' && grid[i][0] == grid[i][1] && grid[i][1] == grid[i][2]) {
                return true; // Row win
            }
            if (grid[0][i] != '-' && grid[0][i] == grid[1][i] && grid[1][i] == grid[2][i]) {
                return true; // Column win
            }
        }
        if (grid[0][0] != '-' && grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2]) {
            return true; // Diagonal win
        }
        if (grid[0][2] != '-' && grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0]) {
            return true; // Anti-diagonal win
        }
        return false;
    }

    public boolean isFull() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public void print() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // Print each cell separated by a space but without a space at the end of the line
                System.out.print(grid[i][j]);
                if (j < SIZE - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println(); // move to the next line after printing all cells in a row
        }
    }
}
