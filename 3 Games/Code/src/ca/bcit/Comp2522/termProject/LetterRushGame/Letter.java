package ca.bcit.Comp2522.termProject.LetterRushGame;

import javafx.scene.text.Text;

/**
 * Represents a letter in the LetterRush game that moves around the screen.
 * Each letter has a character value, a position, and a direction of movement.
 * Letters can be part of a target word, a bonus word, or neither, and can be
 * locked to prevent further movement. The letter's position is updated within
 * the boundaries of the game window, and its direction is randomized upon creation.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class Letter
{
    private static final double LETTER_SPEED     = 3.0;
    private static final double LETTER_SIZE      = 20.0;
    private static final int    DIRECTION_RANGE  = 2;
    private static final int    DIRECTION_OFFSET = 1;
    private static final int    MIN_POSITION     = 0;

    private final Text    node;
    private final char    value;
    private final boolean isTarget;
    private final boolean isBonus;
    private       double  dx;
    private       double  dy;
    private       boolean isLocked;

    /**
     * Constructs a new Letter with the specified character value, position, and properties.
     * The letter's direction is randomized upon creation, and its position is set using
     * the provided x and y coordinates. The letter is initially unlocked.
     *
     * @param value    the character value of the letter (e.g., 'A', 'B', etc.)
     * @param x        the initial x-coordinate of the letter
     * @param y        the initial y-coordinate of the letter
     * @param isTarget true if the letter is part of the target word, false otherwise
     * @param isBonus  true if the letter is part of the bonus word, false otherwise
     */
    public Letter(final char value,
                  final double x,
                  final double y,
                  final boolean isTarget,
                  final boolean isBonus)
    {
        this.value = value;
        this.node  = new Text(String.valueOf(value));
        this.node.setX(x);
        this.node.setY(y);
        this.isTarget = isTarget;
        this.isBonus  = isBonus;
        this.isLocked = false;
        randomizeDirection();
    }

    /**
     * Gets the JavaFX Text node representing the letter.
     *
     * @return the Text node of the letter
     */
    public final Text getNode()
    {
        return node;
    }

    /**
     * Gets the character value of the letter.
     *
     * @return the character value (e.g., 'A', 'B', etc.)
     */
    final char getValue()
    {
        return value;
    }

    /**
     * Checks if the letter is locked and cannot move.
     *
     * @return true if the letter is locked, false otherwise
     */
    final boolean isLocked()
    {
        return isLocked;
    }

    /**
     * Locks the letter, preventing it from moving.
     */
    final void lock()
    {
        isLocked = true;
    }

    /**
     * Updates the letter's position based on its current direction and speed.
     * If the letter is locked, its position does not change. The letter bounces
     * off the boundaries of the game window, defined by the specified width and height.
     *
     * @param width  the width of the game window in pixels
     * @param height the height of the game window in pixels
     */
    public void updatePosition(final int width,
                               final int height)
    {
        if(isLocked)
        {
            return;
        }

        double x = node.getX() + dx;
        double y = node.getY() + dy;

        if(x <= MIN_POSITION || x >= width - LETTER_SIZE)
        {
            dx = -dx;
            x  = Math.max(MIN_POSITION,
                          Math.min(x,
                                   width - LETTER_SIZE));
        }
        if(y <= MIN_POSITION || y >= height - LETTER_SIZE)
        {
            dy = -dy;
            y  = Math.max(MIN_POSITION,
                          Math.min(y,
                                   height - LETTER_SIZE));
        }

        node.setX(x);
        node.setY(y);
    }

    /**
     * Randomizes the letter's direction by setting its horizontal and vertical
     * speeds (dx and dy) to random values. The speed is scaled by LETTER_SPEED,
     * and the direction is randomly chosen to be positive or negative.
     */
    private void randomizeDirection()
    {
        dx = (Math.random() * DIRECTION_RANGE - DIRECTION_OFFSET) * LETTER_SPEED;
        dy = (Math.random() * DIRECTION_RANGE - DIRECTION_OFFSET) * LETTER_SPEED;
    }
}