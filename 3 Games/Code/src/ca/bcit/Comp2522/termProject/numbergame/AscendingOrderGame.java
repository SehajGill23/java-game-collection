package ca.bcit.Comp2522.termProject.numbergame;

import java.util.Random;

/*
 * Implements the Ascending Order Number Placement Game on a 4x5 grid.
 * Players must strategically place randomly generated numbers into the grid
 * such that the numbers are in ascending order from left to right, top to bottom.
 * This class manages the game board, the sequence of numbers to be placed,
 * the current game state, and provides methods for game actions like starting
 * the game and placing numbers. It also tracks the player's performance,
 * including the number of games played and won, and the total successful
 * placements. This class adheres to the {@link GameController} interface
 * to ensure a consistent game flow management.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class AscendingOrderGame extends GameBoard implements GameController
{

    private   int[][] grid;
    private int[] numbers;
    private int   currentIndex;
    private int   gamesPlayed;
    private   int     gamesWon;
    private   int     totalPlacements;

    private static final int NUMBER_OF_ROWS          = 4;
    private static final int NUMBER_OF_COLUMNS       = 5;
    private static final int GAME_NOT_STARTED        = -1;
    private static final int EMPTY_SLOT              = -1;
    private static final int GAME_WIN                = 1;
    private static final int GAME_LOSS_NO_VALID_MOVE = 2;
    private static final int GAME_LOSS_OUT_OF_ORDER  = 3;
    private static final int GRID_EMPTY              = -1;
    private static final int TOTAL_NUMBERS           = 20;
    private static final int MAX_RANDOM_NUM          = 1000;
    private static final int INITIAL_CURR_INDEX      = 0;
    private static final int MIN_RANDOM_NUM          = 1;
    private static final int SLOT_NUMBER_OFFSET      = 1;
    private static final int PLACEMENT_SUCCESS       = 0;
    private static final int COLS_OFFSET             = 1;
    private static final int ROWS_OFFSET             = 1;
    private static final int BASE_DEFAULT_NUMBER     = 0;


    private static final String STATUS_WIN             = "win";
    private static final String STATUS_LOSS            = "loss";
    private static final String STATUS_ONGOING         = "ongoing";
    private static final String NO_GAMES_MESSAGE       = "No games played yet.";
    private static final String POSSIBLE_SLOTS_LABEL   = "Possible slots: ";
    private static final String NO_SLOTS_MESSAGE       = "None";
    private static final String GENERATED_NUMBER_LABEL = "Generated number: ";
    private static final String SCORE_WON_PREFIX       = "You won ";
    private static final String SCORE_OUT_OF           = " out of ";
    private static final String SCORE_GAMES_SUFFIX     = " games";
    private static final String SCORE_AND              = " and ";
    private static final String SCORE_LOST_PREFIX      = "lost ";
    private static final String SCORE_WITH             = ", with ";
    private static final String SCORE_PLACEMENTS_AVG   = " successful placements, an average of ";
    private static final String SCORE_PER_GAME         = " per game.";


    /*
     * Constructs a new instance of the {@code AscendingOrderGame}.
     * Initializes the game grid as a 4x5 2D integer array, the sequence of
     * numbers to be placed as an array of size 20, the index of the current
     * number to be placed to 0, and the statistics for games played, games won,
     * and total successful placements to 0. The grid is initially filled with -1
     * to represent empty slots.
     */
    AscendingOrderGame()
    {
        grid            = new int[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        numbers         = new int[TOTAL_NUMBERS];
        currentIndex    = INITIAL_CURR_INDEX;
        gamesPlayed     = BASE_DEFAULT_NUMBER;
        gamesWon        = BASE_DEFAULT_NUMBER;
        totalPlacements = BASE_DEFAULT_NUMBER;
    }

    /**
     * Starts a new game. This method initializes the game grid by setting all
     * cells to -1 (representing empty). It then generates a new sequence of 20
     * random numbers using {@link #generateNumbers()}. The index of the next
     * number to be placed is reset to 0, and the count of games played is incremented.
     * Finally, it prints the first generated number to the console and displays
     * the possible valid slots for this number using {@link #printPossibleSlots()}.
     */
    @Override
    public void startGame()
    {
        for(int i = BASE_DEFAULT_NUMBER; i < NUMBER_OF_ROWS; i++)
        {
            for(int j = BASE_DEFAULT_NUMBER; j < NUMBER_OF_COLUMNS; j++)
            {
                grid[i][j] = GRID_EMPTY;
            }
        }

        generateNumbers();
        currentIndex = INITIAL_CURR_INDEX;
        gamesPlayed++;

        System.out.println(GENERATED_NUMBER_LABEL + numbers[currentIndex]);
        printPossibleSlots();
    }

    /**
     * Generates a sequence of 20 random integers between 1 (inclusive) and 1000
     * (inclusive). These numbers will be presented to the player one by one to
     * be placed into the game grid. The generated numbers are stored in the
     * {@code numbers} array.
     */
    public void generateNumbers()
    {
        Random random = new Random();
        for(int i = BASE_DEFAULT_NUMBER; i < TOTAL_NUMBERS; i++)
        {
            numbers[i] = random.nextInt(MAX_RANDOM_NUM) + MIN_RANDOM_NUM;
        }
    }

    /**
     * Places a given number into the game grid at the specified row and column.
     * This method does not perform any validation on the placement; it directly
     * inserts the number into the grid at the provided indices. It is assumed
     * that the caller has already verified the validity of the placement using
     * {@link #isValidPlacement(int, int)}.
     *
     * @param row    the row index (0 to 3) where the number should be placed.
     * @param col    the column index (0 to 4) where the number should be placed.
     * @param number the integer number to be placed in the grid.
     */
    @Override
    public void placeNumberInGrid(final int row,
                                  final int col,
                                  final int number)
    {
        if(isValidPlacement(row,
                            col))
        {
            grid[row][col] = number;
        }
    }

    /**
     * Attempts to place the current number (obtained using {@link #getCurrentNumber()})
     * into the game grid at the specified row and column. This method first checks
     * if the target cell is empty. If it is, it then validates the placement using
     * {@link #isValidPlacement(int, int)}. If the placement is valid, the number
     * is placed in the grid, the {@code currentIndex} is incremented to move to the
     * next number, and the {@code totalPlacements} count is increased.
     *
     * <p>After a successful placement, the method checks if the grid is still in
     * ascending order using {@link #checkIfSorted()}. If not, the game is lost due
     * to out-of-order placement. If all numbers have been placed ({@code currentIndex}
     * reaches {@link #TOTAL_NUMBERS}), the game is won. If the game is still ongoing,
     * the next number to be placed is printed, and the possible valid slots for it
     * are displayed. If there are no valid slots for the next number, the game is lost
     * due to no valid moves.
     *
     * @param row the row index (0 to 3) where the number is to be placed.
     * @param col the column index (0 to 4) where the number is to be placed.
     * @return {@link #PLACEMENT_SUCCESS} (0) if placement was successful and the
     * game continues, {@link #GAME_WIN} (1) if the game is won,
     * {@link #GAME_LOSS_NO_VALID_MOVE} (2) if loss due to no valid moves,
     * {@link #GAME_LOSS_OUT_OF_ORDER} (3) if loss due to out-of-order
     * placement, or {@link #GAME_NOT_STARTED} (-1) if the placement was
     * invalid or the selected slot was already occupied.
     */
    @Override
    public int placeNumber(final int row,
                           final int col)
    {
        if(grid[row][col] != GRID_EMPTY)
        {
            return GAME_NOT_STARTED;
        }
        if(isValidPlacement(row,
                            col))
        {
            placeNumberInGrid(row,
                              col,
                              numbers[currentIndex]);
            currentIndex++;
            totalPlacements++;


            if(!checkIfSorted())
            {
                return GAME_LOSS_OUT_OF_ORDER;
            }

            if(currentIndex >= TOTAL_NUMBERS)
            {
                return GAME_WIN; // Win
            }

            System.out.println(GENERATED_NUMBER_LABEL + numbers[currentIndex]);

            boolean hasValidSlots = hasValidPlacement();
            printPossibleSlots();

            if(!hasValidSlots)
            {
                return GAME_LOSS_NO_VALID_MOVE;
            }
            return PLACEMENT_SUCCESS;
        }
        else
        {
            return GAME_NOT_STARTED;
        }
    }

    /*
     * Checks if the numbers currently placed in the grid are in ascending order,
     * reading from left to right, top to bottom. Empty slots (-1) are ignored
     * in this check.
     *
     * @return {@code true} if the placed numbers are in ascending order,
     * {@code false} otherwise.
     */
    private boolean checkIfSorted()
    {
        int lastNumber = EMPTY_SLOT;
        for(int i = BASE_DEFAULT_NUMBER; i < NUMBER_OF_ROWS; i++)
        {
            for(int j = BASE_DEFAULT_NUMBER; j < NUMBER_OF_COLUMNS; j++)
            {
            if(grid[i][j] != GRID_EMPTY)
                {
                    int currentNumber = grid[i][j];
                    if(currentNumber < lastNumber)
                    {
                        return false;
                    }
                    lastNumber = currentNumber;
                }
            }
        }
        return true;
    }

    /**
     * Prints to the console the possible empty slots in the grid where the next
     * number (obtained by {@link #getCurrentNumber()}) can be validly placed
     * according to the game's ascending order rule. The slots are displayed
     * using a 1-based numbering system (slot 1 is at row 0, col 0, slot 2 at
     * row 0, col 1, and so on). If no valid slots are found, it prints "None".
     */
    private void printPossibleSlots()
    {
        System.out.print(POSSIBLE_SLOTS_LABEL);
        boolean hasPossibleSlots = false;
        for(int i = BASE_DEFAULT_NUMBER; i < NUMBER_OF_ROWS; i++)
        {
            for(int j = BASE_DEFAULT_NUMBER; j < NUMBER_OF_COLUMNS; j++)
            {
                if(grid[i][j] == GRID_EMPTY && isValidPlacement(i,
                                                        j))
                {
                    int slotNumber = (i * NUMBER_OF_COLUMNS) + j + SLOT_NUMBER_OFFSET;
                    System.out.print(slotNumber + " ");
                    hasPossibleSlots = true;
                }
            }
        }
        if(!hasPossibleSlots)
        {
            System.out.print(NO_SLOTS_MESSAGE);
        }
        System.out.println();
    }

    /**
     * Checks if the game is over. The game ends either when all numbers have been
     * successfully placed in the grid (a win condition) or when there are no
     * valid slots left to place the current number (a loss condition).
     *
     * @return {@code true} if the game is over, {@code false} otherwise.
     */
    @Override
    public boolean isGameOver()
    {
        if(currentIndex >= TOTAL_NUMBERS)
        {
            return true;
        }
        return !hasValidPlacement();
    }


    /**
     * Checks if there is at least one empty slot in the grid where the current
     * number (obtained by {@link #getCurrentNumber()}) can be placed according
     * to the game's ascending order rule.
     *
     * @return {@code true} if there is at least one valid slot available for
     * the current number, {@code false} otherwise.
     */
    @Override
    public boolean hasValidPlacement()
    {
        for(int i = BASE_DEFAULT_NUMBER; i < NUMBER_OF_ROWS; i++)
        {
            for(int j = BASE_DEFAULT_NUMBER; j < NUMBER_OF_COLUMNS; j++)
            {
                if(grid[i][j] == GRID_EMPTY && isValidPlacement(i,
                                                                j))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the current number that the player needs to place in the grid.
     *
     * @return the current number to be placed, or {@link #GRID_EMPTY} (-1) if
     * all numbers have been used.
     */
    @Override
    public int getCurrentNumber()
    {
        if(currentIndex < TOTAL_NUMBERS)
        {
            return numbers[currentIndex];
        }
        return GRID_EMPTY;
    }

    /**
     * Generates a summary of the player's performance across all games played.
     * This includes the total number of games played, the number of games won
     * and lost, the total number of successful placements made, and the average
     * number of successful placements per game.
     *
     * @return a {@link String} summarizing the player's score. If no games have
     * been played yet, it returns a specific message indicating that.
     */
    @Override
    public String getScore()
    {
        if(gamesPlayed == BASE_DEFAULT_NUMBER)
        {
            return NO_GAMES_MESSAGE;
        }
        final int           gamesLost         = gamesPlayed - gamesWon;
        final float         averagePlacements = (float) totalPlacements / gamesPlayed;
        final StringBuilder score             = new StringBuilder();
        if(gamesWon > BASE_DEFAULT_NUMBER)
        {
            score.append(SCORE_WON_PREFIX).append(gamesWon).append(SCORE_OUT_OF)
                 .append(gamesPlayed).append(SCORE_GAMES_SUFFIX);
            if(gamesLost > BASE_DEFAULT_NUMBER)
            {
                score.append(SCORE_AND);
            }
        }
        if(gamesLost > BASE_DEFAULT_NUMBER)
        {
            score.append(SCORE_LOST_PREFIX).append(gamesLost).append(SCORE_OUT_OF)
                 .append(gamesPlayed).append(SCORE_GAMES_SUFFIX);
        }
        score.append(SCORE_WITH).append(totalPlacements).append(SCORE_PLACEMENTS_AVG);
        score.append(String.format("%.2f", averagePlacements)).append(SCORE_PER_GAME);
        return score.toString();
    }


    /**
     * Gets the current state of the 4x5 game grid. Each cell in the grid contains
     * either a placed number or {@link #GRID_EMPTY} (-1) if the slot is empty.
     *
     * @return a 4x5 integer array representing the current state of the game board.
     */
    @Override
    public int[][] getGrid()
    {
        return grid;
    }

    /**
     * Validates if placing the current number (obtained by {@link #getCurrentNumber()})
     * at the specified row and column in the grid would be a valid move according
     * to the game's ascending order rule. A placement is valid if the target cell
     * is empty and placing the number there does not violate the ascending order
     * of numbers already placed in adjacent cells (horizontally and vertically).
     * Additionally, placing the number in the prospective slot must result in the
     * entire grid (including the new placement) still being in ascending order
     * when read row by row.
     *
     * @param row the row index (0 to 3) of the slot to check for validity.
     * @param col the column index (0 to 4) of the slot to check for validity.
     * @return {@code true} if the placement is valid, {@code false} otherwise.
     */
    @Override
    public boolean isValidPlacement(final int row,
                                    final int col)
    {
        if(grid[row][col] != GRID_EMPTY)
        {
            return false;
        }
        final int currentNum = numbers[currentIndex];


        if(col > BASE_DEFAULT_NUMBER && grid[row][col - COLS_OFFSET] != GRID_EMPTY
           && grid[row][col - COLS_OFFSET] >= currentNum)
        {
            return false;
        }
        if(col < NUMBER_OF_COLUMNS - COLS_OFFSET && grid[row][col + COLS_OFFSET] != GRID_EMPTY
           && grid[row][col + COLS_OFFSET] <= currentNum)
        {
            return false;
        }
        if(row > BASE_DEFAULT_NUMBER && grid[row - ROWS_OFFSET][col] != GRID_EMPTY
           && grid[row - ROWS_OFFSET][col] >= currentNum)
        {
            return false;
        }
        if(row < NUMBER_OF_ROWS - ROWS_OFFSET && grid[row + ROWS_OFFSET][col] != GRID_EMPTY
           && grid[row + ROWS_OFFSET][col] <= currentNum)
        {
            return false;
        }

        grid[row][col] = currentNum;
        boolean isSorted = checkIfSorted();
        grid[row][col] = GRID_EMPTY;
        return isSorted;
    }

    /**
     * Determines the current status of the game. The game can be in one of three
     * states: "win" if all numbers have been successfully placed in ascending order,
     * "loss" if there are no valid moves left for the current number, or "ongoing"
     * if the game is still in progress. This method updates the count of games won
     * if the game has just been won.
     *
     * @return a {@link String} representing the current game status:
     * {@link #STATUS_WIN} ("win"), {@link #STATUS_LOSS} ("loss"), or
     * {@link #STATUS_ONGOING} ("ongoing").
     */
    @Override
    public String checkGameStatus()
    {
        if(currentIndex >= TOTAL_NUMBERS)
        {
            gamesWon++;
            return STATUS_WIN;
        }
        if(!hasValidPlacement())
        {
            return STATUS_LOSS;
        }
        return STATUS_ONGOING;
    }
}