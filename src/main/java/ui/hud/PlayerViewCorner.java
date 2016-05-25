package ui.hud;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.CornerRadii;

public enum PlayerViewCorner {

    TOP_LEFT {
        @Override
        void anchor(Node node, double horizontalOffset) {
            AnchorPane.setLeftAnchor(node, horizontalOffset);
            AnchorPane.setTopAnchor(node, 0.0);
        }

        @Override
        CornerRadii faceRadii() {
            return new CornerRadii(0, 100, 100, 100, true);
        }

        @Override
        CornerRadii buildingsRadii() {
            return new CornerRadii(0, 0, 20, 0, false);
        }
    },

    TOP_RIGHT {
        @Override
        void anchor(Node node, double horizontalOffset) {
            AnchorPane.setRightAnchor(node, horizontalOffset);
            AnchorPane.setTopAnchor(node, 0.0);
        }

        @Override
        CornerRadii faceRadii() {
            return new CornerRadii(100, 0, 100, 100, true);
        }

        @Override
        CornerRadii buildingsRadii() {
            return new CornerRadii(0, 0, 0, 20, false);
        }
    },

    BOTTOM_LEFT {
        @Override
        void anchor(Node node, double horizontalOffset) {
            AnchorPane.setLeftAnchor(node, horizontalOffset);
            AnchorPane.setBottomAnchor(node, 0.0);
        }

        @Override
        CornerRadii faceRadii() {
            return new CornerRadii(100, 100, 100, 0, true);
        }

        @Override
        CornerRadii buildingsRadii() {
            return new CornerRadii(0, 20, 0, 0, false);
        }
    },

    BOTTOM_RIGHT {
        @Override
        void anchor(Node node, double horizontalOffset) {
            AnchorPane.setRightAnchor(node, horizontalOffset);
            AnchorPane.setBottomAnchor(node, 0.0);
        }

        @Override
        CornerRadii faceRadii() {
            return new CornerRadii(100, 100, 0, 100, true);
        }

        @Override
        CornerRadii buildingsRadii() {
            return new CornerRadii(20, 0, 0, 0, false);
        }
    };

    final void anchor(Node node) {
        anchor(node, 0);
    }

    abstract void anchor(Node node, double horizontalOffset);


    abstract CornerRadii faceRadii();

    abstract CornerRadii buildingsRadii();
}
