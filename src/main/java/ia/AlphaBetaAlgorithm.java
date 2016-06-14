package ia;

import com.google.common.collect.ImmutableList;
import data.BuildingType;
import data.VolcanoTile;
import engine.Engine;
import engine.EngineStatus;
import engine.action.*;
import engine.rules.PlaceBuildingRules;
import map.Hex;
import map.Village;
import util.Randoms;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by milanovb on 31/05/16.
 */
class AlphaBetaAlgorithm implements IAAlgorithm {

    // Strategies possibles pour l'IA
    private static final int NB_STRATEGIES = 4;

    // Facteur de branchment pour l'arbre MIN-MAX
    private final int branchingFactor;
    // Profondeur de la descente recursive dans l'arbre MIN-MAX
    private final int totalDepth;
    // Heuristics utilisées
    private final Heuristics heuristics;

    // Donnees
    private final Engine engine;
    private final AtomicBoolean cancelled;
    private final int[] strategyPoints = new int[NB_STRATEGIES];

    // Constructeur
    AlphaBetaAlgorithm(int branchingFactor, int totalDepth, Heuristics heuristics, Engine engine, AtomicBoolean cancelled) {
        this.branchingFactor = branchingFactor;
        this.totalDepth = totalDepth;
        this.heuristics = heuristics;

        this.engine = engine;
        this.cancelled = cancelled;
    }

    // Jouer un coup
    @Override
    public Move play() {
        Move m =  engine.getStatus().getTurn() == 0
                ? doFirstPlay()
                : doPlay(totalDepth, Integer.MAX_VALUE, AlphaBetaIntervalles.INF);
        engine.logger().fine("[IA] Choosed move with {0} points ", m.points);
        return m;
    }

    private Move doFirstPlay() {
        TileAction seaPlacement = engine.getSeaTileActions().get(0);
        engine.action(seaPlacement);
        BuildingAction buildAction = engine.getPlaceBuildingActions().get(engine.getRandom().nextInt(2));
        return new Move(buildAction, seaPlacement, 0);
    }


    private Move doPlay(int depth, int alpha, AlphaBetaIntervalles type) {
        engine.logger().fine("PLAY : depth {0}", depth);

        // 0 -- Test d'un gagnant ou perdant
        // Test effectué au début du coup : si le joueur appartient à la liste des gagnants
        if( engine.getStatus() instanceof EngineStatus.Finished ){
            throw new IllegalStateException("Call to doPlay() but game not running anymore");
        }

        // 1 -- Determiner le poids de chaque stratégie
        heuristics.chooseStrategies(engine,strategyPoints,branchingFactor);
        engine.logger().fine("-> Strategy Chosen\n" +
                        "\tTemples {0}\n\tTours {1}\n\tHuttes {2}\n\tContre {3}",
                strategyPoints[0], strategyPoints[1], strategyPoints[2], strategyPoints[3]);

        // 2 -- Determiner un sous-ensemble pertinent de coups possibles
        Move[] branchMoves = new Move[branchingFactor];
        int branchNb = branchSortFusion(engine, strategyPoints, branchMoves );
        // Test du cas où aucun coup n'est jouable !!
        if( branchMoves[0] == null) {
            // Si l'on atteint ce point, c'est que le joueur courant ne peut plus placer de batiment,
            // on choisit donc un placement de tuile au hasard
            TileAction tileAction = Randoms.pickRandom(engine.getRandom(),
                    ImmutableList.of(engine.getSeaTileActions(), engine.getVolcanoTileActions()));
            return new Move(null, tileAction, Integer.MAX_VALUE);
        }

        engine.logger().fine("-> Branch Chosen");

        // 3 -- ALPHA-BETA sur l'arbre réduit
        // A ce point : strategiesQueues contient les coups possibles pour chaque stratégie.
        // On va donc appeler la fonction de jeu pour l'adversaire dessus...
        // Et de choisir le meilleur choix
        Move bestMove = null;
        int bestPoints = Integer.MAX_VALUE;
        int bestConfigPoints = Integer.MAX_VALUE;
        int p;

        for (int i = 0; i < branchNb; i++) {
            if (i > 0 && branchMoves[i].tileAction == null || branchMoves[i].buildingAction == null)
                throw new IllegalStateException("Coup mal recombiné -> un des champs est null");

            engine.action(branchMoves[i].tileAction);
            engine.action(branchMoves[i].buildingAction);

            if( engine.getStatus() instanceof EngineStatus.Finished ){
                // On evalue la config de l'adversaire donc on prend sa moins bonne configuration !!!
                if( (p = heuristics.evaluateConfiguration(engine)) < bestConfigPoints ){
                    bestMove = new Move(branchMoves[i].buildingAction, branchMoves[i].tileAction, p);
                    bestConfigPoints = p;
                    bestPoints = p;
                }
            }
            else if (depth > 0) {
                Move m = doPlay(depth - 1, -1*alpha, type.inverse());
                // Pour inverser entre min et max
                m.points *= -1;
                if (m.points < bestPoints) {
                    bestPoints = m.points;
                    bestMove = new Move( branchMoves[i].buildingAction, branchMoves[i].tileAction, bestPoints );
                    if( type == AlphaBetaIntervalles.SUP )
                        if( bestPoints <= alpha ) {
                            engine.logger().fine("[AB] Alpha cut");
                            engine.cancelLastStep();
                            engine.cancelLastStep();
                            return bestMove;
                        }
                        else
                            alpha = bestPoints;
                    else if( type == AlphaBetaIntervalles.INF )
                        if( bestPoints >= alpha ){
                            engine.logger().fine("[AB] Alpha cut");
                            engine.cancelLastStep();
                            engine.cancelLastStep();
                            return bestMove;
                        }
                        else
                            alpha = bestPoints;
                    engine.logger().fine("[Choice] Found opponent best choice only : {0}", m.points);
                }
            }else{
                // On evalue la config de l'adversaire donc on prend sa moins bonne configuration !!!
                if( (p = heuristics.evaluateConfiguration(engine)) < bestConfigPoints ){
                    bestMove = new Move(branchMoves[i].buildingAction, branchMoves[i].tileAction, p);
                    bestConfigPoints = p;
                    if( type == AlphaBetaIntervalles.INF && p < alpha )
                        alpha = p;
                    else if( type == AlphaBetaIntervalles.SUP && p > alpha )
                        alpha = p;
                }
            }

            engine.cancelLastStep();
            engine.cancelLastStep();
        }
        if( depth > 0 )
            engine.logger().fine("[Choice] Best move choosen, opponent best branch was {0}", bestMove.points);
        return bestMove;
    }

    // Retourne le nombre de coups ajoutes dans le tableau branchMoves
    // Remplit ce tableau avec les coups qui semblent les meilleurs
    private int branchSortFusion( Engine engine, int [] strategyPoints, Move [] branchMoves) {
        int comp = 0;
        // Donnees pour classer les differents coups ( placements, constructions, move complet ... )
        PriorityQueue<Move> placements = new PriorityQueue<Move>((a,b) -> Integer.compare( b.points, a.points));
        PriorityQueue<Move>[] building = new PriorityQueue[NB_STRATEGIES];
        PriorityQueue<Move>[] moves = new PriorityQueue[NB_STRATEGIES];

        for (int i = 0; i < NB_STRATEGIES; i++) {
            building[i] = new PriorityQueue<Move>((a, b) -> Integer.compare(b.points, a.points));
            moves[i] =  new PriorityQueue<Move>((a, b) -> Integer.compare(b.points, a.points));
        }

        // Evaluation des seaPlacements + moves entiers a la volee
        engine.logger().finer("[Sort] Begin seaPlacements : {0}", engine.getSeaTileActions().size());
        for (SeaTileAction tileAction : engine.getSeaTileActions()) {
            int points = heuristics.evaluateSeaPlacement(engine, tileAction);
            // Ajout du placement seul
            placements.add( new Move(null, tileAction, points));
            engine.placeOnSea(tileAction);
            comp++;
            // Pour chaque construction et extension correlee
            for (PlaceBuildingAction action : engine.getNewPlaceBuildingActions(tileAction)) {
                heuristics.evaluateBuildAction(engine, tileAction, action, points, moves);
                comp++;
            }
            for (ExpandVillageAction action : engine.getNewExpandVillageActions(tileAction)) {
                heuristics.evaluateExpandAction(engine, tileAction, action, points, moves);
                comp++;
            }
            engine.cancelLastStep();
        }

        // Evaluation des placements sur la terre + moves entiers a la volee
        engine.logger().finer("[Sort] Begin volcanoPlacements : {0}", engine.getVolcanoTileActions().size());
        for (VolcanoTileAction tileAction : engine.getVolcanoTileActions()) {
            int points = heuristics.evaluateVolcanoPlacement(engine, tileAction);
            placements.add( new Move(null, tileAction, points));
            engine.placeOnVolcano(tileAction);
            comp++;

            for (PlaceBuildingAction buildingaction : engine.getNewPlaceBuildingActions(tileAction)) {
                heuristics.evaluateBuildAction(engine, tileAction, buildingaction, points, moves);
                comp++;
            }
            for (ExpandVillageAction action : engine.getNewExpandVillageActions(tileAction)) {
                heuristics.evaluateExpandAction(engine, tileAction, action, points, moves);
                comp++;
            }
            engine.cancelLastStep();
        }

        // Evaluation des constructions seules
        for (PlaceBuildingAction action : engine.getPlaceBuildingActions()) {
            heuristics.evaluateBuildAction(engine, null, action, 0, building);
            comp++;
        }

        for (ExpandVillageAction action : engine.getExpandVillageActions()) {
            heuristics.evaluateExpandAction(engine, null, action, 0, building);
            comp++;
        }

        // Fusion :
        // On choisit les meilleurs coups
        int ind = 0;
        for(int i = 0; i < NB_STRATEGIES; i++ )
            ind += combine(engine, placements, building[i], branchMoves, ind, strategyPoints[i], moves[i]);

        engine.logger().fine("[Sort] {0} evaluations", comp);
        return ind;
    }

    // Ajoute à partir de l'indice ind dans branchMoves[] nb moves les meilleurs en combinant les placements <placements> et les constructions <building> et les coups entiers <moves>
    // Renvoie le nombre de coups ajoutes
    private int combine( Engine engine, PriorityQueue<Move> placements, PriorityQueue<Move> building, Move[] branchMoves, int ind, int nb, PriorityQueue<Move> moves){
        Move place, build, p = null, b = null;
        int added = 0;
        Iterator<Move> pi, bi;
        place = placements.peek();
        build = building.peek();
        pi = placements.iterator();
        bi = building.iterator();
        // Cas ou aucun placement ou aucune construction n'est possible
        if( !(bi.hasNext() && pi.hasNext()) ) {
            if (moves.size() == 0)
                return 0;
            place = null;
            build = null;
        }else {
            b = bi.next();
            p = pi.next();
        }
        Move m = moves.poll();
        while( nb > 0 ){
            // Si plus aucun coup possible
            if( (m == null && ( place == null || build == null ))) {
                return added;
            }
            // Si aucun coup entier ou s'ils sont tous moins bons que la premiere combinaison
            else if( place != null && build != null && ( m == null || m.points < place.points + build.points )) {
                // Teste de compatibilite entre les deux actions
                if (compatible(engine, place.tileAction, build.buildingAction)) {
                    // Ajout sans doublon dans le tableau branchMoves[]
                    if( add( new Move(build.buildingAction, place.tileAction, place.points + build.points), branchMoves, ind) ) {
                        nb--;
                        ind++;
                        added++;
                    }
                }
                if (place.points - p.points > build.points - b.points){
                    // On garde le meme placement et on change de construction
                    if( bi.hasNext()){
                        build = b;
                        b = bi.next();
                    }else{
                        if( pi.hasNext() ){
                            place = p;
                            p = pi.next();
                        }else{
                            place = null;
                        }
                    }
                }else{
                    // On garde la meme construction et on change le placement
                    if( pi.hasNext()) {
                        place = p;
                        p = pi.next();
                    }else{
                        if( bi.hasNext() ) {
                            build = b;
                            b = bi.next();
                        }else
                            build = null;
                    }
                }
            }// Sinon on ajoute le coup entier et on passe au suivant
            else if( m != null){
                if( add( m, branchMoves, ind) ) {
                    ind++;
                    nb--;
                    added++;
                }
                m = moves.poll();
            }else{
                return added;
            }
        }
        return added;
    }

    // Teste la compatibilité entre un placement et une construction/extension
    private boolean compatible( Engine engine, TileAction placement, BuildingAction build ) {
        engine.logger().fine("Checking compatibility : " + placement + " " + build);

        if( placement instanceof SeaTileAction ){
            if (build instanceof PlaceBuildingAction) {
                // Construction + placement mer ne pose jamais de problème
                return true;
            }
            else {
                // Si extension et placement mer :
                // On vérifie que le placement ne modifie pas l'extension :
                VolcanoTile tile = engine.getVolcanoTileStack().current();
                ExpandVillageAction expansion = (ExpandVillageAction) build;
                Village expansionVillage = expansion.getVillage(engine.getIsland());
                if (tile.getLeft() == expansion.getFieldType()) {
                    for(Hex neighbor : placement.getLeftHex().getNeighborhood() )
                        if (engine.getIsland().getField(neighbor).hasBuilding()
                                && engine.getIsland().getVillage(neighbor).equals(expansionVillage))
                            return false;
                }
                else if (tile.getRight() == expansion.getFieldType()) {
                    for(Hex neighbor : placement.getRightHex().getNeighborhood() )
                        if (engine.getIsland().getField(neighbor).hasBuilding())
                            if (engine.getIsland().getVillage(neighbor).equals(expansionVillage))
                                return false;
                }
                return true;
            }
        }
        else {
            if (build instanceof PlaceBuildingAction) {
                // Placement volcan + construction simple
                engine.action(placement);
                BuildingType buildingType = ((PlaceBuildingAction) build).getType();
                Hex buildingHex = ((PlaceBuildingAction) build).getHex();
                if (!PlaceBuildingRules.validate(engine, buildingType, buildingHex).isValid()) {
                    engine.cancelLastStep();
                    return false;
                }
                else {
                    engine.cancelLastStep();
                    return (placement.getLeftHex() != buildingHex
                            && placement.getRightHex() != buildingHex);
                }
            }
            else {
                // On vérifie que le placement ne modifie pas l'extension :
                VolcanoTile tile = engine.getVolcanoTileStack().current();
                ExpandVillageAction expansion = (ExpandVillageAction) build;
                Village expansionVillage = expansion.getVillage(engine.getIsland());
                if (tile.getLeft() == expansion.getFieldType()) {
                    for (Hex neighbor : placement.getLeftHex().getNeighborhood())
                        if (engine.getIsland().getField(neighbor).hasBuilding()
                                && expansionVillage.equals(engine.getIsland().getVillage(neighbor))) {
                            return false;
                        }
                }
                else if (tile.getRight() == expansion.getFieldType()) {
                    for (Hex neighbor : placement.getRightHex().getNeighborhood())
                        if (engine.getIsland().getField(neighbor).hasBuilding()
                                && expansionVillage.equals(engine.getIsland().getVillage(neighbor))) {
                            return false;
                        }
                }

                // On vérifie qu'on écrase pas le village OU le champ sur lequel on s'étend
                if( engine.getIsland().getField(placement.getLeftHex()).hasBuilding())
                    if( engine.getIsland().getVillage( placement.getLeftHex()).equals(expansionVillage))
                        return false;
                if( engine.getIsland().getField( placement.getLeftHex()).getType() == ( expansion.getFieldType()))
                        return false;
                if( engine.getIsland().getField(placement.getRightHex()).hasBuilding())
                    if( engine.getIsland().getVillage( placement.getRightHex()).equals(expansionVillage))
                        return false;
                if( engine.getIsland().getField( placement.getRightHex()).getType() == ( expansion.getFieldType()))
                        return false;
                return true;
            }
        }
    }

    // Tente d'ajouter un Move dans branchMoves à l'indice ind :
    // Renvoie false ssi le Move est déjà présent dans branchMoves
    private boolean add( Move m, Move[] branchMoves, int ind){
        for (int i = 0; i < ind; i++) {
            if( branchMoves[i].equals( m))
                return false;
        }
        branchMoves[ind] = m;
        return true;
    }
}

