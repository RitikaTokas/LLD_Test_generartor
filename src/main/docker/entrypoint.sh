#!/bin/bash

# Compile Java files
javac *.java

# Run the Main class with input redirection
java Main < input.txt > output.txt 2> error.txt