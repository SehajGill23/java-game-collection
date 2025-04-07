package ca.bcit.Comp2522.termProject.letterrushgame;

/*
 * Represents a cactus obstacle in the LetterRush game, extending the
 * {@code Obstacle} class to define a specific type of hazard. The cactus
 * is characterized by a unique image loaded from a resource file
 * ({@code IMAGE_PATH}), and it inherits behavior such as movement,
 * collision detection, and damage application from its parent class.
 * Positioned at specified coordinates in pixels ({@code xPixels},
 * {@code yPixels}) within the game window, the cactus serves as an
 * environmental challenge that players must avoid to prevent penalties
 * or game-over conditions. This class customizes the base
 * {@code Obstacle} functionality with a cactus-specific appearance.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class Cactus extends Obstacle
{
    private static final String IMAGE_PATH = "/spike.png";

    /*
     * Constructs a new {@code Cactus} instance at the specified position
     * within the LetterRush game window. This constructor initializes the
     * cactus by passing its image path ({@code IMAGE_PATH}) and initial
     * coordinates in pixels ({@code xPixels}, {@code yPixels}) to the
     * {@code Obstacle} superclass constructor. The {@code xPixels} and
     * {@code yPixels} parameters determine where the cactus appears on the
     * screen, leveraging the parent class to set up its visual
     * representation, movement properties, and collision behavior. The
     * cactus is ready to interact with the player and game environment upon
     * creation.
     *
     * @param xPixels the initial x-coordinate of the cactus in pixels
     * @param yPixels the initial y-coordinate of the cactus in pixels
     */
    Cactus(final double xPixels,
           final double yPixels)
    {
        super(IMAGE_PATH,
              xPixels,
              yPixels);
    }
}