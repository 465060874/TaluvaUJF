package ia;

import engine.Engine;
import engine.action.*;

import java.util.Collection;
import java.util.Random;

class RandomHeuristics implements Heuristics {

    final Random r;
    int nbStrategies;

    RandomHeuristics(Random r) {
        this.r = r;
        this.nbStrategies = 4;
    }

    public void chooseStrategies(Engine e , int[] StrategyValues, int BranchingFactor) {
        int i, j;
        for( i = 0; i < nbStrategies; i++ )
            StrategyValues[i] = 0;
        i = 0; j = r.nextInt(nbStrategies);
        while( i < BranchingFactor ){
            if( j == nbStrategies )
                j = 0;
            StrategyValues[j++]++;
            i++;
        }
    }

    public int evaluateSeaPlacement(Engine e, SeaTileAction move){
        return r.nextInt(40) - 20;
    }

    public int evaluateVolcanoPlacement(Engine e, VolcanoTileAction move){
        return r.nextInt(40) - 20;
    }

    public int evaluateBuildAction(Engine e, TileAction tileAction, PlaceBuildingAction move, int pointsPlacement, Collection<Move>[] strategiesQueues){
        Move m;
        for (int i = 0; i < nbStrategies; i++) {
            m = new Move( move, tileAction, r.nextInt(40) + pointsPlacement - 20 );
            strategiesQueues[i].add(m);
        }
        return 0;
    }

    public int evaluateExpandAction(Engine e, TileAction tileAction, ExpandVillageAction move, int pointsPlacement, Collection<Move>[] strategiesQueues){
        Move m;
        for (int i = 0; i < nbStrategies; i++) {
            m = new Move( move, tileAction, r.nextInt(40) + pointsPlacement - 20 );
            strategiesQueues[i].add(m);
        }
        return 0;
    }

    @Override
    public int evaluateConfiguration(Engine engine) {
        return r.nextInt(40) - 20;
    }
}
