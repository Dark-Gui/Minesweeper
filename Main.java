import java.util.Random;
import java.util.Scanner;

/*
 * Provided in this class is the neccessary code to get started with your game's implementation
 * You will find a while loop that should take your minefield's gameOver() method as its conditional
 * Then you will prompt the user with input and manipulate the data as before in project 2
 *
 * Things to Note:
 * 1. Think back to project 1 when we asked our user to give a shape. In this project we will be asking the user to provide a mode. Then create a minefield accordingly
 * 2. You must implement a way to check if we are playing in debug mode or not.
 * 3. When working inside your while loop think about what happens each turn. We get input, user our methods, check their return values. repeat.
 * 4. Once while loop is complete figure out how to determine if the user won or lost. Print appropriate statement.
 */

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to My Minefield Game");
        System.out.println("Please choose a game difficulty: (Easy/Medium/Hard)");
        String difficulty = scanner.nextLine().toLowerCase();
        int rows = 0, columns = 0, flags = 0;
        switch (difficulty) {
            case "easy":
                rows = 5;
                columns = 5;
                flags = 5;
                break;
            case "medium":
                rows = 9;
                columns = 9;
                flags = 12;
                break;
            case "hard":
                rows = 20;
                columns = 20;
                flags = 40;
                break;
            default:
                System.out.println("Invalid difficulty, setting to Easy by default.");
                rows = 5;
                columns = 5;
                flags = 5;
                break;
        }
        System.out.println("Enable debug mode? (true/false):");
        boolean debug = scanner.nextBoolean();
        Minefield minefield = new Minefield(rows, columns, flags);
        System.out.println("Enter x coordinate:");
        int x = scanner.nextInt();
        System.out.println("Enter y coordinate:");
        int y = scanner.nextInt();
        minefield.createMines(x, y, flags);
        int counter = flags;
        minefield.evaluateField();
        minefield.revealStartingArea(x, y);
        while (!minefield.gameOver()) {
            if (debug) {
                minefield.debug();
                System.out.println();
            }
            System.out.println(minefield);
            System.out.println("Remaining flags: " + counter);
            System.out.println("Enter x coordinate:");
            x = scanner.nextInt();
            System.out.println("Enter y coordinate:");
            y = scanner.nextInt();
            System.out.println("Place flag? (true/false):");
            boolean flag = scanner.nextBoolean();
            if (flag) {
                counter--;
            }
            boolean mineHit = minefield.guess(x, y, flag);
            if (mineHit) {
                System.out.println("Game over - Mine hit!");
                return;
            }
        }
        System.out.println("Congratulations! You won!");
        scanner.close();
    }
}


