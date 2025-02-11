package org.example;

import java.io.*;
import java.util.*;

public class Main {
  public static void main(String[] args) {
    try {
      // Read input from test_input.txt
      BufferedReader inputReader = new BufferedReader(new FileReader("test_input.txt"));
      System.out.println("file read");
      List<String> inputLines = new ArrayList<>();
      String line;
      while ((line = inputReader.readLine()) != null) {
        inputLines.add(line);
      }
      inputReader.close();

      // Redirect System.out to capture the output
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      PrintStream printStream = new PrintStream(outputStream);
      System.setOut(printStream);

      // Simulate user input
      Scanner scanner = new Scanner(new ByteArrayInputStream(String.join("\n", inputLines).getBytes()));

      // Input player names
      String playerXName = scanner.nextLine().split(" ")[1];
      String playerOName = scanner.nextLine().split(" ")[1];
      System.out.println(playerXName);

      // Initialize the game
      Game game = new Game(playerXName, playerOName);

      // Print initial board
      game.printBoard();

      // Game loop
      while (scanner.hasNextLine()) {
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("exit")) {
          System.out.println("Game Over");
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

          if (!game.makeMove(row, col)) {
            System.out.println("Invalid Move");
          } else {
            game.printBoard();
            if (game.checkWin()) {
              System.out.println(game.getCurrentPlayerName() + " won the game");
              break;
            }
            if (game.isBoardFull()) {
              System.out.println("Game Over");
              break;
            }
          }
        } catch (NumberFormatException e) {
          System.out.println("Invalid Move");
        }
      }

      scanner.close();

      // Capture the actual output
      String actualOutput = outputStream.toString().trim();

      // Read expected output from expected_output.txt
      BufferedReader expectedReader = new BufferedReader(new FileReader("expected_output.txt"));
      StringBuilder expectedOutput = new StringBuilder();
      while ((line = expectedReader.readLine()) != null) {
        expectedOutput.append(line).append("\n");
      }
      expectedReader.close();

      // Compare actual and expected output
      if (actualOutput.equals(expectedOutput.toString().trim())) {
        System.out.println("Test Passed!");
      } else {
        System.out.println("Test Failed!");
        System.out.println("Actual Output:");
        System.out.println(actualOutput);
        System.out.println("Expected Output:");
        System.out.println(expectedOutput.toString().trim());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}