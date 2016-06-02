package ui.hud;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public enum PlayerViewCorner {

    TOP_LEFT {
        @Override
        void anchor(Node node, double horizontalOffset) {
            AnchorPane.setLeftAnchor(node, horizontalOffset);
            AnchorPane.setTopAnchor(node, 0.0);
        }

        @Override
        public double imageX(double width) {
            return 0;
        }

        @Override
        public double imageY(double height) {
            return 0;
        }

        @Override
        public double arcX(double radius) {
            return -radius;
        }

        @Override
        public double arcY(double radius) {
            return -radius;
        }

        @Override
        public double hutX(int width) {
            return 100;
        }

        @Override
        public double hutY(int height) {
            return 100;
        }
    },

    TOP_RIGHT {
        @Override
        void anchor(Node node, double horizontalOffset) {
            AnchorPane.setRightAnchor(node, horizontalOffset);
            AnchorPane.setTopAnchor(node, 0.0);
        }

        @Override
        public double imageX(double width) {
            return width / 2;
        }

        @Override
        public double imageY(double height) {
            return 0;
        }

        @Override
        public double arcX(double radius) {
            return 0;
        }

        @Override
        public double arcY(double radius) {
            return -radius;
        }

        @Override
        public double hutX(int width) {
            return 0;
        }

        @Override
        public double hutY(int height) {
            return 0;
        }
    },

    BOTTOM_RIGHT {
        @Override
        void anchor(Node node, double horizontalOffset) {
            AnchorPane.setRightAnchor(node, horizontalOffset);
            AnchorPane.setBottomAnchor(node, 0.0);
        }

        @Override
        public double imageX(double width) {
            return width  / 2;
        }

        @Override
        public double imageY(double height) {
            return height / 2;
        }

        @Override
        public double arcX(double radius) {
            return 0;
        }

        @Override
        public double arcY(double radius) {
            return 0;
        }

        @Override
        public double hutX(int width) {
            return 0;
        }

        @Override
        public double hutY(int height) {
            return 0;
        }
    },

    BOTTOM_LEFT {
        @Override
        void anchor(Node node, double horizontalOffset) {
            AnchorPane.setLeftAnchor(node, horizontalOffset);
            AnchorPane.setBottomAnchor(node, 0.0);
        }

        @Override
        public double imageX(double width) {
            return 0;
        }

        @Override
        public double imageY(double height) {
            return height / 2;
        }

        @Override
        public double arcX(double radius) {
            return -radius;
        }

        @Override
        public double arcY(double radius) {
            return 0;
        }

        @Override
        public double hutX(int width) {
            return 0;
        }

        @Override
        public double hutY(int height) {
            return 0;
        }
    };

    final void anchor(Node node) {
        anchor(node, 0);
    }

    abstract void anchor(Node node, double horizontalOffset);

    abstract double imageX(double width);

    abstract double imageY(double height);

    abstract double arcX(double radius);

    abstract double arcY(double radius);

    public abstract double hutX(int width);

    public abstract double hutY(int height);


}
