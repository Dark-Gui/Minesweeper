import java.util.Random;


public class Minefield {
    private Cell[][] field; // 2D array representing the minefield
    private int rows; // Number of rows in the minefield
    private int columns; // Number of columns in the minefield
    private int flags; // Number of flags in the minefield
    private boolean mines = false; // Amount of mines on the board

    /**
     * Global Section
     */
    public static final String ANSI_YELLOW_BRIGHT = "\u001B[33;1m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_PURPLE = "\u001b[35m";
    public static final String ANSI_CYAN = "\u001b[36m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001b[47m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001b[45m";
    public static final String ANSI_GREY_BACKGROUND = "\u001b[0m";

    /*
     * Class Variable Section
     *
     */

    /*Things to Note:
     * Please review ALL files given before attempting to write these functions.
     * Understand the Cell.java class to know what object our array contains and what methods you can utilize
     * Understand the StackGen.java class to know what type of stack you will be working with and methods you can utilize
     * Understand the QGen.java class to know what type of queue you will be working with and methods you can utilize
     */

    /**
     * Minefield
     * <p>
     * Build a 2-d Cell array representing your minefield.
     * Constructor
     *
     * @param rows    Number of rows.
     * @param columns Number of columns.
     * @param flags   Number of flags, should be equal to mines
     */
    public Minefield(int rows, int columns, int flags) {
        //Constructor
        // Initialize the minefield grid with cells
        this.rows = rows;
        this.columns = columns;
        this.flags = flags;
        this.mines = false;
        this.field = new Cell[rows][columns];
        // Fill the minefield with default cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                field[i][j] = new Cell(false, "-");
            }
        }
    }

    /**
     * evaluateField
     *
     * @function: Evaluate entire array.
     * When a mine is found check the surrounding adjacent tiles. If another mine is found during this check, increment adjacent cells status by 1.
     */
    public void evaluateField() {
        // Logic to evaluate the minefield for adjacent mines
        int countMines = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                countMines = 0;
                // Check if the current cell contains a mine
                if (field[i][j].getStatus().equals("M")) {
                    // Define directions to evaluate adjacent cells
                    int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
                    int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};
                    countMines++;
                    // Evaluate adjacent cells
                    for (int k = 0; k < 8; k++){
                        int newX = i + dx[k];
                        int newY = j + dy[k];
                        // Ensure adjacent cell is within bounds
                        if (newX >= 0 && newX < rows && newY >= 0 && newY < columns) {
                            if (!(field[newX][newY].getStatus().equals("M"))) {
                                int newStatus;
                                if(field[newX][newY].getStatus().equals("-")){
                                    newStatus = 1;
                                    field[newX][newY].setStatus(Integer.toString(newStatus));
                                } else {
                                    newStatus = Integer.parseInt(field[newX][newY].getStatus()) + 1;
                                    field[newX][newY].setStatus(Integer.toString(newStatus));
                                }
                            }
                        }
                    }
                }else{
                    // Set status for cells without mines
                    field[i][j].setStatus(Integer.toString(countMines));
                }
            }
        }
    }


    /**
     * createMines
     * <p>
     * Randomly generate coordinates for possible mine locations.
     * If the coordinate has not already been generated and is not equal to the starting cell set the cell to be a mine.
     * utilize rand.nextInt()
     *
     * @param x     Start x, avoid placing on this square.
     * @param y     Start y, avoid placing on this square.
     * @param mines Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        // Randomly generate mines on the minefield
        Random random = new Random();
        while (mines > 0) {
            int a = random.nextInt(rows);
            int j = random.nextInt(columns);
            if (a != x && j != y) {
                if (field[a][j].getStatus() != "M") {
                    field[a][j].setStatus("M");
                    mines--;
                }
            }
        }
    }

    /**
     * guess
     * <p>
     * Check if the guessed cell is inbounds (if not done in the Main class).
     * Either place a flag on the designated cell if the flag boolean is true or clear it.
     * If the cell has a 0 call the revealZeroes() method or if the cell has a mine end the game.
     * At the end reveal the cell to the user.
     *
     * @param x    The x value the user entered.
     * @param y    The y value the user entered.
     * @param flag A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean flag) {
        // Logic for player's guess and mine checking
        if (x < 0 || x >= rows || y < 0 || y >= columns) {
            return false;
        }
        // Logic for placing a flag
        if (flag && flags > 0) {
            if (!field[x][y].getRevealed()) {
                field[x][y].setStatus("F");
                field[x][y].setRevealed(true);
                flags--;
                return false;
            }
        } else {
            //
            if (field[x][y].getStatus().equals("M")) {
                //Checks for mines
                return true;
            } else if (field[x][y].getStatus().equals("0") && !field[x][y].getRevealed()) {
                // Reveal all the zeroes
                revealZeroes(x, y);
            } else if (!field[x][y].getRevealed()) {
                // Reveal the cell to the user
                field[x][y].setRevealed(true);
            }
        }
        return false;
    }


    /**
     * gameOver
     * <p>
     * Ways a game of Minesweeper ends:
     * 1. player guesses a cell with a mine: game over -> player loses
     * 2. player has revealed the last cell without revealing any mines -> player wins
     *
     * @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse return true.
     */
    public boolean gameOver() {
        // Check if the game is over
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (!field[i][j].getRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Reveal the cells that contain zeroes that surround the inputted cell.
     * Continue revealing 0-cells in every direction until no more 0-cells are found in any direction.
     * Utilize a STACK to accomplish this.
     * <p>
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x The x value the user entered.
     * @param y The y value the user entered.
     */
    public void revealZeroes(int x, int y) {
        // Use a stack to reveal adjacent cells with 0 mines
        Stack1Gen<int[]> stack = new Stack1Gen<>();
        stack.push(new int[]{x, y});
        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int currentX = current[0];
            int currentY = current[1];
            // Checks if the coordinates are inbounds
            if (currentX < 0 || currentX >= rows || currentY < 0 || currentY >= columns) {
                continue;
            }
            // Reveals the cell
            field[currentX][currentY].setRevealed(true);
            //Checks all the adjacent cells for zeros
            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
            for (int[] dir : directions) {
                int newX = currentX + dir[0];
                int newY = currentY + dir[1];
                // Checks if adjacent cell is within bounds and unrevealed with 0
                if (newX >= 0 && newX < rows && newY >= 0 && newY < columns
                        && !field[newX][newY].getRevealed()
                        && field[newX][newY].getStatus().equals("0")) {
                    stack.push(new int[]{newX, newY});
                }
            }
        }
    }


    /**
     * revealStartingArea
     * <p>
     * On the starting move only reveal the neighboring cells of the inital cell and continue revealing the surrounding concealed cells until a mine is found.
     * Utilize a QUEUE to accomplish this.
     * <p>
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a queue be useful for this function?
     *
     * @param x The x value the user entered.
     * @param y The y value the user entered.
     */
    public void revealStartingArea(int x, int y) {
        // Use a queue to reveal neighboring cells at the start
        Q1Gen<int[]> queue = new Q1Gen<>();
        queue.add(new int[]{x, y});
        while (queue.length() != 0) {
            int[] current = queue.remove();
            int currentX = current[0];
            int currentY = current[1];
            // Checks if its in bound
            if (currentX < 0 || currentX >= rows || currentY < 0 || currentY >= columns) {
                continue;
            }
            // Set current cell to revealed
            field[currentX][currentY].setRevealed(true);
            // Break when a mine is found in the revealed cells
            if (field[currentX][currentY].getStatus().equals("M")) {
                break;
            }
            // Checks adjacent cells
            int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
            for (int[] dir : directions) {
                int newX = currentX + dir[0];
                int newY = currentY + dir[1];
                // Ensure adjacent cell is in bounds and unrevealed
                if (newX >= 0 && newX < rows && newY >= 0 && newY < columns
                        && !field[newX][newY].getRevealed()) {
                    queue.add(new int[]{newX, newY});
                }
            }
        }
    }


    /**
     * For both printing methods utilize the ANSI colour codes provided!
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * debug
     *
     * @function This method should print the entire minefield, regardless if the user has guessed a square.
     * *This method should print out when debug mode has been selected.
     */
    public void debug(){
        // Print the minefield for debugging purposes
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                String status = field[i][j].getStatus();
                String colorCode = ANSI_GREY_BACKGROUND;
                System.out.print(field[i][j].getStatus() + " ");
                switch (status) {
                    case "0":
                        colorCode = ANSI_RED;
                        break;
                    case "1":
                        colorCode = ANSI_GREEN;
                        break;
                    case "2":
                        colorCode = ANSI_BLUE;
                        break;
                    case "3":
                        colorCode = ANSI_YELLOW;
                        break;
                    case "4":
                        colorCode = ANSI_PURPLE;
                        break;
                    case "5":
                        colorCode = ANSI_CYAN;
                        break;
                    default:
                        colorCode = ANSI_GREY_BACKGROUND;
                        break;
                }
            }
            System.out.println();
        }
    }

    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    public String toString() {
        // Generate a string with revealed squares for display
        StringBuilder output = new StringBuilder();
        output.append("  ");
        for (int c = 0; c < columns; c++) {
            output.append(c).append(" ");
        }
        output.append("\n");
        for (int i = 0; i < rows; i++) {
            output.append(i).append(" ");
            for (int j = 0; j < columns; j++) {
                if (field[i][j].getRevealed()) {
                    String status = field[i][j].getStatus();
                    String colorCode = ANSI_GREY_BACKGROUND;
                    switch (status) {
                        case "0":
                            colorCode = ANSI_RED;
                            break;
                        case "1":
                            colorCode = ANSI_GREEN;
                            break;
                        case "2":
                            colorCode = ANSI_BLUE;
                            break;
                        case "3":
                            colorCode = ANSI_YELLOW;
                            break;
                        case "4":
                            colorCode = ANSI_PURPLE;
                            break;
                        case "F":
                            colorCode = ANSI_CYAN;
                            break;
                        default:
                            colorCode = ANSI_GREY_BACKGROUND;
                            break;
                    }
                    output.append(colorCode).append(status).append(" ").append(ANSI_GREY_BACKGROUND);
                } else {
                    output.append("- ");
                }
            }
            output.append("\n");
        }
        return output.toString();
    }



}

