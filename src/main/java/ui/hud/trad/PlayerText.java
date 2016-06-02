package ui.hud.trad;

import data.PlayerColor;

public class PlayerText {

    public static String of(PlayerColor color) {
        switch (color) {
            case WHITE: return "Blanc";
            case RED: return "Rouge";
            case YELLOW: return "Jaune";
            case BROWN: return "Marron";
        }

        throw new IllegalStateException();
    }
}
