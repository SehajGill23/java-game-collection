package ca.bcit.comp2522.termproject.numbergame;

/**
 * Represents the core game board logic for a grid-based game, providing common functionality
 * while leaving specific win/loss conditions and placement rules to its subclasses.
 * This abstract class initializes a 4x5 integer grid, intended to hold the game's elements.
 * It also provides a basic structure for managing a sequence of numbers to be used within the game.
 * Subclasses of {@code GameBoard} will implement the specific rules for generating these numbers,
 * determining valid placements within the grid, and handling the actual placement of numbers.
 *
 * <p>The grid is initialized with a default value (typically -1) to represent empty cells.
 * The class also includes fields to store the sequence of numbers and track the current
 * index within that sequence. This base class serves as a foundation for different types
 * of grid-based number games where the underlying grid structure and number sequence
 * management are common, but the gameplay rules vary.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
abstract class GameBoard
{
    private static final int NUM_OF_ROWS    = 4;
    private static final int NUM_OF_COLUMNS = 5;
    private static final int MAX_RAND_NUMS  = 20;
    private static final int EMPTY_CELL     = -1;
    private static final int INITIAL_INDEX  = 0;

    private int[][] grid;
    private int[]   numbers;
    private int     currentIndex;


    /*
     * Constructs a {@code GameBoard} with a fixed size of 4 rows and 5 columns.
     * Initializes the game grid as a 2D integer array where each cell is initially
     * set to {@link #EMPTY_CELL} (-1), indicating that the grid is empty.
     * It also initializes an array to store the numbers that will be used in the game,
     * with a maximum capacity defined by {@link #MAX_RAND_NUMS} (20). The {@code currentIndex}
     * is set to {@link #INITIAL_INDEX} (0), pointing to the beginning of the number sequence.
     */
    GameBoard()
    {
        grid = new int[NUM_OF_ROWS][NUM_OF_COLUMNS];
        for(int i = INITIAL_INDEX; i < NUM_OF_ROWS; i++)
        {
            for(int j = INITIAL_INDEX; j < NUM_OF_COLUMNS; j++)
            {
                grid[i][j] = EMPTY_CELL;
            }
        }
        numbers      = new int[MAX_RAND_NUMS];
        currentIndex = INITIAL_INDEX;
    }

    /**
     * Returns the current state of the game grid. This method provides access to
     * the 2D integer array representing the game board. Each element in the array
     * corresponds to a cell in the grid, containing either a number placed by the
     * game logic or the value representing an empty cell ({@link #EMPTY_CELL}).
     *
     * @return a 4x5 integer array representing the game board. The first dimension
     * represents the rows (0 to 3), and the second dimension represents the columns
     * (0 to 4).
     */
    public int[][] getGrid()
    {
        return grid;
    }

    /**
     * Populates the {@code numbers} array with a sequence of random integers.
     * The specific implementation of how these numbers are generated (e.g., the
     * range and distribution) is left to the concrete subclasses of {@code GameBoard}.
     * This method is responsible for providing the numbers that the player will
     * interact with during the game.
     */
    public abstract void generateNumbers();

    /**
     * Checks if the specified position (row and column) in the grid is a valid
     * location to place a number. The criteria for a valid placement are specific
     * to the type of game being implemented and will be defined in the subclasses
     * of {@code GameBoard}. This method should consider factors such as whether
     * the cell is currently empty and any game-specific rules regarding the placement
     * of numbers (e.g., adjacency rules, ordering requirements).
     *
     * @param row the row index (0-based) of the position to check.
     * @param col the column index (0-based) of the position to check.
     * @return {@code true} if the specified position is a valid place to put a number,
     * {@code false} otherwise.
     */
    public abstract boolean isValidPlacement( final int row,
                                              final int col);

    /**
     * Places a given {@code number} into the game grid at the specified {@code row}
     * and {@code col}, provided that the placement is valid according to the rules
     * defined by the subclass. The actual logic for checking validity should be
     * encapsulated in the {@link #isValidPlacement(int, int)} method. This method
     * is responsible for updating the state of the game board by inserting the
     * number into the appropriate cell.
     *
     * @param row    the row index (0-based) where the number should be placed.
     * @param col    the column index (0-based) where the number should be placed.
     * @param number the integer number to be placed in the grid.
     */
    public abstract void placeNumberInGrid(final int row,
                                           final int col,
                                           final int number);
}