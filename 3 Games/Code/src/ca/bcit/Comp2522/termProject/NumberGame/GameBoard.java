package ca.bcit.Comp2522.termProject.NumberGame;

/**
 * Represents the core game board logic for a grid-based game, providing common functionality
 * while leaving specific win/loss conditions to subclasses.
 */
public abstract class GameBoard
{
    private static final int NUM_OF_ROWS       = 4;
    private static final int NUM_OF_COLUMNS    = 5;
    private static final int NUM_OF_RANDOM_INT = 20;
    private static final int MAX_RANDOM_NUM    = 1000;


    /**
     * A 4x5 array storing the placed numbers, initialized with -1 to indicate empty slots.
     */
    protected int[][] grid;

    /**
     * An array of 20 randomly generated integers (1-1000) to be placed in the grid.
     */
    protected int[] numbers;

    /**
     * Tracks the current number being placed (0 to 19).
     */
    protected int currentIndex;

    /**
     * Constructs an GameBoard with an empty 4x5 grid and initializes fields.
     */
    public GameBoard()
    {
        grid = new int[NUM_OF_ROWS][NUM_OF_COLUMNS]; // 4 rows, 5 columns
        for(int i = 0; i < NUM_OF_ROWS; i++)
        {
            for(int j = 0; j < NUM_OF_COLUMNS; j++)
            {
                grid[i][j] = -1; // -1 indicates empty
            }
        }
        numbers      = new int[NUM_OF_RANDOM_INT];
        currentIndex = 0;
    }

    /**
     * Populates the numbers array with 20 random integers between 1 and 1000, inclusive.
     */
    public void generateNumbers()
    {
        for(int i = 0; i < NUM_OF_RANDOM_INT; i++)
        {
            numbers[i] = (int) (Math.random() * MAX_RANDOM_NUM) + 1;
        }
    }

    /**
     * Checks if the specified position is empty and valid for placement.
     * Subclasses define specific validity rules (e.g., ascending order).
     *
     * @param row the row index (0-3) in the 4x5 grid
     * @param col the column index (0-4) in the 4x5 grid
     * @return true if the placement is valid, false otherwise
     */
    public abstract boolean isValidPlacement(int row,
                                             int col);

    /**
     * Places the number in the grid at the specified position if valid.
     *
     * @param row    the row index (0-3)
     * @param col    the column index (0-4)
     * @param number the number to place
     */
    public void placeNumberInGrid(int row,
                                  int col,
                                  int number)
    {
        if(isValidPlacement(row,
                            col))
        {
            grid[row][col] = number;
        }
    }

    /**
     * Returns the current state of the grid array.
     *
     * @return a 4x5 int array representing the game board
     */
    public int[][] getGrid()
    {
        return grid;
    }

    /**
     * Determines the current game status (win, loss, or ongoing).
     * Subclasses implement specific win/loss logic.
     *
     * @return a string indicating "win", "loss", or "ongoing"
     */
    public abstract String checkGameStatus();
}