package ca.bcit.Comp2522.termProject.Letterrushgame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The Obstacle class is an abstract base class for obstacles in the LetterRush game.
 * It defines common properties and behaviors for obstacles, such as position, movement,
 * and collision detection with the player.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public abstract class Obstacle
{
    private static final double OBSTACLE_WIDTH         = 20.0;
    private static final double OBSTACLE_HEIGHT        = 20.0;
    private static final double SPEED                  = 4.0;
    private static final double HALF_WIDTH             = OBSTACLE_WIDTH / 2.0;
    private static final double HALF_HEIGHT            = OBSTACLE_HEIGHT / 2.0;
    private static final double MIN_POSITION           = 0.0;
    private static final double SPEED_RANGE_MIN        = -1.0;
    private static final double SPEED_RANGE_MULTIPLIER = 2.0;
    private static final String DEFAULT_IMAGE_PATH     = "/default.png";
    private static final String ERROR_MESSAGE_PREFIX   = "Error loading obstacle image (";
    private static final String ERROR_MESSAGE_MIDDLE   = "): ";
    private static final String ERROR_FAILED_MESSAGE   = "Obstacle image failed to load (";
    private static final String OBSTACLE_STYLE_CLASS   = "obstacle";

    private final ImageView node;
    private       double    dx;
    private       double    dy;

    /**
     * Constructs a new Obstacle instance with the specified image and initial position.
     *
     * @param imagePath the path to the obstacle's image
     * @param x         the initial x-coordinate of the obstacle
     * @param y         the initial y-coordinate of the obstacle
     */
    public Obstacle(final String imagePath,
                    final double x,
                    final double y)
    {
        Image image;
        image = null;

        try
        {
            image = new Image(imagePath,
                              OBSTACLE_WIDTH,
                              OBSTACLE_HEIGHT,
                              true,
                              true);

            if(image.isError())
            {
                System.err.println(ERROR_FAILED_MESSAGE + imagePath + ERROR_MESSAGE_MIDDLE + image.getException());
            }
        }
        catch(final Exception e)
        {
            System.err.println(ERROR_MESSAGE_PREFIX + imagePath + ERROR_MESSAGE_MIDDLE + e.getMessage());
        }

        this.node = new ImageView(image != null && !image.isError() ? image : new Image(DEFAULT_IMAGE_PATH));
        node.setFitWidth(OBSTACLE_WIDTH);
        node.setFitHeight(OBSTACLE_HEIGHT);
        node.setPreserveRatio(true);
        node.setX(x);
        node.setY(y);
        node.getStyleClass().add(OBSTACLE_STYLE_CLASS);
        randomizeSpeed();
    }

    /*
     * Returns the ImageView node representing the obstacle.
     *
     * @return the ImageView node
     */
    final ImageView getNode()
    {
        return node;
    }

    /*
     * Updates the obstacle's position, handling boundary collisions within the game area.
     *
     * @param width  the width of the game area
     * @param height the height of the game area
     */
    final void update(final int width,
                      final int height)
    {
        double x;
        x = node.getX() + dx;

        double y;
        y = node.getY() + dy;

        if(x <= MIN_POSITION || x >= width - OBSTACLE_WIDTH)
        {
            dx = -dx;
            x  = Math.max(MIN_POSITION,
                          Math.min(x,
                                   width - OBSTACLE_WIDTH));
        }

        if(y <= MIN_POSITION || y >= height - OBSTACLE_HEIGHT)
        {
            dy = -dy;
            y  = Math.max(MIN_POSITION,
                          Math.min(y,
                                   height - OBSTACLE_HEIGHT));
        }

        node.setX(x);
        node.setY(y);
    }

    /*
     * Checks if the obstacle collides with the player's cursor.
     *
     * @param player the player to check collision against
     * @return true if a collision occurs, false otherwise
     */
    final boolean collidesWith(final Player player)
    {
        final double obstacleCenterX;
        obstacleCenterX = node.getX() + HALF_WIDTH;

        final double obstacleCenterY;
        obstacleCenterY = node.getY() + HALF_HEIGHT;

        final double cursorCenterX;
        cursorCenterX = player.getCursorX();

        final double cursorCenterY;
        cursorCenterY = player.getCursorY();

        final double distance;
        distance = Math.sqrt(Math.pow(obstacleCenterX - cursorCenterX,
                                      SPEED_RANGE_MULTIPLIER) + Math.pow(obstacleCenterY - cursorCenterY,
                                                                         SPEED_RANGE_MULTIPLIER));

        final double collisionDistance;
        collisionDistance = HALF_WIDTH + (player.getCursorSize() / SPEED_RANGE_MULTIPLIER);

        return distance <= collisionDistance;
    }

    /*
     * Randomizes the obstacle's speed in both x and y directions.
     */
    private void randomizeSpeed()
    {
        dx = (Math.random() * SPEED_RANGE_MULTIPLIER + SPEED_RANGE_MIN) * SPEED;
        dy = (Math.random() * SPEED_RANGE_MULTIPLIER + SPEED_RANGE_MIN) * SPEED;
    }
}