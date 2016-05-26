package IA;
import com.google.common.collect.SetMultimap;
import data.BuildingType;
import data.FieldType;
import engine.*;
import engine.action.*;
import map.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

class BasicHeuristics implements Heuristics {
    // Critères d'évaluation pondérés
    int echelle = 100 ;
    int nbStrategies = 4;
    int [] pointsVillageSize;
    int pointsVillageWithTemple;

    // Critères de choix de pondération
    int mintimeFinishTowers; // Temps min après lequel le joueur décide de privilégier temples à tours si il a une tour manquante et moins de temples que l'autre

    final static int TEMPLESTRATEGY = 0;
    final static int TOWERSTRATEGY = 1;
    final static int HUTSTRATEGY = 2;
    final static int COUNTERSTRATEGY = 3;

    BasicHeuristics() {
        pointsVillageSize = new int[]{0,5,15,30,40,50};
        pointsVillageWithTemple = 100;
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
                    if( StrategyValues[COUNTERSTRATEGY] < 0 ){
                        int correction = StrategyValues[COUNTERSTRATEGY] / 2;
                        StrategyValues[TEMPLESTRATEGY] += correction;
                        StrategyValues[TOWERSTRATEGY] += ( StrategyValues[COUNTERSTRATEGY] - correction );
                        StrategyValues[COUNTERSTRATEGY] = 0;
                    }
            }else{
                if( templesPlaced < templesPlacedOpponent ){
                    StrategyValues[TEMPLESTRATEGY] += StrategyValues[TOWERSTRATEGY]/2;
                    StrategyValues[TOWERSTRATEGY] -= StrategyValues[TOWERSTRATEGY]/2;
                }
            }
        }
    }

    public int evaluateSeaPlacement(Engine e, SeaTileAction move){
        int points = 0;
        int scale = 1;
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
                    points -= bonus; // Ecraser une de ses propres villes pourrait faire perdre des points pour temple
                    if (village.getHexes().size() < 3)
                        points -= bonus; // Ecraser un petit village ( i.e l'annihiler ) est mauvais pour la strategie hutte
                }
                // Peut être tester si on peut accéder à un espace haut de 3 du haut
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
                    points += bonus;
                }
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
                    points += bonus;
                }
            }
        }
        return scale * points;
    }

    public int evaluateVolcanoPlacement(Engine e, VolcanoTileAction move){
        int points = 0;
        int scale = 2;
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
                if( !village.hasTemple())
                    points -= bonus; // Ecraser une de ses propres villes pourrait faire perdre des points pour temple
            }
        }

        // Etude des hex qu'on va écraser avec le gauche
        hex = move.getLeftHex();
        BuildingType building = island.getField(hex).getBuilding().getType();
        if( building != BuildingType.NONE ){
            if( island.getField(hex).getBuilding().getColor() == e.getCurrentPlayer().getColor())
                bonus = 2;
            else
                bonus = -2;
            Village village = island.getVillage(hex);
            if( !village.hasTemple()) {
                points -= bonus;
                // Si on réduit un village sans temple à moins de 3 hexs...
                if( village.getHexes().size() == 3 || village.getHexes().size() == 4)
                    if (island.getField(move.getRightHex()).getBuilding().getType() != BuildingType.NONE )
                        if( island.getField(move.getRightHex()).getBuilding().getColor() != e.getCurrentPlayer().getColor() )
                            points += 2;

            }
            /* CAs tour a gérer plus tard if( !village.hasTower() )
                points -= bonus; */
            points -= bonus;
        }
        // Et si on permet de s'étendre
        for (Neighbor neighbor : Neighbor.values()) {
            Hex adjacent = hex.getNeighbor(neighbor);
            building = island.getField(adjacent).getBuilding().getType();
            // On donne la possibilité de s'étendre notamment à une ville sans temple
            if( !adjacent.equals( move.getRightHex() ) && building != BuildingType.NONE  ){
                if( island.getField(adjacent).getBuilding().getColor() == e.getCurrentPlayer().getColor())
                    bonus = 2;
                else
                    bonus = -2;
                Village village = island.getVillage(adjacent);
                if( !village.hasTemple()) {
                    points += bonus;
                }
                /* if( !village.hasTower()) {
                    points += bonus * (village.getHexes().size() > 2 ? 2 : village.getHexes().size());
                    if( island.getField( hex ).getLevel() == 2)
                        points += 4*bonus;
                } */
                points += bonus;
            }
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
                points -= bonus;
                // Si on réduit un village sans temple à moins de 3 hexs...
                if( village.getHexes().size() == 3 || village.getHexes().size() == 4)
                    if (island.getField(move.getLeftHex()).getBuilding().getType() != BuildingType.NONE )
                        if( island.getField(move.getLeftHex()).getBuilding().getColor() != e.getCurrentPlayer().getColor() )
                            points += 2;

            }
            /* CAs tour a gérer plus tard if( !village.hasTower() )
                points -= bonus; */
            points -= bonus;
        }
        for (Neighbor neighbor : Neighbor.values()) {
            Hex adjacent = hex.getNeighbor(neighbor);
            building = island.getField(adjacent).getBuilding().getType();
            // On donne la possibilité de s'étendre notamment à une ville sans temple
            if( !adjacent.equals( move.getLeftHex() ) && building != BuildingType.NONE  ){
                if( island.getField(adjacent).getBuilding().getColor() == e.getCurrentPlayer().getColor())
                    bonus = 2;
                else
                    bonus = -2;
                Village village = island.getVillage(adjacent);
                if( !village.hasTemple()) {
                    points += bonus;
                }
                /* if( !village.hasTower()) {
                    points += bonus * (village.getHexes().size() > 2 ? 2 : village.getHexes().size());
                    if( island.getField( hex ).getLevel() == 2)
                        points += 4*bonus;
                } */
                points += bonus;
            }
        }
        return points*scale;
    }

    public int evaluateBuildAction(Engine e, TileAction tileAction, PlaceBuildingAction move, int pointsPlacement, PriorityQueue<Move>[] strategiesQueues){
        int temple = 0, tower = 0, huts = 0, counter = 0;
        int general = 0;
        int scale = 2;
        Hex hex = move.getHex();
        BuildingType type = move.getType();
        if( type == BuildingType.TEMPLE ){
            temple += 10;
        }else if( type == BuildingType.TOWER){
            tower += 10;
        }else{ // type == HUT
            temple += 2;
            huts += 1;
            for (Neighbor neighbor : Neighbor.values()) {
                Hex adjacent = hex.getNeighbor(neighbor);
                if( e.getIsland().getField( adjacent).getLevel() >= 3 && e.getIsland().getField( adjacent).getBuilding().getType() == BuildingType.NONE )
                    tower += 5;
            }
        }
        for (Neighbor neighbor : Neighbor.values()) {
            Hex adjacent = hex.getNeighbor(neighbor);
            Building building = e.getIsland().getField(adjacent).getBuilding();
            if( building.getType() != BuildingType.NONE && building.getColor() != e.getCurrentPlayer().getColor() ) {
                if( !e.getIsland().getVillage(adjacent).hasTemple()) {
                    general += 1;
                    counter += 2;
                }
            }
        }
        strategiesQueues[TEMPLESTRATEGY].add( new Move( move, tileAction, scale*(temple + general) ));
        strategiesQueues[TOWERSTRATEGY].add( new Move( move, tileAction, scale*(tower + general) ));
        strategiesQueues[HUTSTRATEGY].add( new Move( move, tileAction, scale*(huts + general) ));
        strategiesQueues[COUNTERSTRATEGY].add( new Move( move, tileAction, scale*(counter + general) ));
        return 0;
    }

    public int evaluateExpandAction(Engine e, TileAction tileAction, ExpandVillageAction move, int pointsPlacement, PriorityQueue<Move>[] strategiesQueues){
        int temple = 0, tower = 0, huts = 0, counter = 0;
        int general = 0;
        int scale = 2;
        int newHexes = 0;
        Village village = move.getVillage(e.getIsland());
        FieldType field = move.getFieldType();
        SetMultimap<FieldType, Hex> m = village.getExpandableHexes();
        for( Hex hex : m.get(field) ){
            for (Neighbor neighbor : Neighbor.values()) {
                Hex adjacent = hex.getNeighbor(neighbor);
                // COnstruire a la place de l'autre
                if( e.getIsland().getField(adjacent).getBuilding().getType() != BuildingType.NONE
                        && e.getIsland().getField(adjacent).getBuilding().getColor() != e.getCurrentPlayer().getColor() )
                        if( ! e.getIsland().getVillage(adjacent).hasTemple()) {
                            general += 1;
                            counter += 2;
                        }
                // Construire à côté d'une hauteur 3 sans tour
               if( !village.hasTower() && e.getIsland().getField(adjacent).getBuilding().getType() == BuildingType.NONE)
                    if( e.getIsland().getField( adjacent ).getLevel() >= 3 )
                        tower += 5;
            }
            // Nombre de huttes rajoutées
            huts += e.getIsland().getField(hex).getLevel();
            temple += 2;
            newHexes++;
        }
        // Si on a agrandi sa cité de plus de trois pour construire un temps
        if( newHexes + village.getHexes().size() >= 3 && village.getHexes().size() < 3 && !village.hasTemple())
            temple += 5;

        strategiesQueues[TEMPLESTRATEGY].add( new Move( move, tileAction, scale*(temple + general) ));
        strategiesQueues[TOWERSTRATEGY].add( new Move( move, tileAction, scale*(tower + general) ));
        strategiesQueues[HUTSTRATEGY].add( new Move( move, tileAction, scale*(huts + general) ));
        strategiesQueues[COUNTERSTRATEGY].add( new Move( move, tileAction, scale*(counter + general) ));
        return 0;
    }

    @Override
    public int evaluateConfiguration(Engine engine) {
        int score = 0, tmpScore;
        int bonus;
        int nbVillages = 0, nbVIllagesOpponent = 0;
        List<Player> players = engine.getPlayers();
        Player player = engine.getCurrentPlayer();

        /*Set<Village> villages = new HashSet<>();
        for (Hex hex : engine.getIsland().getFields()) {
            if( engine.getIsland().getField(hex).getBuilding().getType() != BuildingType.NONE )
                villages.add(engine.getIsland().getVillage(hex));
        }*/

        Iterable<Village> villages = engine.getIsland().getVillages( player.getColor());
        for( Village village : villages) {
            tmpScore = 0;
            if( !village.hasTemple())
                tmpScore += village.getHexes().size() > 5 ? pointsVillageSize[5] : pointsVillageSize[village.getHexes().size()];
            else
                tmpScore += pointsVillageWithTemple;
            engine.logger().finer("[Eval] {0} Village : {1} ", player.getColor(), tmpScore);
            score += tmpScore;
        }
        int i = 0;
        while( (player = players.get(i++)) == engine.getCurrentPlayer() ){}
        villages = engine.getIsland().getVillages( player.getColor());
        for( Village village : villages) {
            tmpScore = 0;
            if( !village.hasTemple())
                tmpScore += village.getHexes().size() > 5 ? pointsVillageSize[5] : pointsVillageSize[village.getHexes().size()];
            else
                tmpScore += pointsVillageWithTemple;
            engine.logger().finer("[Eval] {0} Village : {1} ", player.getColor(), tmpScore);
            score -= tmpScore;
        }
        engine.logger().fine("[Eval] {0} Config with {1} points ", engine.getCurrentPlayer().getColor(), score);
        return score;
    }
}