package org.example;

public class Board {
    private final int size;
    private final char[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new char[size][size];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = '-';
            }
        }
    }

    public int getSize() {
        return size;
    }

    public boolean isCellEmpty(int row, int col) {
        return grid[row - 1][col - 1] == '-';
    }

    public void placePiece(int row, int col, char piece) {
        grid[row - 1][col - 1] = piece;
    }

    public boolean checkWin() {
        // Check rows and columns
        for (int i = 0; i < size; i++) {
            boolean rowWin = true;
            boolean colWin = true;
            char firstRow = grid[i][0];
            char firstCol = grid[0][i];
            if (firstRow == '-') rowWin = false;
            if (firstCol == '-') colWin = false;
            for (int j = 1; j < size; j++) {
                if (grid[i][j] != firstRow) rowWin = false;
                if (grid[j][i] != firstCol) colWin = false;
            }
            if (rowWin || colWin) return true;
        }

        // Check diagonals
        boolean diag1Win = true;
        boolean diag2Win = true;
        char firstDiag1 = grid[0][0];
        char firstDiag2 = grid[0][size - 1];
        if (firstDiag1 == '-') diag1Win = false;
        if (firstDiag2 == '-') diag2Win = false;
        for (int i = 1; i < size; i++) {
            if (grid[i][i] != firstDiag1) diag1Win = false;
            if (grid[i][size - 1 - i] != firstDiag2) diag2Win = false;
        }
        return diag1Win || diag2Win;
    }

    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public void print() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(grid[i][j]);
                if (j < size - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}