package IA;
import engine.action.*;

public class FullMove {

    final Placement placement;
    final Action action;
    int points;

    public FullMove(Action a, Placement p) {
        this.action = a;
        this.placement = p;
    }
}
