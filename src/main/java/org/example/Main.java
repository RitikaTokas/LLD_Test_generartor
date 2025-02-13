package org.example;

import java.io.*;
import java.util.*;

public class Main {
  public static void main(String[] args) {
    try {
      List<String> inputLines = readLines("test_input.txt");
      List<String> expectedLines = readLines("expected_output.txt");

      Iterator<String> inputIterator = inputLines.iterator();
      Iterator<String> expectedIterator = expectedLines.iterator();

      int testCaseNum = 1;
      while (inputIterator.hasNext()) {
        // Read board size and number of players
        int boardSize = Integer.parseInt(inputIterator.next().trim());
        int numPlayers = Integer.parseInt(inputIterator.next().trim());

        // Read player names
        List<String> playerNames = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
          playerNames.add(inputIterator.next().trim());
        }

        // Read moves
        List<String> testCaseInput = new ArrayList<>();
        String line;
        while (inputIterator.hasNext() && !(line = inputIterator.next()).isEmpty()) {
          testCaseInput.add(line);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream originalOut = System.out;
        System.setOut(printStream);

        simulateTestCase(boardSize, numPlayers, playerNames, testCaseInput);

        System.setOut(originalOut);

        String actualOutput = outputStream.toString().trim();
        StringBuilder expectedOutput = new StringBuilder();
        while (expectedIterator.hasNext() && !(line = expectedIterator.next()).isEmpty()) {
          expectedOutput.append(line).append("\n");
        }

        if (actualOutput.equals(expectedOutput.toString().trim())) {
          System.out.println("Test Case " + testCaseNum + " Passed!");
        } else {
          System.out.println("Test Case " + testCaseNum + " Failed!");
          System.out.println("Actual Output:");
          System.out.println(actualOutput);
          System.out.println("Expected Output:");
          System.out.println(expectedOutput.toString().trim());
        }

        testCaseNum++;
        if (inputIterator.hasNext()) {
          System.out.println();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void simulateTestCase(int boardSize, int numPlayers, List<String> playerNames, List<String> inputLines) {
    Scanner scanner = new Scanner(String.join("\n", inputLines));
    Game game = new Game(boardSize, playerNames);

    boolean everMoved = false;        // Track if any move has been made
    boolean printedAfterMove = false; // Ensure board prints only immediately after a valid move

    while (scanner.hasNextLine()) {
      String input = scanner.nextLine().trim();
      if (input.equalsIgnoreCase("exit")) {
        break;
      }

      String[] parts = input.split(" ");
      if (parts.length != 2) {
        System.out.println("Invalid Move");
        continue;
      }

      try {
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        if (game.makeMove(row, col)) {
          game.printBoard();
          everMoved = true;
          printedAfterMove = true;

          if (game.checkWin()) {
            System.out.println(game.getCurrentPlayerName() + " won the game");
            break;
          }

          if (game.isBoardFull()) {
            System.out.println("Game Over");
            break;
          }
        } else {
          System.out.println("Invalid Move");
        }
      } catch (NumberFormatException e) {
        System.out.println("Invalid Move");
      }
    }

    // Print the final game board state only if moves were made and last print wasn't immediately after a valid move
    if (everMoved && !printedAfterMove) {
      game.printBoard();
    }

    System.out.println("Game Over");
    scanner.close();
  }

  private static List<String> readLines(String filename) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    List<String> lines = new ArrayList<>();
    String line;
    while ((line = reader.readLine()) != null) {
      if (!line.trim().isEmpty() || line.isEmpty()) {
        lines.add(line);
      }
    }
    reader.close();
    return lines;
  }
}