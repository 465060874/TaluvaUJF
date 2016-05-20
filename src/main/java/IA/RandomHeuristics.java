package IA;

import engine.Engine;
import engine.action.*;
import java.util.Random;
import java.util.PriorityQueue;

public class RandomHeuristics implements Heuristics {

    Random r;
    int nbStrategies;

    public RandomHeuristics(){
        r = new Random();
        nbStrategies = 4;
    }

    public void chooseStrategies (Engine e , int [] StrategyValues, int BranchingFactor ){
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

    public int evaluateBuildAction(Engine e, TileAction tileAction, PlaceBuildingAction move, int pointsPlacement, PriorityQueue<Move>[] strategiesQueues){
        Move m;
        for (int i = 0; i < nbStrategies; i++) {
            m = new Move( move, tileAction, r.nextInt(40) + pointsPlacement - 20 );
            strategiesQueues[i].add(m);
        }
        return 0;
    }

    public int evaluateExpandAction(Engine e, TileAction tileAction, ExpandVillageAction move, int pointsPlacement, PriorityQueue<Move>[] strategiesQueues){
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
