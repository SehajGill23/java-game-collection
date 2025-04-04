package ca.bcit.Comp2522.termProject.MyGame;

public class Cactus extends Obstacle
{
    private static final String IMAGE_PATH        = "/spike.png";

    public Cactus(final double x,
                  final double y)
    {
        super(IMAGE_PATH,
              x,
              y);
    }
}