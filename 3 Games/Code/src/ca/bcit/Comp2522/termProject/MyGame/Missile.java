package ca.bcit.Comp2522.termProject.MyGame;

public class Missile extends Obstacle
{ // Lesson 2: Inheritance
    private static final String IMAGE_PATH = "/missile.png";

    public Missile(final double x,
                   final double y)
    {
        super(IMAGE_PATH,
              x,
              y);
    }
}