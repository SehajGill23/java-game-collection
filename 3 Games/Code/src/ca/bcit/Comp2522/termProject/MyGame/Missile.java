package ca.bcit.Comp2522.termProject.MyGame;

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
    private static final String IMAGE_PATH       = "/missile.png";
    private static final double SPEED_MULTIPLIER = 1.0; // Same as original

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

    /**
     * Returns the speed multiplier for the missile, affecting its movement speed.
     *
     * @return the speed multiplier (1.0, same as original)
     */
    @Override
    public double getSpeedMultiplier()
    {
        return SPEED_MULTIPLIER;
    }
}