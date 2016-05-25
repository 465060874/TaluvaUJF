package ui.hud;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.CornerRadii;

public enum PlayerViewCorner {

    TOP_LEFT {
        @Override
        CornerRadii faceRadii() {
            return new CornerRadii(0, 100, 100, 100, true);
        }

        @Override
        void anchor(Node node) {
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setTopAnchor(node, 0.0);
        }
    },

    TOP_RIGHT {
        @Override
        CornerRadii faceRadii() {
            return new CornerRadii(100, 0, 100, 100, true);
        }

        @Override
        void anchor(Node node) {
            AnchorPane.setRightAnchor(node, 0.0);
            AnchorPane.setTopAnchor(node, 0.0);
        }
    },

    BOTTOM_LEFT {
        @Override
        CornerRadii faceRadii() {
            return new CornerRadii(100, 100, 100, 0, true);
        }

        @Override
        void anchor(Node node) {
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
        }
    },

    BOTTOM_RIGHT {
        @Override
        CornerRadii faceRadii() {
            return new CornerRadii(100, 100, 0, 100, true);
        }

        @Override
        void anchor(Node node) {
            AnchorPane.setRightAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
        }
    };

    abstract CornerRadii faceRadii();

    abstract void anchor(Node node);
}
