package IA;
import data.BuildingType;
import engine.*;
import engine.action.*;
import map.*;

import java.util.List;
import java.util.Random;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

class BasicHeuristics implements Heuristics {
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

    BasicHeuristics() {

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
        int templesPlaced = 3 - ia.getBuildingCount(BuildingType.TEMPLE), towersPlaced = 2 - ia.getBuildingCount(BuildingType.TOWER),
                hutsPlaced = 20 - ia.getBuildingCount(BuildingType.HUT);
        int templesPlacedOpponent = 3 - opponent.getBuildingCount(BuildingType.TEMPLE), towersPlacedOpponent = 2 - opponent.getBuildingCount(BuildingType.TOWER),
                hutsPlacedOpponent = 20 - opponent.getBuildingCount(BuildingType.HUT);
        // Un type a été terminé
        if( templesPlaced == 3 ) {
            // Choix entre finir les huttes et finir les tours
            StrategyValues[HUTSTRATEGY] = StrategyValues[TEMPLESTRATEGY];
            StrategyValues[TEMPLESTRATEGY] = 0;
            // On regarde les tours
            if( towersPlaced == 1 && turnsLeft >= 2 ){
                int ecart = StrategyValues[HUTSTRATEGY] - StrategyValues[HUTSTRATEGY]/2;
                StrategyValues[HUTSTRATEGY] -= ecart;
                StrategyValues[TOWERSTRATEGY] += ecart;
            }else if( towersPlaced == 0 && (20-hutsPlaced)/turnsLeft <= 2 ){
                int ecart = StrategyValues[TOWERSTRATEGY] - StrategyValues[TOWERSTRATEGY]/2;
                StrategyValues[TOWERSTRATEGY] -= ecart;
                StrategyValues[HUTSTRATEGY] += ecart;
            }
        }else if (towersPlaced == 2) {
            // Choix entre finir les temples et finir les tours
            StrategyValues[HUTSTRATEGY] = StrategyValues[TOWERSTRATEGY];
            StrategyValues[TOWERSTRATEGY] = 0;
            // On regarde les temples
            if (turnsLeft / (3 - templesPlaced) <= 3) {
                int ecart = StrategyValues[TEMPLESTRATEGY] - StrategyValues[TEMPLESTRATEGY] / 2;
                StrategyValues[TEMPLESTRATEGY] -= ecart;
                StrategyValues[HUTSTRATEGY] += ecart;
            } else {
                int ecart = StrategyValues[HUTSTRATEGY] - StrategyValues[HUTSTRATEGY] / 2;
                StrategyValues[HUTSTRATEGY] -= ecart;
                StrategyValues[TEMPLESTRATEGY] += ecart;
            }
        }else {
            // Sinon soit on est loin de la fin de partie
            if( turnsLeft >= 3 ){
                    StrategyValues[TEMPLESTRATEGY] += 2*pas*templesPlaced - pas*towersPlaced;
                    StrategyValues[TOWERSTRATEGY] += -pas*templesPlaced + 2*pas*towersPlaced;
                    StrategyValues[COUNTERSTRATEGY] += -pas*templesPlaced -pas*towersPlaced;
            }else{
                if( templesPlaced < templesPlacedOpponent ){
                    StrategyValues[TEMPLESTRATEGY] += StrategyValues[TOWERSTRATEGY]/2;
                    StrategyValues[TOWERSTRATEGY] -= StrategyValues[TOWERSTRATEGY]/2;
                }
            }
        }
    }

    public int evaluateSeaPlacement(Engine e, SeaTileAction move){
        int tower = 0, temple = 0, hut = 0, counter = 0;
        int scale = 2;
        Island island = e.getIsland();
        Hex hex = move.getVolcanoHex();
        int bonus;
        for (Neighbor neighbor : Neighbor.values()) {
            Hex adjacent = hex.getNeighbor(neighbor);
            BuildingType building = island.getField(adjacent).getBuilding().getType();
            // Si on place le volcan a côté de huttes -> possibilité de les écraser plus tard
            if( building == BuildingType.HUT && island.getField(adjacent).getLevel() == 1){
                if( island.getField(adjacent).getBuilding().getColor() == e.getCurrentPlayer().getColor())
                    bonus = 1;
                else
                    bonus = -1;
                Village village = island.getVillage(adjacent);
                if( !village.hasTemple()) {
                    temple -= bonus; // Ecraser une de ses propres villes pourrait faire perdre des points pour temple
                    if (village.getHexes().size() < 3)
                        hut -= 2*bonus; // Ecraser un petit village ( i.e l'annihiler ) est mauvais pour la strategie hutte
                }
                // Peut être tester si on peut accéder à un espace haut de 3 du haut
                counter -= bonus;
            }
        }

        hex = move.getLeftHex();
        for (Neighbor neighbor : Neighbor.values()) {
            Hex adjacent = hex.getNeighbor(neighbor);
            BuildingType building = island.getField(adjacent).getBuilding().getType();
            if( building != BuildingType.NONE ){
                if( island.getField(adjacent).getBuilding().getColor() == e.getCurrentPlayer().getColor())
                    bonus = 1;
                else
                    bonus = -1;
                Village village = island.getVillage(adjacent);
                if( !village.hasTemple()) {
                    temple += bonus*( village.getHexes().size() > 3 ? 3 : village.getHexes().size());
                    hut += 2*bonus;
                }
                counter += bonus;
            }
        }

        hex = move.getRightHex();
        for (Neighbor neighbor : Neighbor.values()) {
            Hex adjacent = hex.getNeighbor(neighbor);
            BuildingType building = island.getField(adjacent).getBuilding().getType();
            if( building != BuildingType.NONE ){
                if( island.getField(adjacent).getBuilding().getColor() == e.getCurrentPlayer().getColor())
                    bonus = 1;
                else
                    bonus = -1;
                Village village = island.getVillage(adjacent);
                if( !village.hasTemple()) {
                    temple += bonus*( village.getHexes().size() > 3 ? 3 : village.getHexes().size());
                    hut += 2*bonus;
                }
                counter += bonus;
            }
        }
        return scale * (temple + tower + hut + counter );
    }

    public int evaluateVolcanoPlacement(Engine e, VolcanoTileAction move){
        int tower = 0, temple = 0, hut = 0, counter = 0;
        int scale = 1;
        int res = 0;
        Island island = e.getIsland();
        Hex hex = move.getVolcanoHex();
        int bonus;
        for (Neighbor neighbor : Neighbor.values()) {
            Hex adjacent = hex.getNeighbor(neighbor);
            BuildingType building = island.getField(adjacent).getBuilding().getType();
            // Si on place le volcan a côté de huttes -> possibilité de les écraser plus tard
            if( building == BuildingType.HUT && island.getField(adjacent).getLevel() == island.getField(hex).getLevel() + 1 ){
                if( island.getField(adjacent).getBuilding().getColor() == e.getCurrentPlayer().getColor())
                    bonus = 1;
                else
                    bonus = -1;
                Village village = island.getVillage(adjacent);
                if( !village.hasTemple()) {
                    temple -= bonus; // Ecraser une de ses propres villes pourrait faire perdre des points pour temple
                    if (village.getHexes().size() < 3)
                        hut -= 2*bonus; // Ecraser un petit village ( i.e l'annihiler ) est mauvais pour la strategie hutte
                }
                // Peut être tester si on peut accéder à un espace haut de 3 du haut
                counter -= bonus;
            }
        }

        // Etude des hex qu'on va écraser
        hex = move.getLeftHex();
        BuildingType building = island.getField(hex).getBuilding().getType();
        if( building != BuildingType.NONE ){
            if( island.getField(hex).getBuilding().getColor() == e.getCurrentPlayer().getColor())
                bonus = 2;
            else
                bonus = -2;
            Village village = island.getVillage(hex);
            if( !village.hasTemple()) {
                temple -= bonus*( village.getHexes().size() > 3 ? 3 : village.getHexes().size());
            }
            if( !village.hasTower())
                tower -= bonus*( village.getHexes().size() > 2 ? 2 : village.getHexes().size());
            counter -= 2*bonus;
            res -= scale*bonus;
        }


        hex = move.getRightHex();
        building = island.getField(hex).getBuilding().getType();
        if( building != BuildingType.NONE ){
            if( island.getField(hex).getBuilding().getColor() == e.getCurrentPlayer().getColor())
                bonus = 2;
            else
                bonus = -2;
            Village village = island.getVillage(hex);
            if( !village.hasTemple()) {
                temple -= bonus*( village.getHexes().size() > 3 ? 3 : village.getHexes().size());
            }
            if( !village.hasTower())
                tower -= bonus*( village.getHexes().size() > 2 ? 2 : village.getHexes().size());
            counter -= 2*bonus;
            res -= scale*bonus;
        }
        return res*40;
    }

    public int evaluateBuildAction(Engine e, TileAction tileAction, PlaceBuildingAction move, int pointsPlacement, PriorityQueue<Move>[] strategiesQueues){
        Move m;
        Random r = e.getRandom();
        long t = System.nanoTime() + TimeUnit.MICROSECONDS.toNanos(50);
        while (System.nanoTime() < t) { }
        for (int i = 0; i < nbStrategies; i++) {
            m = new Move( move, tileAction, pointsPlacement);
            strategiesQueues[i].add(m);
        }
        return 0;
    }

    public int evaluateExpandAction(Engine e, TileAction tileAction, ExpandVillageAction move, int pointsPlacement, PriorityQueue<Move>[] strategiesQueues){
        Move m;
        Random r = e.getRandom();
        long t = System.nanoTime() + TimeUnit.MICROSECONDS.toNanos(50);
        while (System.nanoTime() < t) { }
        for (int i = 0; i < nbStrategies; i++) {
            m = new Move( move, tileAction, pointsPlacement );
            strategiesQueues[i].add(m);
        }
        return 0;
    }

    @Override
    public int evaluateConfiguration(Engine engine) {
        return 0;
    }
}