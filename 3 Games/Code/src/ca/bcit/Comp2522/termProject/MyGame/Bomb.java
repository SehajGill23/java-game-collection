package ca.bcit.Comp2522.termProject.MyGame;


public class Bomb extends Obstacle
{ // Lesson 2: Inheritance
    private static final String IMAGE_PATH   = "/bomb.png";

    public Bomb(final double x,
                final double y)
    {
        super(IMAGE_PATH,
              x,
              y);
    }
}
