package ca.bcit.comp2522.termproject.numbergame;

/**
 * Defines the contract for controlling the flow of a grid-based number placement game.
 * Implementing classes are responsible for managing the lifecycle of a game, including
 * its initialization, the process of placing numbers onto the game board, and the
 * determination of the game's current status (e.g., ongoing, won, or lost).
 * This interface provides a set of methods that any game controller for such a game
 * must implement, ensuring a consistent API for interacting with the game logic.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public interface GameController
{

    /**
     * Starts a new game session. This method should handle all necessary initializations,
     * such as resetting the game board to its starting state, generating the sequence
     * of numbers to be used in the game, and setting any initial game flags or counters.
     */
    void startGame();

    /**
     * Attempts to place the current number, as determined by the game's logic, at the
     * specified row and column on the game grid. The implementation should validate
     * whether the placement is allowed according to the game's rules.
     *
     * @param row the row index (0-based, typically 0 to 3 for a 4x5 grid) where the
     * player attempts to place the number.
     * @param col the column index (0-based, typically 0 to 4 for a 4x5 grid) where the
     * player attempts to place the number.
     * @return An integer code representing the outcome of the placement attempt and the
     * current game state:
     * <ul>
     * <li>{@code 0}: Placement was successful, and the game is still in progress.</li>
     * <li>{@code 1}: The game has been won as a result of this placement.</li>
     * <li>{@code 2}: The game has been lost because there are no valid moves remaining.</li>
     * <li>{@code 3}: The game has been lost due to an out-of-order or otherwise invalid
     * placement according to the game's specific rules.</li>
     * <li>{@code -1}: The attempted placement was invalid, or the selected grid slot
     * was already occupied. The game state may or may not have changed.</li>
     * </ul>
     */
    int placeNumber(final int row,
                    final int col);

    /**
     * Checks if the game has reached a terminal state (either won or lost).
     *
     * @return {@code true} if the game is over, {@code false} if the game is still ongoing.
     */
    boolean isGameOver();

    /**
     * Gets the current number that the player is expected to place on the game board.
     *
     * @return the current number to be placed, or a specific value (e.g., -1) to indicate
     * that there are no more numbers left to place.
     */
    int getCurrentNumber();

    /**
     * Retrieves a summary of the player's performance in the current or across multiple
     * game sessions. This summary typically includes statistics such as the number of
     * games won, the number of games lost, the total number of successful placements made,
     * and possibly an average of successful placements per game.
     *
     * @return a {@code String} containing a human-readable summary of the player's score
     * and performance.
     */
    String getScore();

    /**
     * Gets the current state of the game grid. This method provides access to the
     * underlying data structure representing the game board.
     *
     * @return a 2D integer array representing the game board. For a 4x5 grid, this
     * would be a 4x5 {@code int[][]} array. The values in the array represent the
     * numbers placed in the grid or a specific value indicating an empty cell.
     */
    int[][] getGrid();

    /**
     * Validates if placing the current number at the specified position on the grid
     * would be a valid move according to the game's rules. This method checks if the
     * target cell is empty and if the placement adheres to any specific constraints
     * of the game (e.g., ascending order in the Ascending Order Game).
     *
     * @param row the row index (0-based) of the position to check for validity.
     * @param col the column index (0-based) of the position to check for validity.
     * @return {@code true} if the placement is valid, {@code false} otherwise.
     */
    boolean isValidPlacement(final int row,
                             final int col);

    /**
     * Determines the current status of the game based on the game state.
     *
     * @return a {@code String} indicating the current game status. Common values might include:
     * <ul>
     * <li>{@code "win"}: The game has been won.</li>
     * <li>{@code "loss"}: The game has been lost.</li>
     * <li>{@code "ongoing"}: The game is currently in progress.</li>
     * </ul>
     */
    String checkGameStatus();

    /**
     * Checks if there are any valid empty slots on the game grid where the next
     * number can be placed according to the game's rules. This is often used to
     * determine if the game can continue or if a loss condition (no valid moves)
     * has been reached.
     *
     * @return {@code true} if there is at least one valid slot available for the
     * current number, {@code false} otherwise.
     */
    boolean hasValidPlacement();
}