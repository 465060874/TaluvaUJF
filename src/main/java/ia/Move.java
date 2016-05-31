package ia;

import engine.action.*;

import java.util.Objects;

class Move {

    final TileAction tileAction;
    final BuildingAction buildingAction;
    int points;

    public Move(BuildingAction a, TileAction p, int points) {
        this.buildingAction = a;
        this.tileAction = p;
        this.points = points;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Move)) {
            return false;
        }

        Move other = (Move) obj;
        return Objects.equals(this.tileAction, other.tileAction)
                && Objects.equals(this.buildingAction, other.buildingAction);
    }
}
