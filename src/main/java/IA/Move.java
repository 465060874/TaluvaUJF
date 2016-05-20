package IA;
import engine.action.*;

class Move {

    final TileAction tileAction;
    final BuildingAction buildingAction;
    int points;

    public Move(BuildingAction a, TileAction p, int points) {
        this.buildingAction = a;
        this.tileAction = p;
        this.points = points;
    }

    public boolean equals(Move m) {
        return this.tileAction.equals(m.tileAction) && this.buildingAction.equals(m.buildingAction);
    }
}
