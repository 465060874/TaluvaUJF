package ia;
import engine.*;
import engine.action.*;
import java.util.PriorityQueue;

interface Heuristics {

    void chooseStrategies (Engine e , int [] StrategyValues, int BranchingFactor );

    int evaluateSeaPlacement(Engine e, SeaTileAction move);
    int evaluateVolcanoPlacement(Engine e, VolcanoTileAction move);
    int evaluateBuildAction(Engine e, TileAction tileAction, PlaceBuildingAction move, int pointsPlacement, PriorityQueue<Move>[] strategiesQueues);
    int evaluateExpandAction(Engine e, TileAction tileAction, ExpandVillageAction move, int pointsPlacement, PriorityQueue<Move>[] strategiesQueues);

    int evaluateConfiguration(Engine engine);
}
