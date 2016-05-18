package IA;
import engine.*;
import engine.action.*;

import java.util.PriorityQueue;

public interface Heuristics {
    void chooseStrategies (Engine e , int [] StrategyValues, int BranchingFactor );
    int evaluateSeaPlacement(Engine e, SeaPlacement move );
    int evaluateVolcanoPlacement(Engine e, VolcanoPlacement move );
    int evaluateBuildAction(Engine e, BuildAction move, Placement placement, int pointsPlacement, PriorityQueue<FullMove> [] strategiesQueues  );
    int evaluateExpandAction(Engine e, ExpandAction move, Placement placement, int pointsPlacement, PriorityQueue<FullMove> [] strategiesQueues  );
}
