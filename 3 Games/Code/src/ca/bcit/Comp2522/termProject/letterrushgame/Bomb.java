package ca.bcit.comp2522.termproject.letterrushgame;

/**
 * Represents a bomb obstacle in the LetterRush game, extending the
 * {@code Obstacle} class to define a specific type of explosive hazard.
 * The bomb is characterized by a distinct image loaded from a resource
 * file ({@code IMAGE_PATH}), and it inherits functionality such as
 * movement, collision detection, and damage infliction from its parent
 * class. Positioned at specified coordinates in pixels ({@code xPixels},
 * {@code yPixels}) within the game window, the bomb poses a significant
 * threat to the player, potentially causing immediate penalties or ending
 * the game upon contact. This class tailors the base {@code Obstacle}
 * behavior with a bomb-specific visual identity.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class Bomb extends Obstacle
{
    private static final String IMAGE_PATH = "/bomb.png";

    /*
     * Constructs a new {@code Bomb} instance at the specified position
     * within the LetterRush game window. This constructor initializes the
     * bomb by passing its image path ({@code IMAGE_PATH}) and initial
     * coordinates in pixels ({@code xPixels}, {@code yPixels}) to the
     * {@code Obstacle} superclass constructor. The {@code xPixels} and
     * {@code yPixels} parameters define the bomb’s starting location on the
     * screen, utilizing the parent class to establish its graphical
     * display, movement dynamics, and collision effects. The bomb is fully
     * prepared to engage with the game environment and player upon
     * instantiation.
     *
     * @param xPixels the initial x-coordinate of the bomb in pixels
     * @param yPixels the initial y-coordinate of the bomb in pixels
     */
    Bomb(final double xPixels,
         final double yPixels)
    {
        super(IMAGE_PATH,
              xPixels,
              yPixels);
    }
}