package ca.bcit.Comp2522.termProject.letterrushgame;

import javafx.scene.text.Text;

/**
 * Represents a letter in the LetterRush game that moves dynamically across the game window.
 * Each letter instance encapsulates a single character value (e.g., 'A', 'B'), a position defined
 * by x and y coordinates in pixels, and a movement direction determined by horizontal and vertical
 * speed components (dxPixels and dyPixels). Letters can be designated as part of a target word or
 * a bonus word, influencing gameplay mechanics, and can be locked to halt movement after interaction.
 * The letter’s position updates within the specified window boundaries, bouncing off edges when it
 * reaches them, with its initial direction randomized during construction. This class leverages
 * JavaFX’s Text node for rendering the letter visually on the screen.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class Letter
{
    private static final double LETTER_SPEED_PIXELS_PER_UPDATE = 3.0;
    private static final double LETTER_SIZE_PIXELS             = 20.0;
    private static final int    DIRECTION_RANGE                = 2;
    private static final int    DIRECTION_OFFSET               = 1;
    private static final int    MIN_POSITION_PIXELS            = 0;

    private final Text    node;
    private final char    value;
    private final boolean target;
    private final boolean bonus;
    private       double  dxPixels;
    private       double  dyPixels;
    private       boolean locked;

    /**
     * Constructs a new Letter instance with the specified character value, initial position,
     * and gameplay properties. This constructor initializes the letter’s JavaFX Text node to
     * display the character, sets its initial position using the provided x and y coordinates
     * in pixels, and assigns its role as part of the target word or bonus word based on the
     * provided boolean flags. The letter starts unlocked, allowing it to move, and its movement
     * direction is randomized by invoking randomizeDirection(). The resulting letter object is
     * ready to be rendered and updated within the game window.
     *
     * @param valueChar the character value of the letter (e.g., 'A', 'B') to be displayed
     * @param xPixels   the initial x-coordinate of the letter in pixels within the game window
     * @param yPixels   the initial y-coordinate of the letter in pixels within the game window
     * @param target    true if the letter is part of the target word, false otherwise
     * @param bonus     true if the letter is part of the bonus word, false otherwise
     */
    public Letter(final char valueChar,
                  final double xPixels,
                  final double yPixels,
                  final boolean target,
                  final boolean bonus)
    {
        this.node     = new Text(String.valueOf(valueChar));
        this.value    = valueChar;
        this.target   = target;
        this.bonus    = bonus;
        this.dxPixels = 0.0;
        this.dyPixels = 0.0;
        this.locked   = false;

        this.node.setX(xPixels);
        this.node.setY(yPixels);

        randomizeDirection();
    }

    /**
     * Updates the letter’s position based on its current direction and speed within the game window.
     * If the letter is locked, this method exits immediately without altering its position, preserving
     * its current location. For an unlocked letter, the method calculates a new position by adding
     * the horizontal speed (dxPixels) to the current x-coordinate and the vertical speed (dyPixels)
     * to the current y-coordinate. The letter bounces off the window boundaries (left, right, top,
     * bottom) by reversing its direction (negating dxPixels or dyPixels) when it exceeds the minimum
     * position (MIN_POSITION_PIXELS) or maximum position (widthPixels - LETTER_SIZE_PIXELS or
     * heightPixels - LETTER_SIZE_PIXELS). The position is then clamped to ensure the letter remains
     * fully within the window, accounting for its size in pixels. Finally, the updated coordinates
     * are applied to the Text node for rendering.
     *
     * @param widthPixels  the width of the game window in pixels, defining the right boundary
     * @param heightPixels the height of the game window in pixels, defining the bottom boundary
     */
    public void updatePosition(final int widthPixels,
                        final int heightPixels)
    {
        if(locked)
        {
            return;
        }

        final double xPixels;
        final double yPixels;

        xPixels = node.getX() + dxPixels;
        yPixels = node.getY() + dyPixels;

        if(xPixels <= MIN_POSITION_PIXELS || xPixels >= widthPixels - LETTER_SIZE_PIXELS)
        {
            dxPixels = -dxPixels;
            node.setX(Math.max(MIN_POSITION_PIXELS,
                               Math.min(xPixels,
                                        widthPixels - LETTER_SIZE_PIXELS)));
        }
        else
        {
            node.setX(xPixels);
        }

        if(yPixels <= MIN_POSITION_PIXELS || yPixels >= heightPixels - LETTER_SIZE_PIXELS)
        {
            dyPixels = -dyPixels;
            node.setY(Math.max(MIN_POSITION_PIXELS,
                               Math.min(yPixels,
                                        heightPixels - LETTER_SIZE_PIXELS)));
        }
        else
        {
            node.setY(yPixels);
        }
    }

    /**
     * Retrieves the JavaFX Text node that visually represents the letter in the game.
     * This node contains the letter’s character value, current position (xPixels, yPixels),
     * and styling properties, serving as the graphical element rendered in the JavaFX scene.
     * The node is immutable in terms of its identity (not replaced after construction),
     * though its position can be updated via updatePosition().
     *
     * @return the Text node representing this letter
     */
    public final Text getNode()
    {
        return node;
    }

    /*
     * Retrieves the character value of the letter.
     * This method returns the single character (e.g., 'A', 'B') that defines the letter’s
     * identity and is displayed via the Text node. The value is immutable after construction.
     *
     * @return the character value of the letter
     */
    final char getValue()
    {
        return value;
    }

    /*
     * Checks whether the letter is currently locked, preventing further movement.
     * A locked letter remains stationary when updatePosition() is called, typically after
     * being clicked or otherwise interacted with in the game.
     *
     * @return true if the letter is locked, false otherwise
     */
    final boolean lockedLetter()
    {
        return locked;
    }

    /*
     * Locks the letter, setting its state to prevent movement.
     * Once invoked, the letter will no longer update its position until the game state resets.
     */
    final void lock()
    {
        locked = true;
    }

    /*
     * Randomizes the letter’s movement direction by assigning random horizontal (dxPixels)
     * and vertical (dyPixels) speed components. The direction is generated using a random
     * value between -1 and 1 (calculated as Math.random() * DIRECTION_RANGE - DIRECTION_OFFSET),
     * scaled by LETTER_SPEED_PIXELS_PER_UPDATE to determine the speed in pixels per update.
     * This method is called during construction to set the initial movement direction.
     */
    private void randomizeDirection()
    {

        final double randomX;
        final double randomY;

        randomX = Math.random() * DIRECTION_RANGE - DIRECTION_OFFSET;
        randomY = Math.random() * DIRECTION_RANGE - DIRECTION_OFFSET;

        dxPixels = randomX * LETTER_SPEED_PIXELS_PER_UPDATE;
        dyPixels = randomY * LETTER_SPEED_PIXELS_PER_UPDATE;
    }
}