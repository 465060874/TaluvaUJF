package IA;
import engine.*;
import engine.action.*;
import java.util.PriorityQueue;

public interface Heuristics {

    void chooseStrategies (Engine e , int [] StrategyValues, int BranchingFactor );

    int evaluateSeaPlacement(Engine e, SeaPlacement move);
    int evaluateVolcanoPlacement(Engine e, VolcanoPlacement move);
    int evaluateBuildAction(Engine e, Placement placement, BuildAction move, int pointsPlacement, PriorityQueue<FullMove>[] strategiesQueues);
    int evaluateExpandAction(Engine e, Placement placement, ExpandAction move, int pointsPlacement, PriorityQueue<FullMove>[] strategiesQueues);
}
