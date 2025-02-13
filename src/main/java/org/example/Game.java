package org.example;

public class Game {
    private final Player playerX;
    private final Player playerO;
    private Player currentPlayer;
    private final Board board;

    public Game(String playerXName, String playerOName) {
        this.playerX = new Player(playerXName, 'X');
        this.playerO = new Player(playerOName, 'O');
        this.currentPlayer = playerX; // X always starts first
        this.board = new Board();
    }

    public boolean makeMove(int row, int col) {
        if (row < 1 || row > Board.SIZE || col < 1 || col > Board.SIZE) {
            System.out.println("Invalid Move");
            return false;
        }
        if (board.isCellEmpty(row, col)) {
            board.placePiece(row, col, currentPlayer.getPiece());
            switchPlayer();
            return true;
        }
        return false;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
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
        return currentPlayer.getName();
    }
}
