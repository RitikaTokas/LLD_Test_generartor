## **[1]. Tic Tac Toe**
**Solved Status:** ✅ Solved  
**Difficulty:** 🟠 Medium  
**Topics:** `Game`, `Board`, `Simulation`

---

### **Problem Description**
Tic Tac Toe is a classic game played on an **n × n board** by **m players**. Players take turns placing their marks (X, O, etc.) on the board in a **round-robin manner**. The game continues until one player wins by forming a straight line (horizontal, vertical, or diagonal) or until the board is full.

The game should display the board's state **after every valid move**. If a move is **invalid** (out of bounds or already occupied), it should print `"Invalid move"` and prompt the same player to try again.

Once a player wins, the system should **display the winner's name** followed by `"Game Over"` and stop further moves.

---

### **Input Format**
1. An integer `n` → Size of the `n × n` board.
2. An integer `m` → Number of players.
3. The next `m` lines → Names of the players in order.
4. A sequence of moves: Two integers `a` and `b` representing the row and column (1-based index) where the current player places their mark.

Players take turns **in the order they were listed**.

---

### **Output Format**
- The board state after every **valid move**.
- If a move is **invalid**, print `"Invalid move"` and **replay the turn**.
- If a player **wins**, print:

---

### **Constraints**
- `3 ≤ n ≤ 10` → Board size
- `2 ≤ m ≤ 5` → Number of players
- `1 ≤ a, b ≤ n` → Valid board positions
- Players **must** place their marks in **round-robin order**.
- **Invalid Moves:**
- Position already occupied
- Move out of bounds

---

### **Example 1: Normal Gameplay**
#### **Input:**
```yaml
3
2
Alice
Bob
1 1
1 2
2 2
3 1
2 1
1 3
3 2
2 3
3 3
```

#### **Output:**
```yaml
X - -
- - -
- - -

X O -
- - -
- - -

X O -
- X -
- - -

X O -
- X -
O - -

X O -
X X -
O - -

X O O
X X -
O - -

X O O
X X -
O X -

X O O
X X O
O X -

X O O
X X O
O X X

Bob won the game
Game Over
```

#### **Explanation:**

1. Alice places X at (1,1) → Board updates.
2. Bob places O at (1,2) → Board updates.
3. Alice places X at (2,2) → Board updates.
4. Game continues...
5. Bob wins by completing a diagonal (1,3 → 2,2 → 3,1).
6. The program prints "Bob won the game" and "Game Over".

### **Example 2: Invalid Move Handling**
#### **Input:**
```yaml
3
2
Alice
Bob
1 1
1 1
1 2
2 2
3 1
3 3
```

#### **Output:**
```yaml
X - -
- - -
- - -

Invalid move

X O -
- - -
- - -

X O -
- X -
- - -

X O -
- X -
O - -

X O -
- X -
O - X

Alice won the game
Game Over
```

#### **Explanation:**

1. Alice places X at (1,1).
2. Bob tries to place O at (1,1) (already occupied) → "Invalid move".
3. Bob retries and places O at (1,2).