package ca.bcit.Comp2522.termProject.NumberGame;

import java.util.Random;

/**
 * Implements the game where numbers must be placed in ascending order in a 4x5 grid.
 * Implements GameController for game flow.
 */
public class AscendingOrderGame implements GameController
{

    protected            int[][] grid;
    protected            int[]   numbers;
    protected            int     currentIndex;
    private              int     gamesPlayed;
    private              int     gamesWon;
    private              int     totalPlacements;
    private static final int     ROWS          = 4;
    private static final int     COLS          = 5;
    private static final int     TOTAL_NUMBERS = 20;

    public AscendingOrderGame()
    {
        grid            = new int[ROWS][COLS];
        numbers         = new int[TOTAL_NUMBERS];
        currentIndex    = 0;
        gamesPlayed     = 0;
        gamesWon        = 0;
        totalPlacements = 0;
    }

    @Override
    public void startGame()
    {
        // Initialize the grid with -1 (empty)
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
            {
                grid[i][j] = -1;
            }
        }
        // Generate 20 random numbers between 1 and 1000
        generateNumbers();
        currentIndex = 0;
        gamesPlayed++;
        // Print the first number and possible slots
        System.out.println("Generated number: " + numbers[currentIndex]);
        printPossibleSlots();
    }

    protected void generateNumbers()
    {
        Random random = new Random();
        for(int i = 0; i < TOTAL_NUMBERS; i++)
        {
            numbers[i] = random.nextInt(1000) + 1; // 1 to 1000 inclusive
        }
    }

    protected void placeNumberInGrid(final int row,
                                     final int col,
                                     final int number)
    {
        grid[row][col] = number;
    }

    @Override
    public int placeNumber(final int row,
                           final int col)
    {
        if(grid[row][col] != -1)
        {
            return -1; // Slot already occupied
        }
        if(isValidPlacement(row,
                            col))
        {
            placeNumberInGrid(row,
                              col,
                              numbers[currentIndex]);
            currentIndex++;
            totalPlacements++;

            // Check if the current placement violates ascending order
            if(!checkIfSorted())
            {
                return 3; // Loss: Numbers are not in ascending order
            }

            if(currentIndex >= TOTAL_NUMBERS)
            {
                return 1; // Win
            }
            // Print the next number
            System.out.println("Generated number: " + numbers[currentIndex]);
            // Check if there are any valid slots for the next number
            boolean hasValidSlots = hasValidPlacement();

            printPossibleSlots();
            if(!hasValidSlots)
            {
                return 2;
            }
            return 0;
        }
        else
        {
            return -1;
        }
    }

    private boolean checkIfSorted()
    {
        int lastNumber = -1;
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
            {
                if(grid[i][j] != -1)
                {
                    int currentNumber = grid[i][j];
                    if(currentNumber < lastNumber)
                    {
                        return false; // Numbers are not in ascending order
                    }
                    lastNumber = currentNumber;
                }
            }
        }
        return true;
    }

    private void printPossibleSlots()
    {
        System.out.print("Possible slots: ");
        boolean hasPossibleSlots = false;
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
            {
                if(grid[i][j] == -1 && isValidPlacement(i,
                                                        j))
                {
                    int slotNumber = (i * COLS) + j + 1; // 1-based slot number
                    System.out.print(slotNumber + " ");
                    hasPossibleSlots = true;
                }
            }
        }
        if(!hasPossibleSlots)
        {
            System.out.print("None");
        }
        System.out.println();
    }

    @Override
    public boolean isGameOver()
    {
        if(currentIndex >= TOTAL_NUMBERS)
        {
            return true;
        }
        return !hasValidPlacement();
    }

    @Override
    public boolean hasValidPlacement()
    {
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
            {
                if(grid[i][j] == -1 && isValidPlacement(i,
                                                        j))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getCurrentNumber()
    {
        if(currentIndex < TOTAL_NUMBERS)
        {
            return numbers[currentIndex];
        }
        return -1;
    }

    @Override
    public String getScore()
    {
        if(gamesPlayed == 0)
        {
            return "No games played yet.";
        }
        final int           gamesLost         = gamesPlayed - gamesWon;
        final float         averagePlacements = (float) totalPlacements / gamesPlayed;
        final StringBuilder score             = new StringBuilder();
        if(gamesWon > 0)
        {
            score.append("You won ").append(gamesWon).append(" out of ").append(gamesPlayed).append(" games");
            if(gamesLost > 0)
            {
                score.append(" and ");
            }
        }
        if(gamesLost > 0)
        {
            score.append("lost ").append(gamesLost).append(" out of ").append(gamesPlayed).append(" games");
        }
        score.append(", with ").append(totalPlacements).append(" successful placements, an average of ");
        score.append(String.format("%.2f",
                                   averagePlacements)).append(" per game.");
        return score.toString();
    }

    @Override
    public int[][] getGrid()
    {
        return grid;
    }

    @Override
    public boolean isValidPlacement(final int row,
                                    final int col)
    {
        if(grid[row][col] != -1)
        {
            return false; // Slot already occupied
        }
        final int currentNum = numbers[currentIndex];

        // Check immediate neighbors
        if(col > 0 && grid[row][col - 1] != -1 && grid[row][col - 1] >= currentNum)
        {
            return false; // Left neighbor must be smaller
        }
        if(col < COLS - 1 && grid[row][col + 1] != -1 && grid[row][col + 1] <= currentNum)
        {
            return false; // Right neighbor must be larger
        }
        if(row > 0 && grid[row - 1][col] != -1 && grid[row - 1][col] >= currentNum)
        {
            return false; // Above neighbor must be smaller
        }
        if(row < ROWS - 1 && grid[row + 1][col] != -1 && grid[row + 1][col] <= currentNum)
        {
            return false; // Below neighbor must be larger
        }

        return true;
    }

    @Override
    public String checkGameStatus()
    {
        if(currentIndex >= TOTAL_NUMBERS)
        {
            gamesWon++;
            return "win";
        }
        if(!hasValidPlacement())
        {
            return "loss";
        }
        return "ongoing";
    }
}