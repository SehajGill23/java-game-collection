package ca.bcit.Comp2522.termProject.letterrushgame;

/**
 * The Missile class represents a missile obstacle in the LetterRush game.
 * It extends the Obstacle class, defining a specific image, damage, speed multiplier,
 * and collision behavior for the missile.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class Missile extends Obstacle
{
    private static final String IMAGE_PATH = "/missile.png";

    /*
     * Constructs a new Missile instance at the specified position.
     *
     * @param x the initial x-coordinate of the missile
     * @param y the initial y-coordinate of the missile
     */
    Missile(final double x,
            final double y)
    {
        super(IMAGE_PATH,
              x,
              y);
    }

}