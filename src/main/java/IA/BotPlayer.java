package IA;

import com.google.common.collect.Iterables;
import engine.Engine;
import engine.EngineLoggerObserver;
import engine.EngineStatus;
import engine.action.*;
import ui.Placement;

import java.util.Iterator;
import java.util.PriorityQueue;

import static com.google.common.collect.Iterables.getOnlyElement;

class BotPlayer {

    // Strategies possibles pour l'IA
    private static final int NB_STRATEGIES = 4;

    // Facteur de branchment pour l'arbre MIN-MAX
    private final int branchingFactor;
    // Heuristics utilisées
    private final Heuristics heuristics;

    // Donnees
    private final Engine realEngine;
    private final int[] strategyPoints = new int[NB_STRATEGIES];

    // Constructeur
    BotPlayer(int branchingFactor, Heuristics heuristics, Engine realEngine) {
        this.branchingFactor = branchingFactor;
        this.heuristics = heuristics;
        this.realEngine = realEngine;
    }

    // Jouer un coup
    Move play(int depth) {
        if( realEngine.getStatus().getTurn() == 0 )
            return doFirstPlay(realEngine);
        Engine engineCopy = realEngine.copyWithoutObservers();
        //engineCopy.registerObserver(new EngineLoggerObserver(engineCopy, "[IA]"));
        return doPlay(engineCopy, depth);
    }

    private Move doFirstPlay( Engine engine){
        Move m;
        TileAction seaPlacement = getOnlyElement(getOnlyElement(engine.getSeaPlacements().values()));
        Engine engineCopy  = realEngine.copyWithoutObservers();
        engineCopy.action( seaPlacement );
        BuildingAction buildAction1 = getOnlyElement(Iterables.get( engineCopy.getBuildActions().values(), 0));
        BuildingAction buildAction2 = getOnlyElement(Iterables.get( engineCopy.getBuildActions().values(), 1));
        int n = engine.getRandom().nextInt(2);
        if( n == 0 )
            return new Move( buildAction1, seaPlacement, 0);
        else
            return new Move( buildAction2, seaPlacement, 0);
    }


    private Move doPlay(Engine engine, int depth) {
        engine.logger().fine("PLAY : depth {0}", depth);

        // 1 -- Determiner le poids de chaque stratégie
        heuristics.chooseStrategies(engine,strategyPoints,branchingFactor);
        engine.logger().fine("-> Strategy Chosen\n" +
                "\tTemples {0}\n\tTours {1}\n\tHuttes {2}\n\tContre {3}",
                strategyPoints[0], strategyPoints[1], strategyPoints[2], strategyPoints[3]);

        // 2 -- Determiner un sous-ensemble pertinent de coups possibles
        Move[] branchMoves = new Move[branchingFactor];
        branchSort(engine, strategyPoints, branchMoves );
        engine.logger().fine("-> Branch Chosen");

        // 3 -- MIN-MAX sur l'arbre réduit
        // A ce point : strategiesQueues contient les coups possibles pour chaque stratégie.
        // On va donc appeler la fonction de jeu pour l'adversaire dessus...

        // Maintenant il suffit d'appeler la fonction d'evaluation pour l'adversaire
        // Et de choisir le meilleur choix
        Move bestMove = null;
        int bestPoints = Integer.MAX_VALUE;
        int bestConfigPoints = Integer.MIN_VALUE;
        int p;
        for (int i = 0; i < branchingFactor; i++) {
            engine.action(branchMoves[i].tileAction);
            engine.action(branchMoves[i].buildingAction);
            if( engine.getStatus() instanceof EngineStatus.Finished ){
                if( ! ((EngineStatus.Finished) engine.getStatus()).getWinners().contains( engine.getCurrentPlayer()))
                    return new Move( branchMoves[i].buildingAction, branchMoves[i].tileAction, Integer.MAX_VALUE);
                else
                    return new Move( branchMoves[i].buildingAction, branchMoves[i].tileAction, Integer.MIN_VALUE);
            }else if (depth > 0) {
                Move m = doPlay(engine, depth - 1);
                if (m.points < bestPoints) {
                    bestPoints = m.points;
                    bestMove = branchMoves[i];
                }
            }else{
                if( (p = heuristics.evaluateConfiguration(engine)) > bestConfigPoints ){
                    bestMove = new Move(branchMoves[i].buildingAction, branchMoves[i].tileAction, p);
                    bestConfigPoints = p;
                }
            }
            engine.cancelLastStep();
            engine.cancelLastStep();
        }

        return bestMove;
    }

    // Fonction qui classe les coups selon la stratégie choisie
    private void branchSort(Engine engine, int[] strategyPoints, Move[] branchMoves) {
        int comp = 0;
        PriorityQueue<Move>[] strategiesQueues = new PriorityQueue[NB_STRATEGIES];
        for (int i = 0; i < NB_STRATEGIES; i++)
            strategiesQueues[i] = new PriorityQueue<>((a,b) -> Integer.compare( b.points, a.points));

        // Pour tout tileAction dans la mer
        engine.logger().fine("[Sort] Begin seaPlacements : {0}", engine.getSeaPlacements().size());
        for (Iterable<SeaTileAction> seaPlacements : engine.getSeaPlacements().values()) {
            for (SeaTileAction placement  : seaPlacements) {
                int points = heuristics.evaluateSeaPlacement(engine, placement);
                engine.placeOnSea(placement);

                // Pour toute construction :
                engine.logger().finer("    [Sort] Begin buildActions : {0}", engine.getBuildActions().size());
                for (Iterable<PlaceBuildingAction> buildActions : engine.getBuildActions().values()) {
                    for (PlaceBuildingAction action : buildActions) {
                        heuristics.evaluateBuildAction(engine, placement, action, points, strategiesQueues);
                        comp++;
                    }

                }

                engine.logger().finer("    [Sort] Begin expandActions : {0}", engine.getExpandActions().size());
                for (Iterable<ExpandVillageAction> expandActions : engine.getExpandActions().values()) {
                    for (ExpandVillageAction action : expandActions) {
                        heuristics.evaluateExpandAction(engine, placement, action, points, strategiesQueues);
                        comp++;
                    }
                }
                engine.cancelLastStep();
            }
        }

        engine.logger().fine("[Sort] Begin volcanoPlacements : {0}", engine.getVolcanoPlacements().size());
        // Pour tout tileAction sur la terre
        for (Iterable<VolcanoTileAction> volcanoPlacements : engine.getVolcanoPlacements().values()) {
            for (VolcanoTileAction placement  : volcanoPlacements) {
                int points = heuristics.evaluateVolcanoPlacement(engine, placement);
                engine.placeOnVolcano(placement);

                // Pour toute construction :
                for (Iterable<PlaceBuildingAction> buildActions : engine.getBuildActions().values()) {
                    for (PlaceBuildingAction action : buildActions) {
                        heuristics.evaluateBuildAction(engine, placement, action, points, strategiesQueues);
                        comp++;
                    }
                }

                for (Iterable<ExpandVillageAction> expandActions : engine.getExpandActions().values()) {
                    for (ExpandVillageAction action : expandActions) {
                        heuristics.evaluateExpandAction(engine, placement, action, points, strategiesQueues);
                        comp++;
                    }
                }
                engine.cancelLastStep();
            }
        }
        engine.logger().fine("[Sort] {0} evaluations made", comp);

        // On choisit les meilleurs coups
        int ind = 0;
        for(int i = 0; i < NB_STRATEGIES; i++ ) {
            for (int j = strategyPoints[i]; j > 0; j--) {
                boolean found;
                Move m;
                // Check if not already chosen
                do {
                    m = strategiesQueues[i].poll();
                    found = false;
                    for (int k = 0; k < ind; k++) {
                        if (branchMoves[k].equals(m)) {
                            found = true;
                            break;
                        }
                    }
                } while (found);
                // Add to player moves
                branchMoves[ind++] = m;
            }
        }

    }

    private void branchSortFusion( Engine engine, int [] strategyPoints, Move [] branchMoves) {
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
        engine.logger().fine("[Sort] Begin seaPlacements : {0}", engine.getSeaPlacements().size());
        for (Iterable<SeaTileAction> seaPlacements : engine.getSeaPlacements().values()) {
            for (SeaTileAction placement  : seaPlacements) {
                int points = heuristics.evaluateSeaPlacement(engine, placement);
                // Ajout du placement seul
                placements.add( new Move(null, placement, points));
                engine.placeOnSea(placement);
                comp++;
                // Pour chaque construction et extension correlee
                for (PlaceBuildingAction action : engine.getBuildActions(placement)) {
                    heuristics.evaluateBuildAction(engine, placement, action, points, moves);
                    comp++;
                }
                for (ExpandVillageAction action : engine.getExpandActions(placement)) {
                    heuristics.evaluateExpandAction(engine, placement, action, points, moves);
                    comp++;
                }
                engine.cancelLastStep();
            }
        }

        // Evaluation des placements sur la terre + moves entiers a la volee
        engine.logger().fine("[Sort] Begin volcanoPlacements : {0}", engine.getVolcanoPlacements().size());
        for (Iterable<VolcanoTileAction> volcanoPlacements : engine.getVolcanoPlacements().values()) {
            for (VolcanoTileAction placement  : volcanoPlacements) {
                int points = heuristics.evaluateVolcanoPlacement(engine, placement);
                placements.add( new Move(null, placement, points));
                engine.placeOnVolcano(placement);
                comp++;
                for (PlaceBuildingAction action : engine.getBuildActions(placement)) {
                    heuristics.evaluateBuildAction(engine, placement, action, points, moves);
                    comp++;
                }
                for (ExpandVillageAction action : engine.getExpandActions(placement)) {
                    heuristics.evaluateExpandAction(engine, placement, action, points, moves);
                    comp++;
                }
                engine.cancelLastStep();
            }
        }

        // Evaluation des constructions seules
        for (Iterable<PlaceBuildingAction> buildActions : engine.getBuildActions().values()) {
            for (PlaceBuildingAction action : buildActions) {
                heuristics.evaluateBuildAction(engine, null, action, 0, building);
                comp++;
            }
        }
        for (Iterable<ExpandVillageAction> expandActions : engine.getExpandActions().values()) {
            for (ExpandVillageAction action : expandActions) {
                heuristics.evaluateExpandAction(engine, null, action, 0, building);
                comp++;
            }
        }

        // Fusion :
        // On choisit les meilleurs coups
        int ind = 0;
        for(int i = 0; i < NB_STRATEGIES; i++ )
            ind += combine(engine, placements, building[i], branchMoves, ind, strategyPoints[i], moves[i]);

    }

    // Ajoute à partir de l'indice ind dans branchMoves[] nb moves les meilleurs en combinant les placements <placements> et les constructions <building> et les coups entiers <moves>
    // Renvoie l'argument nb
    private int combine( Engine engine, PriorityQueue<Move> placements, PriorityQueue<Move> building, Move[] branchMoves, int ind, int nb, PriorityQueue<Move> moves){
        Move place, build, p, b;
        int sauv = nb;
        Iterator<Move> pi, bi;
        place = placements.peek();
        build = building.peek();
        pi = placements.iterator();
        bi = building.iterator();
        b = bi.next();
        p = pi.next();
        Move m = moves.poll();
        while( nb > 0 ){
            // Si aucun coup entier ou s'ils sont tous moins bons que la premiere combinaison
            if( m == null || m.points < place.points + build.points ) {
                // Teste de compatibilite entre les deux actions
                if (compatible(engine, place.tileAction, build.buildingAction)) {
                    // Ajout sans doublon dans le tableau branchMoves[]
                    if( add( new Move(build.buildingAction, place.tileAction, place.points + build.points), branchMoves, ind) ) {
                        nb--;
                        ind++;
                    }
                }
                // Cas d'erreur qui ne devrait pas apparaître !
                if (!pi.hasNext() && !bi.hasNext())
                    return sauv;
                if (place.points - p.points > build.points - b.points){
                    // On garde le meme placement et on change de construction
                    build = b;
                    b = bi.next();
                }else{
                    // On garde la meme construction et on change le placement
                    place = p;
                    p = pi.next();
                }
            }// Sinon on ajoute le coup entier et on passe au suivant
            else{
                if( add( m, branchMoves, ind) ) {
                    ind++;
                    nb--;
                }
                m = moves.poll();
            }
        }
        return sauv;
    }

    private boolean compatible( Engine engine, TileAction placement, BuildingAction build ){
        if( placement instanceof SeaTileAction ){
            if( build instanceof PlaceBuildingAction )
                return true;
            else{
                // EXTENSION
                return false;
            }
        }else{
            if( build instanceof PlaceBuildingAction )
                return ( ((VolcanoTileAction)placement).getLeftHex() != ((PlaceBuildingAction)build).getHex()
                        && ((VolcanoTileAction)placement).getRightHex() != ((PlaceBuildingAction)build).getHex() );
            else {
                // EXTENSION
                return false;
            }
            // !!! TEMPLE / TOUR - BuildRules -> validate
        }

    }

    private boolean add( Move m, Move[] branchMoves, int ind){
        for (int i = 0; i < ind; i++) {
            if( branchMoves[i].equals( m))
                return false;
        }
        branchMoves[ind] = m;
        return true;
    }
}
