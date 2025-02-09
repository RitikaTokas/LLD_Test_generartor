#!/bin/bash

# Wait for Main.java to be available
while [ ! -f /app/Main.java ]; do
    echo "Waiting for Main.java..."
    sleep 1
done

# Wait for ProblemInterface.java to be available
while [ ! -f /app/ProblemInterface.java ]; do
    echo "Waiting for ProblemInterface.java..."
    sleep 1
done

echo "Java files found. Proceeding with compilation."

# Compile Java files
javac /app/*.java 2>> /app/compilation_errors.txt
if [ $? -ne 0 ]; then
    echo "Compilation failed! Check /app/compilation_errors.txt for details."
    cat /app/compilation_errors.txt
    exit 1
fi

echo "Compilation successful. Running Java program..."

# Run Java program with the entire input.txt and capture output
java -cp /app Main < /app/input.txt >> /app/output.txt 2>> /app/runtime_errors.txt
cat /app/output.txt
echo "Execution completed."