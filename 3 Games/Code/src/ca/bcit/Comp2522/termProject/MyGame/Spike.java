package ca.bcit.Comp2522.termProject.MyGame;

public class Spike extends Obstacle
{
    private static final String IMAGE_PATH        = "/spike.png";

    public Spike(final double x,
                 final double y)
    {
        super(IMAGE_PATH,
              x,
              y);
    }
}