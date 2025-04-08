package ca.bcit.Comp2522.termProject.letterrushgame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The Obstacle class is an abstract base class for obstacles in the LetterRush game.
 * It defines common properties and behaviors for obstacles, such as position, movement,
 * and collision detection with the player. Subclasses of Obstacle will implement
 * specific behaviors for different types of obstacles.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
abstract class Obstacle
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

    /*
     * Constructs a new Obstacle instance. This constructor is responsible for
     * initializing the visual representation and basic properties of an obstacle.
     * It attempts to load the image specified by the {@code imagePath}. If the
     * image loading is successful, the obstacle's {@link ImageView} node is created
     * with this image and set to the predefined {@link #OBSTACLE_WIDTH} and
     * {@link #OBSTACLE_HEIGHT}. If the image fails to load (either due to an
     * exception during loading or if the image reports an error after loading),
     * a default image located at {@link #DEFAULT_IMAGE_PATH} is used instead, and
     * an error message is printed to the standard error stream.
     *
     * <p>The initial position of the obstacle in the game world is set using the
     * provided {@code x} and {@code y} coordinates. A CSS style class, defined by
     * {@link #OBSTACLE_STYLE_CLASS}, is added to the {@link ImageView} node, allowing
     * for consistent styling of all obstacle types. Finally, the obstacle's initial
     * movement speed in both the horizontal and vertical directions is randomized
     * by calling the {@link #randomizeSpeed()} method, giving each obstacle a unique
     * starting trajectory.
     *
     * @param imagePath the path to the image file for the obstacle. This path should
     * be relative to the application's resources.
     * @param x         the initial x-coordinate of the obstacle in the game world.
     * @param y         the initial y-coordinate of the obstacle in the game world.
     */
    Obstacle(final String imagePath,
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
     * Returns the JavaFX {@link ImageView} node that represents the visual
     * element of this obstacle in the game scene. This node is the fundamental
     * building block for rendering the obstacle on the screen and for managing
     * its graphical properties, such as position, size, and image.
     *
     * <p>The {@link ImageView} returned by this method is directly used by the
     * game's rendering system (e.g., the JavaFX Scene Graph) to display the
     * obstacle to the player. Any transformations or visual effects applied to
     * this node will be reflected in the rendered output.
     *
     * @return the {@link ImageView} node associated with this obstacle. This node
     * is the visual representation of the obstacle in the game.
     */
    final ImageView getNode()
    {
        return node;
    }

    /*
     * Updates the obstacle's position within the game world based on its current
     * velocity components, {@code dx} (horizontal speed) and {@code dy} (vertical speed).
     * This method also implements basic collision detection with the boundaries of
     * the game area defined by the provided {@code width} and {@code height}.
     *
     * <p>If the obstacle's new horizontal position (calculated by adding {@code dx}
     * to the current x-coordinate) would cause it to move beyond the left or right
     * boundaries of the game area, the horizontal speed component {@code dx} is
     * reversed. The obstacle's x-coordinate is then clamped to ensure it remains
     * within the valid horizontal bounds. The boundaries are defined by {@link #MIN_POSITION}
     * (typically 0.0) for the left and {@code width - OBSTACLE_WIDTH} for the right.
     *
     * <p>Similarly, if the obstacle's new vertical position (calculated by adding
     * {@code dy} to the current y-coordinate) would cause it to move beyond the top
     * or bottom boundaries of the game area, the vertical speed component {@code dy}
     * is reversed. The obstacle's y-coordinate is then clamped to ensure it remains
     * within the valid vertical bounds. The boundaries are defined by {@link #MIN_POSITION}
     * for the top and {@code height - OBSTACLE_HEIGHT} for the bottom.
     *
     * <p>After handling potential boundary collisions and adjusting the position,
     * the {@link ImageView} node representing the obstacle is updated to reflect
     * the new x and y coordinates.
     *
     * @param width  the width of the game area, used to detect and resolve horizontal
     * boundary collisions.
     * @param height the height of the game area, used to detect and resolve vertical
     * boundary collisions.
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
     * Checks if this obstacle is currently colliding with the player's cursor.
     * Collision detection is performed by calculating the Euclidean distance between
     * the center of the obstacle and the center of the player's cursor. If this
     * distance is less than or equal to the sum of half the obstacle's width and
     * half the player's cursor size (approximated), a collision is considered to
     * have occurred.
     *
     * <p>The center of the obstacle is determined by adding half of its width
     * ({@link #HALF_WIDTH}) to its x-coordinate and half of its height
     * ({@link #HALF_HEIGHT}) to its y-coordinate. The center of the player's cursor
     * is obtained from the {@link Player} object.
     *
     * <p>The collision distance is calculated as the sum of {@link #HALF_WIDTH} and
     * half of the player's cursor size. The division of the cursor size by
     * {@link #SPEED_RANGE_MULTIPLIER} is an approximation and might be adjusted
     * based on desired collision sensitivity.
     *
     * @param player the {@link Player} object to check for collision with. This object
     * provides access to the cursor's current x and y coordinates and its size.
     * @return {@code true} if the obstacle is currently colliding with the player's
     * cursor, {@code false} otherwise.
     */
    final boolean collidesWith(final Player player)
    {
        final double distance;
        final double cursorCenterX;
        final double obstacleCenterX;
        final double collisionDistance;
        final double obstacleCenterY;
        final double cursorCenterY;

        obstacleCenterX = node.getX() + HALF_WIDTH;

        obstacleCenterY = node.getY() + HALF_HEIGHT;

        cursorCenterX = player.getCursorX();

        cursorCenterY = player.getCursorY();

        distance = Math.sqrt(Math.pow(obstacleCenterX - cursorCenterX,
                                      SPEED_RANGE_MULTIPLIER) + Math.pow(obstacleCenterY - cursorCenterY,
                                                                         SPEED_RANGE_MULTIPLIER));

        collisionDistance = HALF_WIDTH + (player.getCursorSize() / SPEED_RANGE_MULTIPLIER);

        return distance <= collisionDistance;
    }

    /*
     * Randomizes the speed of the obstacle in both the horizontal ({@code dx})
     * and vertical ({@code dy}) directions. The speed components are assigned
     * a random value within a range defined by {@link #SPEED_RANGE_MIN} and
     * {@link #SPEED_RANGE_MULTIPLIER}, and then scaled by the base {@link #SPEED}.
     * This ensures that each obstacle, when created, has a unique initial velocity,
     * contributing to the dynamic and unpredictable nature of the game environment.
     *
     * <p>The horizontal speed {@code dx} is calculated as a random number between
     * {@code SPEED_RANGE_MIN} (inclusive) and {@code SPEED_RANGE_MIN + SPEED_RANGE_MULTIPLIER}
     * (exclusive), multiplied by the base {@link #SPEED}. Similarly, the vertical
     * speed {@code dy} is calculated using the same random range and the base speed.
     * This can result in both positive and negative speed values, meaning obstacles
     * can move in any direction.
     */
    private void randomizeSpeed()
    {
        dx = (Math.random() * SPEED_RANGE_MULTIPLIER + SPEED_RANGE_MIN) * SPEED;
        dy = (Math.random() * SPEED_RANGE_MULTIPLIER + SPEED_RANGE_MIN) * SPEED;
    }
}