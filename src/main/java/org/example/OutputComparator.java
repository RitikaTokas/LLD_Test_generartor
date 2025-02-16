package org.example;

import java.util.Iterator;

public class OutputComparator {
  public static boolean compareOutputs(int testCaseNum, String actualOutput, Iterator<String> expectedIterator) {
    StringBuilder expectedOutput = new StringBuilder();
    String line;

    while (expectedIterator.hasNext() && !(line = expectedIterator.next()).isEmpty()) {
      expectedOutput.append(line).append("\n");
    }

    if (actualOutput.equals(expectedOutput.toString().trim())) {
     return true;
    } else {
     return false;
    }
  }
}