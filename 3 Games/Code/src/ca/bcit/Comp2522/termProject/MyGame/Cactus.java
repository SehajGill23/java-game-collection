package ca.bcit.Comp2522.termProject.MyGame;

/**
 * The Cactus class represents a cactus obstacle in the LetterRush game.
 * It extends the Obstacle class, defining a specific image, damage, speed multiplier,
 * and collision behavior for the cactus.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class Cactus extends Obstacle
{
    private static final String IMAGE_PATH       = "/spike.png";
    private static final double SPEED_MULTIPLIER = 1.0; // Same as original

    /**
     * Constructs a new Cactus instance at the specified position.
     *
     * @param x the initial x-coordinate of the cactus
     * @param y the initial y-coordinate of the cactus
     */
    Cactus(final double x,
           final double y)
    {
        super(IMAGE_PATH,
              x,
              y);
    }

}