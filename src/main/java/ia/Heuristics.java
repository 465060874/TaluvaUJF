package ia;

import engine.Engine;
import engine.action.*;

import java.util.Collection;

interface Heuristics {

    void chooseStrategies (Engine e , int [] StrategyValues, int BranchingFactor );

    int evaluateSeaPlacement(Engine e, SeaTileAction move);
    int evaluateVolcanoPlacement(Engine e, VolcanoTileAction move);
    int evaluateBuildAction(Engine e, TileAction tileAction, PlaceBuildingAction move, int pointsPlacement, Collection<Move>[] strategiesQueues);
    int evaluateExpandAction(Engine e, TileAction tileAction, ExpandVillageAction move, int pointsPlacement, Collection<Move>[] strategiesQueues);

    int evaluateConfiguration(Engine engine);
}
