package ui.hud;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import static ui.hud.PlayerView.*;
import static ui.hud.PlayerView.WIDTH_TURN;

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
        public double ovalX(double radius) {
            return -radius;
        }

        @Override
        public double ovalX2(double radius) {
            return -radius;
        }

        @Override
        public double ovalY(double radius) {
            return -radius;
        }

        @Override
        public double ovalY2(double radius) {
            return -radius;
        }

        @Override
        public double templeX(int width) {
            return width / 2;
        }

        @Override
        public double templeY(int height) {
            return height / 2 + (1.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double towerX(int width) {
            return width - (60.0 * width) / WIDTH_TURN;
        }

        @Override
        public double towerY(int height) {
            return (40.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double hutX(int width) {
            return (20.0 * width) / WIDTH_TURN;
        }

        @Override
        public double hutY(int height) {
            return height - (40.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double buildingCountOffsetX(double width) {
            return (20 * width) / WIDTH_TURN;
        }

        @Override
        public double buildingCountOffsetY(double height) {
            return (10 * height) / HEIGHT_TURN;
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
            return width / 2 + 20;
        }

        @Override
        public double imageY(double height) {
            return 0;
        }

        @Override
        public double ovalX(double radius) {
            return 0;
        }

        @Override
        public double ovalX2(double radius) {
            return radius;
        }

        @Override
        public double ovalY(double radius) {
            return -radius;
        }

        @Override
        public double ovalY2(double radius) {
            return -radius;
        }

        @Override
        public double templeX(int width) {
            return width / 2;
        }

        @Override
        public double templeY(int height) {
            return height / 2 + (10.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double towerX(int width) {
            return (60.0 * width) / WIDTH_TURN;
        }

        @Override
        public double towerY(int height) {
            return (40.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double hutX(int width) {
            return width - (20.0 * width) / WIDTH_TURN;
        }

        @Override
        public double hutY(int height) {
            return height - (40.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double buildingCountOffsetX(double width) {
            return -(20 * width) / WIDTH_TURN;
        }

        @Override
        public double buildingCountOffsetY(double height) {
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
            return width  / 2 + 20;
        }

        @Override
        public double imageY(double height) {
            return height / 2 + 20;
        }

        @Override
        public double ovalX(double radius) {
            return 0;
        }

        @Override
        public double ovalX2(double radius) {
            return radius;
        }

        @Override
        public double ovalY(double radius) {
            return 0;
        }

        @Override
        public double ovalY2(double radius) {
            return radius;
        }

        @Override
        public double templeX(int width) {
            return width / 2;
        }

        @Override
        public double templeY(int height) {
            return height / 2 - (5.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double towerX(int width) {
            return (60.0 * width) / WIDTH_TURN;
        }

        @Override
        public double towerY(int height) {
            return height - (20.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double hutX(int width) {
            return width - (20.0 * width) / WIDTH_TURN;
        }

        @Override
        public double hutY(int height) {
            return (40.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double buildingCountOffsetX(double width) {
            return -(20 * width) / WIDTH_TURN;
        }

        @Override
        public double buildingCountOffsetY(double height) {
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
            return height / 2 + 20;
        }

        @Override
        public double ovalX(double radius) {
            return -radius;
        }

        @Override
        public double ovalX2(double radius) {
            return -radius;
        }

        @Override
        public double ovalY(double radius) {
            return 0;
        }

        @Override
        public double ovalY2(double radius) {
            return radius;
        }

        @Override
        public double templeX(int width) {
            return width / 2;
        }

        @Override
        public double templeY(int height) {
            return height / 2 - (1.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double towerX(int width) {
            return width - (60.0 * width) / WIDTH_TURN;
        }

        @Override
        public double towerY(int height) {
            return height - (20.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double hutX(int width) {
            return (20.0 * width) / WIDTH_TURN;
        }

        @Override
        public double hutY(int height) {
            return (40.0 * height) / HEIGHT_TURN;
        }

        @Override
        public double buildingCountOffsetX(double width) {
            return (20 * width) / WIDTH_TURN;
        }

        @Override
        public double buildingCountOffsetY(double height) {
            return 0;
        }
    };

    final void anchor(Node node) {
        anchor(node, 0);
    }

    abstract void anchor(Node node, double horizontalOffset);

    abstract double imageX(double width);

    abstract double imageY(double height);

    abstract double ovalX(double radius);

    abstract double ovalX2(double radius);

    abstract double ovalY(double radius);

    abstract double ovalY2(double radius);

    public abstract double templeX(int width);

    public abstract double templeY(int height);

    public abstract double towerX(int width);

    public abstract double towerY(int height);

    public abstract double hutX(int width);

    public abstract double hutY(int height);

    public abstract double buildingCountOffsetX(double width);

    public abstract double buildingCountOffsetY(double height);
}
