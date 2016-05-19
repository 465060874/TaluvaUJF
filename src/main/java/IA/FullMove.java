package IA;
import engine.action.*;

public class FullMove {

    final Placement placement;
    final Action action;
    int points;

    public FullMove(Action a, Placement p, int points) {
        this.action = a;
        this.placement = p;
        this.points = points;
    }

    public boolean equals( FullMove m){
        return this.placement.equals(m.placement) && this.action.equals(m.action);
    }
}
