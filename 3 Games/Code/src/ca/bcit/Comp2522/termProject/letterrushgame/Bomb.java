package ca.bcit.Comp2522.termProject.letterrushgame;

/**
 * The Bomb class represents a bomb obstacle in the LetterRush game.
 * It extends the Obstacle class, defining a specific image, damage, speed multiplier,
 * and collision behavior for the bomb.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class Bomb extends Obstacle
{
    private static final String IMAGE_PATH       = "/bomb.png";

    /**
     * Constructs a new Bomb instance at the specified position.
     *
     * @param x the initial x-coordinate of the bomb
     * @param y the initial y-coordinate of the bomb
     */
    Bomb(final double x,
         final double y)
    {
        super(IMAGE_PATH,
              x,
              y);
    }
}