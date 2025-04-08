package ca.bcit.comp2522.termproject.letterrushgame;

/**
 * Represents a missile obstacle in the LetterRush game, extending the
 * {@code Obstacle} class to define a specific type of fast-moving hazard.
 * The missile is distinguished by a unique image sourced from a resource
 * file ({@code IMAGE_PATH}), and it inherits properties like movement,
 * collision detection, and damage application from its parent class.
 * Positioned at specified coordinates in pixels ({@code xPixels},
 * {@code yPixels}) within the game window, the missile challenges the
 * player with its potentially rapid and unpredictable trajectory, risking
 * penalties or game termination upon impact. This class customizes the
 * base {@code Obstacle} functionality with a missile-specific appearance.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class Missile extends Obstacle
{
    private static final String IMAGE_PATH = "/missile.png";

    /*
     * Constructs a new {@code Missile} instance at the specified position
     * within the LetterRush game window. This constructor initializes the
     * missile by passing its image path ({@code IMAGE_PATH}) and initial
     * coordinates in pixels ({@code xPixels}, {@code yPixels}) to the
     * {@code Obstacle} superclass constructor. The {@code xPixels} and
     * {@code yPixels} parameters specify the missile’s initial placement
     * on the screen, relying on the parent class to configure its visual
     * rendering, movement characteristics, and collision mechanics. The
     * missile is immediately ready to interact with the player and game
     * environment following construction.
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