import java.util.Scanner;
import java.util.Stack;

public class ConnectFour {
    static char[][] board;
    static int rows;
    static int columns;
    static Scanner k = new Scanner(System.in);
    static int turnCounter = 0;
    static int firstPlayerScore ;
    static int secondPlayerScore ;
    static int gameMode;
    static int levelOfComputer;
    static int playPosition;
    static char decision;
    static Stack<Integer> undoStack = new Stack<>();
    static Stack<Integer> redoStack = new Stack<>();

    public static void main(String[] args) {
        welcomeScreen();
        playGame();
    }

    static void welcomeScreen() {
        System.out.println("1-PvP \n2-Player vs computer");
        gameMode = k.nextInt();
        if (gameMode == 2) {
            System.out.println("1-Easy\n2-Intermediate");
            levelOfComputer = k.nextInt();
            if (levelOfComputer != 1 && levelOfComputer != 2) {
                System.out.println("Invalid input");
                welcomeScreen();
            }
        } else if (gameMode != 1) {
            welcomeScreen();
            System.out.println("Invalid input");
        }
    }

    static void playGame() {
        if (gameMode == 1) {
            initializeBoard();
            while (isNotFull()) {
                takeNextMoveWithUndoAndRedo();
            }
        } else if (levelOfComputer == 1) {
            initializeBoard();
            while (isNotFull()) {
                turnCounter = 0;
                takeNextMove();
                if (isNotFull()) {
                    computerStrategyEasy();
                }
            }
        } else {
            initializeBoard();
            while (isNotFull()) {
                turnCounter = 0;
                takeNextMove();
                if (isNotFull()) {
                    computerStrategyIntermediate();
                }
            }
        }
        getScore('X');
        getScore('O');
        if (firstPlayerScore > secondPlayerScore) {
            System.out.println("First player wins!(X)" + "\nwith score : " + firstPlayerScore + "\nthe other player score was : " + secondPlayerScore);
        } else if (secondPlayerScore > firstPlayerScore) {
            System.out.println("Second player wins(O)!" + "\nwith score : " + secondPlayerScore + "\nthe other player score was : " + firstPlayerScore);

        } else {
            System.out.println("Tie!");
            System.out.println("Both players scored " + firstPlayerScore + " connected fours");
        }
    }

    static void computerStrategyEasy() {
        playPosition = (int) (Math.random() * (columns + 1)); //columns+1 since math.random can't give (1)
        if (playPosition == 0) {
            playPosition++;
        }
        if (playPosition > columns || board[0][playPosition - 1] != '-') {
            computerStrategyEasy();
            return;
        }
        for (int i = rows; i > 0; i--) {
            if (board[i - 1][playPosition - 1] != '-') {
                continue;
            }
            board[i - 1][playPosition - 1] = 'O';
            break;
        }
        printBoard();
    }

    static void computerStrategyIntermediate() {
        // full column ------> implement random strategy
        if (board[0][playPosition - 1] != '-') {
            computerStrategyEasy();
        } else {
            // defensive strategy (same column)
            for (int i = rows; i > 0; i--) {
                if (board[i - 1][playPosition - 1] != '-') {
                    continue;
                }
                board[i - 1][playPosition - 1] = 'O';
                printBoard();
                break;
            }
        }

    }

    static void initializeBoard() {
        System.out.println("Enter number of rows : ");
        rows = k.nextInt();
        System.out.println("Enter number of columns : ");
        columns = k.nextInt();
        System.out.print(" ");
        for (int m = 1; m <= columns; m++) {
            System.out.print(m + " ");
        }
        System.out.println();
        board = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = '-';
                System.out.print(" ");
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
        System.out.println("____________________________________");
    }

    static void insertMove(int position) {
        // validate input (bounds of array , full column)
        if (playPosition > columns || board[0][position - 1] != '-') {
            System.out.println("Enter a valid input");
            takeNextMoveWithUndoAndRedo();
            return;
        }
        playPosition = position;
        for (int i = rows; i > 0; i--) {
            if (board[i - 1][position - 1] != '-') {
                continue;
            }
            board[i - 1][position - 1] = getPlayerSymbol();
            undoStack.push(position);
            break;
        }
        turnCounter++;
        printBoard();
    }

    static void takeNextMove() {
        System.out.println("Enter the column number");
        playPosition = k.nextInt();
        // validate input (bounds of array , full column)
        if (playPosition > columns || board[0][playPosition - 1] != '-') {
            System.out.println("Enter a valid input");
            takeNextMove();
            return;
        }
        for (int i = rows; i > 0; i--) {
            if (board[i - 1][playPosition - 1] != '-') {
                continue;
            }
            board[i - 1][playPosition - 1] = getPlayerSymbol();
            undoStack.push(playPosition);
            redoStack.clear();
            break;
        }
        turnCounter++;
        printBoard();
    }

    static void takeNextMoveWithUndoAndRedo() {
        System.out.println("If you wish to undo/redo the last move\nenter 'u' to undo / 'r' to redo \notherwise enter column number");
        decision = k.next().charAt(0);
        if (decision > '0' && decision <= '9') {
            int number = decision - '0';
            insertMove(number);
            redoStack.clear();
            return;
        }
        if (decision == 'u' || decision == 'U') {
            undo();
        } else if (decision == 'r' || decision == 'R') {
            redo();
        }
    }

    static void undo() {
        if (undoStack.empty()) {
            System.out.println("You cannot undo!");
            return;
        }
        redoStack.push(undoStack.peek());
        int current = undoStack.pop();
        for (int i = 0; i < rows; i++) {
            if (board[i][current - 1] != '-') {
                board[i][current - 1] = '-';
                turnCounter++;
                break;
            }
        }
        printBoard();
        takeNextMoveWithUndoAndRedo();
    }

    static void redo() {
        if (redoStack.empty()) {
            System.out.println("You cannot redo!");
            return;
        }
        int current = redoStack.pop();
        undoStack.clear();
        for (int i = rows; i > 0; i--) {
            if (board[i - 1][current - 1] != '-') {
                continue;
            }
            board[i - 1][current - 1] = getPlayerSymbol();
            turnCounter++;
            break;
        }
        printBoard();
        takeNextMoveWithUndoAndRedo();
    }

    static void printBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("____________________________________");
    }

    static boolean isNotFull() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (board[i][j] == '-') {
                    return true;
                }
            }
        }
        return false;
    }

    static char getPlayerSymbol() {
        return isFirstPlayerTurn() ? 'X' : 'O';
    }

    static boolean isFirstPlayerTurn() {
        return turnCounter % 2 == 0;
    }

    static void getScore(char symbol) {
        firstPlayerScore = 0;
        secondPlayerScore = 0;
        int counter;
        // horizontal check
        for (int i = 0; i < rows; i++) { //6 ----5
            counter = 0;
            for (int j = 0; j < columns; j++) {//7 ----6
                if (board[i][j] == symbol) {
                    counter++;
                } else {
                    counter = 0;
                }
                if (counter == 4) {
                    counter = 0;
                    if (symbol == 'X') {
                        firstPlayerScore++;
                    } else {
                        secondPlayerScore++;
                    }
                }
            }
        }
        //vertical check
        for (int i = 0; i < columns; i++) { //7---6
            counter = 0;
            for (int j = 0; j < rows; j++) { //6---5
                if (board[j][i] == 'X') {
                    counter++;
                } else {
                    counter = 0;
                }
                if (counter == 4) {
                    counter = 0;
                    if (symbol == 'X') {
                        firstPlayerScore++;
                    } else {
                        secondPlayerScore++;
                    }

                }
            }
        }
        //diagonals starting from the left column (ascending) (blue lines)
        for (int i = rows - 2; i >= 0; i--) {
            counter = 0;
            for (int increment = 0; i - increment >= 0; increment++) {
                if (board[i - increment][increment] == symbol) {
                    counter++;
                } else {
                    counter = 0;
                }
                if (counter >= 4) {
                    counter = 0;
                    if (symbol == 'X') {
                        firstPlayerScore++;
                    } else {
                        secondPlayerScore++;
                    }
                }
            }
        }
        //diagonals starting from bottom row (ascending) (black lines)
        int bottomRow = rows - 1;
        for (int i = 0; i < columns - 1; i++) {
            counter = 0;
            for (int increment = 0; bottomRow - increment >= 0 && i + increment <= columns - 1; increment++) {
                if (board[bottomRow - increment][i + increment] == symbol) {
                    counter++;
                } else {
                    counter = 0;
                }
                if (counter == 4) {
                    counter = 0;
                    if (symbol == 'X') {
                        firstPlayerScore++;
                    } else {
                        secondPlayerScore++;
                    }
                }
            }
        }
        //diagonals starting from left column (descending) (green lines)
        for (int i = 0; i < rows - 1; i++) {
            counter = 0;
            for (int increment = 0; i + increment <= rows - 1 && increment <= columns - 1; increment++) {
                if (board[i + increment][increment] == symbol) {
                    counter++;
                } else {
                    counter = 0;
                }
                if (counter == 4) {
                    counter = 0;
                    if (symbol == 'X') {
                        firstPlayerScore++;
                    } else {
                        secondPlayerScore++;
                    }
                }
            }
        }
        //diagonals starting from the top row (descending) (red lines)
        for (int i = 1;i<board[0].length; i++) {
            counter = 0;
            for (int increment = 0; increment <= rows - 1 && i + increment <= columns - 1; increment++) {
                if (board[increment][i + increment] == symbol) {
                    counter++;
                } else {
                    counter = 0;
                }
                if (counter == 4) {
                    counter = 0;
                    if (symbol == 'X') {
                        firstPlayerScore++;
                    } else {
                        secondPlayerScore++;
                    }
                }
            }

        }
    }

}

