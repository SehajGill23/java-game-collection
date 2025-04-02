package ca.bcit.Comp2522.termProject.MyGame;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Lesson 2: Abstract Classes

    public abstract class Obstacle {
        private static final double OBSTACLE_WIDTH = 20.0;
        private static final double OBSTACLE_HEIGHT = 20.0;
        private static final double SPEED = 4.0;

        protected final ImageView node;
        protected double dx;
        protected double dy;

        public Obstacle(final String imagePath, final double x, final double y) {
            Image image = null;
            try {
                image = new Image(imagePath, OBSTACLE_WIDTH, OBSTACLE_HEIGHT, true, true);
                if (image.isError()) {
                    System.err.println("Obstacle image failed to load (" + imagePath + "): " + image.getException());
                }
            } catch (Exception e) {
                System.err.println("Error loading obstacle image (" + imagePath + "): " + e.getMessage());
            }

            this.node = new ImageView(image != null && !image.isError() ? image : new Image("/default.png"));
            node.setFitWidth(OBSTACLE_WIDTH);
            node.setFitHeight(OBSTACLE_HEIGHT);
            node.setPreserveRatio(true);
            node.setX(x);
            node.setY(y);
            node.getStyleClass().add("obstacle");
            randomizeSpeed();
        }

        public final ImageView getNode() {
            return node;
        }

        public void update(final int width, final int height) {
            double x = node.getX() + dx;
            double y = node.getY() + dy;

            if (x <= 0 || x >= width - OBSTACLE_WIDTH) {
                dx = -dx;
                x = Math.max(0, Math.min(x, width - OBSTACLE_WIDTH));
            }
            if (y <= 0 || y >= height - OBSTACLE_HEIGHT) {
                dy = -dy;
                y = Math.max(0, Math.min(y, height - OBSTACLE_HEIGHT));
            }

            node.setX(x);
            node.setY(y);
        }

        public boolean collidesWith(final Player player) {
            // Simple distance-based collision: check if cursor center is close to obstacle center
            double obstacleCenterX = node.getX() + OBSTACLE_WIDTH / 2;
            double obstacleCenterY = node.getY() + OBSTACLE_HEIGHT / 2;
            double cursorCenterX = player.getCursorX();
            double cursorCenterY = player.getCursorY();
            double distance = Math.sqrt(
                    Math.pow(obstacleCenterX - cursorCenterX, 2) +
                    Math.pow(obstacleCenterY - cursorCenterY, 2)
                                       );
            // Consider collision if distance is less than the sum of half-sizes
            double collisionDistance = (OBSTACLE_WIDTH / 2) + (player.getCursorSize() / 2);
            return distance <= collisionDistance;
        }

        protected void randomizeSpeed() {
            dx = (Math.random() * 2 - 1) * SPEED;
            dy = (Math.random() * 2 - 1) * SPEED;
        }
    }



