package ca.bcit.Comp2522.termProject.numbergame;

/**
 * Defines the contract for controlling the game flow in a grid-based number placement game.
 * Implementing classes must manage starting the game, placing numbers, and tracking game status.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public interface GameController {

    /**
     * Starts a new game by initializing the game state, grid, and numbers.
     */
    void startGame();

    /**
     * Attempts to place the current number at the specified grid position.
     *
     * @param row the row index (0-3) in the 4x5 grid
     * @param col the column index (0-4) in the 4x5 grid
     * @return 0 if placement was successful and game continues, 1 if game is won,
     *         2 if loss due to no valid moves, 3 if loss due to out-of-order placement,
     *         -1 if placement was invalid or slot occupied
     */
    int placeNumber(int row, int col);

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over (win or loss), false otherwise
     */
    boolean isGameOver();

    /**
     * Gets the current number to be placed.
     *
     * @return the current number, or -1 if no numbers are left
     */
    int getCurrentNumber();

    /**
     * Gets the player's score summary.
     *
     * @return a string summarizing the number of games won, lost, total placements,
     * and average placements per game
     */
    String getScore();

    /**
     * Gets the current state of the game grid.
     *
     * @return a 4x5 integer array representing the game board
     */
    int[][] getGrid();

    /**
     * Validates if the specified position is a valid placement for the current number.
     *
     * @param row the row index (0-3) in the 4x5 grid
     * @param col the column index (0-4) in the 4x5 grid
     * @return true if the placement is valid, false otherwise
     */
    boolean isValidPlacement(int row, int col);

    /**
     * Determines the current game status.
     *
     * @return "win" if the game is won, "loss" if the game is lost,
     * "ongoing" if the game is still in progress
     */
    String checkGameStatus();

    /**
     * Checks if there are any valid slots to place the next number.
     *
     * @return true if there is at least one valid slot, false otherwise
     */
    boolean hasValidPlacement();
}