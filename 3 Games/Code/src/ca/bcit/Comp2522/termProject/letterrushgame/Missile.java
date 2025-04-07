package ca.bcit.Comp2522.termProject.letterrushgame;

/**
 * Represents a missile obstacle in the LetterRush game, extending the Obstacle class to define a specific
 * type of fast-moving hazard. The missile is distinguished by a unique image sourced from a resource file,
 * and it inherits properties like movement, collision detection, and damage application from its parent class.
 * Positioned at specified coordinates in pixels within the game window, the missile challenges the player
 * with its potentially rapid and unpredictable trajectory, risking penalties or game termination upon impact.
 * This class customizes the base Obstacle functionality with a missile-specific appearance.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class Missile extends Obstacle
{
    private static final String IMAGE_PATH = "/missile.png";

    /**
     * Constructs a new Missile instance at the specified position within the LetterRush game window.
     * This constructor initializes the missile by passing its image path (IMAGE_PATH) and initial coordinates
     * in pixels to the Obstacle superclass constructor. The xPixels and yPixels parameters specify the missile’s
     * initial placement on the screen, relying on the parent class to configure its visual rendering, movement
     * characteristics, and collision mechanics. The missile is immediately ready to interact with the player
     * and game environment following construction.
     *
     * @param xPixels the initial x-coordinate of the missile in pixels
     * @param yPixels the initial y-coordinate of the missile in pixels
     */
    Missile(final double xPixels,
            final double yPixels)
    {
        super(IMAGE_PATH,
              xPixels,
              yPixels);
    }
}