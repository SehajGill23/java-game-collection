package ca.bcit.Comp2522.termProject.NumberGame;

/**
 * Represents the core game board logic for a grid-based game, providing common functionality
 * while leaving specific win/loss conditions to subclasses.
 */
public abstract class GameBoard
{
    private static final int NUM_OF_ROWS    = 4;
    private static final int NUM_OF_COLUMNS = 5;
    private static final int NUM_OF_RANDOM_INT = 20;
    private static final int MAX_RANDOM_NUM = 1000;

    protected int[][] grid;

    protected int[] numbers;

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
     * Returns the current state of the grid array.
     *
     * @return a 4x5 int array representing the game board
     */
    public int[][] getGrid()
    {
        return grid;
    }

    /**
     * Populates the numbers array with 20 random integers between 1 and 1000, inclusive.
     */
    public abstract void generateNumbers();

    /**
     * Checks if the specified position is empty and valid for placement.
     * Subclasses define specific validity rules (e.g., ascending order).
     *
     */
    public abstract boolean isValidPlacement(int row,
                                             int col);

    /**
     * Places the number in the grid at the specified position if valid.
     */
     public abstract void placeNumberInGrid(int row,
                                  int col,
                                  int number);

    /**
     * Determines the current game status (win, loss, or ongoing).
     * Subclasses implement specific win/loss logic.
     *
     * @return a string indicating "win", "loss", or "ongoing"
     */
    public abstract String checkGameStatus();
}