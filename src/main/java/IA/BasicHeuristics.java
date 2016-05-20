package IA;
import data.BuildingType;
import engine.*;
import engine.action.*;
import map.Hex;
import map.Island;
import map.Neighbor;
import map.Village;

import java.util.List;
import java.util.Random;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class BasicHeuristics implements Heuristics {
    // Critères d'évaluation pondérés
    int echelle = 100 ;
    int nbStrategies = 4;
    int templePlacedPts, towerPlacedPts, hutPlacedPts;
    int templeGapPts, towerGapPts, hutGapPts;
    int templeTimeOut, towerTimeOut;
    int notEnoughHutsForTemple, notEnoughHutsForTower;
    int templeFreeCity, towerFreeCity; // Citée dans laquelle on peut construire un temple ( 3+ huttes ) ou une tour ( niveau 3+ adjacent ).

    // Critères de choix de pondération
    int mintimeFinishTowers; // Temps min après lequel le joueur décide de privilégier temples à tours si il a une tour manquante et moins de temples que l'autre

    final static int TEMPLESTRATEGY = 0;
    final static int TOWERSTRATEGY = 1;
    final static int HUTSTRATEGY = 2;
    final static int COUNTERSTRATEGY = 3;

    Random r;

    public BasicHeuristics(){
        r = new Random();
    }

    public void chooseStrategies (Engine e , int [] StrategyValues, int BranchingFactor ){
        Player ia, opponent;
        List<Player> playersList;
        // 1 -- Standard repartition
        StrategyValues[TEMPLESTRATEGY] = 3*BranchingFactor/8;
        StrategyValues[TOWERSTRATEGY] = 3*BranchingFactor/8;
        StrategyValues[HUTSTRATEGY] = 0;
        StrategyValues[COUNTERSTRATEGY] = BranchingFactor/4;

        // 2 -- Affinage
        int i;
        int pas = BranchingFactor/8;
        // On récuppère les deux joueurs
        ia = e.getCurrentPlayer();
        playersList = e.getPlayers();
        opponent = playersList.get(0);
        i = 1;
        while( opponent.getColor() == ia.getColor() )
            opponent = playersList.get(i++);
        int turnsLeft = e.getVolcanoTileStack().size() / 2;
        // Comparaison des constructions
        int templesPlaced = ia.getBuildingCount(BuildingType.TEMPLE), towersPlaced = ia.getBuildingCount(BuildingType.TOWER),
                hutsPlaced = ia.getBuildingCount(BuildingType.HUT);
        int templesPlacedOpponent = opponent.getBuildingCount(BuildingType.TEMPLE), towersPlacedOpponent = opponent.getBuildingCount(BuildingType.TOWER),
                hutsPlacedOpponent = opponent.getBuildingCount(BuildingType.HUT);
        // Un type a été terminé
        if( templesPlaced == 3 ) {
            // Choix entre finir les huttes et finir les tours
            StrategyValues[HUTSTRATEGY] = StrategyValues[TEMPLESTRATEGY];
            StrategyValues[TEMPLESTRATEGY] = 0;
            // On regarde les tours
            if( towersPlaced == 1 && turnsLeft >= 2 ){
                int ecart = StrategyValues[HUTSTRATEGY] - StrategyValues[HUTSTRATEGY]/2;
                StrategyValues[HUTSTRATEGY] /= 2;
                StrategyValues[TOWERSTRATEGY] += ecart;
            }else if( towersPlaced == 0 && (20-hutsPlaced)/turnsLeft <= 2 ){
                int ecart = StrategyValues[TOWERSTRATEGY] - StrategyValues[TOWERSTRATEGY]/2;
                StrategyValues[TOWERSTRATEGY] /= 2;
                StrategyValues[HUTSTRATEGY] += ecart;
            }
        }else if (towersPlaced == 2) {
            // Choix entre finir les temples et finir les tours
            StrategyValues[HUTSTRATEGY] = StrategyValues[TOWERSTRATEGY];
            StrategyValues[TOWERSTRATEGY] = 0;
            // On regarde les temples
            if (turnsLeft / (3 - templesPlaced) <= 3) {
                int ecart = StrategyValues[TEMPLESTRATEGY] - StrategyValues[TEMPLESTRATEGY] / 2;
                StrategyValues[TEMPLESTRATEGY] /= 2;
                StrategyValues[HUTSTRATEGY] += ecart;
            } else {
                int ecart = StrategyValues[HUTSTRATEGY] - StrategyValues[HUTSTRATEGY] / 2;
                StrategyValues[HUTSTRATEGY] /= 2;
                StrategyValues[TEMPLESTRATEGY] += ecart;
            }
        }else {
            // Sinon soit on est loin de la fin de partie
            if( turnsLeft >= 3 ){
                    StrategyValues[TEMPLESTRATEGY] += 2*pas*templesPlaced - pas*towersPlaced;
                    StrategyValues[TOWERSTRATEGY] += -pas*templesPlaced + 2*pas*towersPlaced;
                    StrategyValues[COUNTERSTRATEGY] += -pas*templesPlaced;
            }else{
                if( templesPlaced < templesPlacedOpponent ){
                    StrategyValues[TEMPLESTRATEGY] += StrategyValues[TOWERSTRATEGY]/2;
                    StrategyValues[TOWERSTRATEGY] -= StrategyValues[TOWERSTRATEGY]/2;
                }
            }
        }
    }

    public int evaluateSeaPlacement(Engine e, SeaPlacement move){
        int tower = 0, temple = 0, hut = 0, counter = 0;
        Hex hex = move.getHex1();
        Island island = e.getIsland();
        for (Neighbor neighbor : Neighbor.values()) {
            Hex adjacent = hex.getNeighbor(neighbor);
            BuildingType building = island.getField(adjacent).getBuilding().getType();
            if( building == BuildingType.HUT && true){ // REGARDER LE NIVEAU
                Village village = island.getVillage(adjacent);
                if( !village.hasTemple()){
                    // babla
                }
            }
        }
        return 0;
    }

    public int evaluateVolcanoPlacement(Engine e, VolcanoPlacement move){
        return r.nextInt(40) - 20;
    }

    public int evaluateBuildAction(Engine e, Placement placement, BuildAction move, int pointsPlacement, PriorityQueue<FullMove>[] strategiesQueues){
        FullMove m;
        for (int i = 0; i < nbStrategies; i++) {
            m = new FullMove( move, placement, r.nextInt(40) + pointsPlacement - 20 );
            strategiesQueues[i].add(m);
        }
        return 0;
    }

    public int evaluateExpandAction(Engine e, Placement placement, ExpandAction move, int pointsPlacement, PriorityQueue<FullMove>[] strategiesQueues){
        FullMove m;
        for (int i = 0; i < nbStrategies; i++) {
            m = new FullMove( move, placement, r.nextInt(40) + pointsPlacement - 20 );
            strategiesQueues[i].add(m);
        }
        return 0;
    }

    @Override
    public int evaluateConfiguration(Engine engine) {
        return 0;
    }
}