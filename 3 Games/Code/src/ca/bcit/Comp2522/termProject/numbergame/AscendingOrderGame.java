package ca.bcit.Comp2522.termProject.numbergame;

import java.util.Random;

/**
 * Implements the game where numbers must be placed in ascending order in a 4x5 grid.
 * Implements GameController for game flow.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class AscendingOrderGame extends GameBoard implements GameController
{

    protected int[][] grid;
    protected int[]   numbers;
    protected int     currentIndex;
    private   int     gamesPlayed;
    private   int     gamesWon;
    private   int     totalPlacements;

    private static final int    ROWS                    = 4;
    private static final int    COLS                    = 5;
    private static final int    GAME_NOT_STARTED        = -1;
    private static final int    GAME_WIN                = 1;
    private static final int    GAME_LOSS_NO_VALID_MOVE = 2;
    private static final int    GAME_LOSS_OUT_OF_ORDER  = 3;
    private static final int    GRID_EMPTY              = -1;
    private static final int    TOTAL_NUMBERS           = 20;
    private static final int    MAX_RANDOM_NUM          = 1000;
    private static final int    INITIAL_CURR_INDEX      = 0;
    private static final int    MIN_RANDOM_NUM          = 1;
    private static final int    SLOT_NUMBER_OFFSET      = 1;
    private static final int    PLACEMENT_SUCCESS       = 0;
    private static final int    COLS_OFFSET             = 1;
    private static final int    ROWS_OFFSET             = 1;
    private static final String STATUS_WIN              = "win";
    private static final String STATUS_LOSS             = "loss";
    private static final String STATUS_ONGOING          = "ongoing";
    private static final String NO_GAMES_MESSAGE        = "No games played yet.";
    private static final String POSSIBLE_SLOTS_LABEL    = "Possible slots: ";
    private static final String NO_SLOTS_MESSAGE        = "None";
    private static final String GENERATED_NUMBER_LABEL  = "Generated number: ";
    private static final String SCORE_WON_PREFIX        = "You won ";
    private static final String SCORE_OUT_OF            = " out of ";
    private static final String SCORE_GAMES_SUFFIX      = " games";
    private static final String SCORE_AND               = " and ";
    private static final String SCORE_LOST_PREFIX       = "lost ";
    private static final String SCORE_WITH              = ", with ";
    private static final String SCORE_PLACEMENTS_AVG    = " successful placements, an average of ";
    private static final String SCORE_PER_GAME          = " per game.";


    public AscendingOrderGame()
    {
        grid            = new int[ROWS][COLS];
        numbers         = new int[TOTAL_NUMBERS];
        currentIndex    = INITIAL_CURR_INDEX;
        gamesPlayed     = 0;
        gamesWon        = 0;
        totalPlacements = 0;
    }

    /**
     * Starts a new game by initializing the grid, generating numbers, and resetting the game state.
     */
    @Override
    public void startGame()
    {
        // Initialize the grid with -1 (empty)
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
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
     * Generates 20 random numbers between 1 and 100 to be placed in the grid.
     */
    public void generateNumbers()
    {
        Random random = new Random();
        for(int i = 0; i < TOTAL_NUMBERS; i++)
        {
            numbers[i] = random.nextInt(MAX_RANDOM_NUM) + MIN_RANDOM_NUM;
        }
    }

    /**
     * Places a number in the grid at the specified position if the placement is valid.
     *
     * @param row the row index (0-3) in the 4x5 grid
     * @param col the column index (0-4) in the 4x5 grid
     * @param number the number to place
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
     * Attempts to place the current number at the specified grid position.
     *
     * @param row the row index (0-3) in the 4x5 grid
     * @param col the column index (0-4) in the 4x5 grid
     * @return 0 if placement was successful and game continues, 1 if game is won,
     *         2 if loss due to no valid moves, 3 if loss due to out-of-order placement,
     *        -1 if placement was invalid or slot occupied
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
     * Checks if the numbers in the grid are in ascending order.
     * Returns true if sorted, false otherwise.
     */
    private boolean checkIfSorted()
    {
        int lastNumber = -1;
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
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

    /*
     * Prints the possible slots where the next number can be placed.
     * Displays slot numbers (1-based) or "None" if no valid slots exist.
     */
    private void printPossibleSlots()
    {
        System.out.print(POSSIBLE_SLOTS_LABEL);
        boolean hasPossibleSlots = false;
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
            {
                if(grid[i][j] == GRID_EMPTY && isValidPlacement(i,
                                                        j))
                {
                    int slotNumber = (i * COLS) + j + SLOT_NUMBER_OFFSET;
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
     * Checks if the game is over.
     *
     * @return true if the game is over (win or loss), false otherwise
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
     * Checks if there are any valid slots to place the next number.
     *
     * @return true if there is at least one valid slot, false otherwise
     */
    @Override
    public boolean hasValidPlacement()
    {
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
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
     * Gets the current number to be placed.
     *
     * @return the current number, or -1 if no numbers are left
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
     * Gets the player's score summary.
     *
     * @return a string summarizing the number of games won, lost, total placements,
     *         and average placements per game
     */
    @Override
    public String getScore()
    {
        if(gamesPlayed == 0)
        {
            return NO_GAMES_MESSAGE;
        }
        final int           gamesLost         = gamesPlayed - gamesWon;
        final float         averagePlacements = (float) totalPlacements / gamesPlayed;
        final StringBuilder score             = new StringBuilder();
        if(gamesWon > 0)
        {
            score.append(SCORE_WON_PREFIX).append(gamesWon).append(SCORE_OUT_OF)
                 .append(gamesPlayed).append(SCORE_GAMES_SUFFIX);
            if(gamesLost > 0)
            {
                score.append(SCORE_AND);
            }
        }
        if(gamesLost > 0)
        {
            score.append(SCORE_LOST_PREFIX).append(gamesLost).append(SCORE_OUT_OF)
                 .append(gamesPlayed).append(SCORE_GAMES_SUFFIX);
        }
        score.append(SCORE_WITH).append(totalPlacements).append(SCORE_PLACEMENTS_AVG);
        score.append(String.format("%.2f", averagePlacements)).append(SCORE_PER_GAME);
        return score.toString();
    }

    /**
     * Gets the current state of the game grid.
     *
     * @return a 4x5 integer array representing the game board
     */
    @Override
    public int[][] getGrid()
    {
        return grid;
    }

    /**
     * Validates if the specified position is a valid placement for the current number.
     *
     * @param row the row index (0-3) in the 4x5 grid
     * @param col the column index (0-4) in the 4x5 grid
     * @return true if the placement is valid, false otherwise
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


        if(col > 0 && grid[row][col - COLS_OFFSET] != GRID_EMPTY
           && grid[row][col - COLS_OFFSET] >= currentNum)
        {
            return false;
        }
        if(col < COLS - COLS_OFFSET && grid[row][col + COLS_OFFSET] != GRID_EMPTY
           && grid[row][col + COLS_OFFSET] <= currentNum)
        {
            return false;
        }
        if(row > 0 && grid[row - ROWS_OFFSET][col] != GRID_EMPTY
           && grid[row - ROWS_OFFSET][col] >= currentNum)
        {
            return false;
        }
        if(row < ROWS - ROWS_OFFSET && grid[row + ROWS_OFFSET][col] != GRID_EMPTY
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
     * Determines the current game status.
     *
     * @return "win" if the game is won, "loss" if the game is lost, "ongoing" if the game is still in progress
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