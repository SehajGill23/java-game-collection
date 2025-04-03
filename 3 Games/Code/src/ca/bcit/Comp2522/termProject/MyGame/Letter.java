package ca.bcit.Comp2522.termProject.MyGame;

import javafx.scene.text.Text;

    public class Letter {
        private static final double SPEED = 3.0;
        private static final double SIZE = 20.0;

        private final Text node;
        private final char value;
        private double dx;
        private double dy;
        private final boolean isTarget;
        private final boolean isBonus;
        private boolean isLocked;

        public Letter(final char value, final double x, final double y, final boolean isTarget, final boolean isBonus) {
            this.value = value;
            this.node = new Text(String.valueOf(value));
            this.node.setX(x);
            this.node.setY(y);
            this.isTarget = isTarget;
            this.isBonus = isBonus;
            this.isLocked = false;
            randomizeDirection();
        }

        public final Text getNode() {
            return node;
        }

        public final char getValue() {
            return value;
        }

        public final boolean isTarget() {
            return isTarget;
        }

        public final boolean isBonus() {
            return isBonus;
        }

        public final boolean isLocked() {
            return isLocked;
        }

        public final void lock() {
            isLocked = true;
        }

        public void updatePosition(final int width, final int height) {
            if (isLocked) return;

            double x = node.getX() + dx;
            double y = node.getY() + dy;

            if (x <= 0 || x >= width - SIZE) {
                dx = -dx;
                x = Math.max(0, Math.min(x, width - SIZE));
            }
            if (y <= 0 || y >= height - SIZE) {
                dy = -dy;
                y = Math.max(0, Math.min(y, height - SIZE));
            }

            node.setX(x);
            node.setY(y);
        }

        private void randomizeDirection() {
            dx = (Math.random() * 2 - 1) * SPEED;
            dy = (Math.random() * 2 - 1) * SPEED;
        }
    }