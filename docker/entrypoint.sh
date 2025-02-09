#!/bin/bash
cd /app

# Compile Java files
javac Main.java
if [ $? -ne 0 ]; then
  echo "Compilation Error" > error.txt
  exit 1
fi

# Run Java Program
java Main < input.txt > output.txt 2> error.txt
