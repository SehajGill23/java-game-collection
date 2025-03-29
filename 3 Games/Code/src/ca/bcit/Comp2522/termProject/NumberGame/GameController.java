package ca.bcit.Comp2522.termProject.NumberGame;

/**
 * Defines the contract for controlling the game flow in a grid-based number placement game.
 * Implementing classes must manage starting the game, placing numbers, and tracking game status.
 */
public interface GameController
{

    void startGame();

    /**
     * Attempts to place the current number at the specified grid position.
     *
     * @param row the row index (0-3) in the 4x5 grid
     * @param col the column index (0-4) in the 4x5 grid
     * @return 0 if placement was successful and game continues, 1 if game should end (win), -1 if placement was invalid or slot occupied
     */
    int placeNumber(int row,
                    int col);

    boolean isGameOver();

    int getCurrentNumber();

    String getScore();

    int[][] getGrid();

    boolean isValidPlacement(int row,
                             int col);

    String checkGameStatus();

    boolean hasValidPlacement();
}