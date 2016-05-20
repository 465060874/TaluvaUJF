package IA;
import engine.Engine;
import engine.action.PlaceBuildingAction;
import engine.action.ExpandVillageAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;
import map.HexMap;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.PriorityQueue;

public class BotPlayer {

    // Facteur de branchment pour l'arbre MIN-MAX
    private int branchingFactor = 8;
    // Strategies possibles pour l'IA
    final static int nbStrategies = 4;

    // Donnees
    Engine engine = null;
    Heuristics heuristic;
    int[] strategyPoints = new int[nbStrategies];

    // Constructeur
    public BotPlayer(int branchingFactor, Heuristics heuristic) {
        this.branchingFactor = branchingFactor;
        this.heuristic = heuristic;
    }

    // Jouer un coup
    public Move play(Engine e, int depth) {
        // Créé un nouvel Engine modifiable à souhait sans interference avec
        // l'interface graphique par exemple
        System.out.println("PLAY : depth " + depth);
        this.engine = e;
        @SuppressWarnings("unchecked")
        PriorityQueue<Move>[] strategiesQueues = new PriorityQueue[nbStrategies];
        for (int i = 0; i < nbStrategies; i++)
            strategiesQueues[i] = new PriorityQueue<>((a,b) -> Integer.compare( b.points, a.points));

        // 1 -- Determiner le poids de chaque stratégie
        heuristic.chooseStrategies(engine,strategyPoints,branchingFactor);
        System.out.printf("-> Strategy Chosen\n\tTemples %d\n\tTours %d\n\tHuttes %d\n\tContre %d\n\n ", strategyPoints[0], strategyPoints[1], strategyPoints[2], strategyPoints[3]);

        // 2 -- Determiner un sous-ensemble pertinent de coups possibles
        branchSort( strategiesQueues );
        System.out.println("-> Branch Chosen");

        // 3 -- MIN-MAX sur l'arbre réduit
        // A ce point : strategiesQueues contient les coups possibles pour chaque stratégie.
        // On va donc appeler la fonction de jeu pour l'adversaire dessus...
        Move[] branchMoves = new Move[branchingFactor];
        int ind = 0;
        for( int i = 0; i < nbStrategies; i++ ) {
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

        // Maintenant il suffit d'appeler la fonction d'evaluation pour l'adversaire
        // Et de choisir le meilleur choix
        Move bestMove = null;
        int bestPoints = Integer.MAX_VALUE;
        int bestConfigPoints = Integer.MIN_VALUE;
        int p;
        for (int i = 0; i < branchingFactor; i++) {
            engine.action(branchMoves[i].tileAction);
            engine.action(branchMoves[i].buildingAction);
            if (depth > 0) {
                Move m = play(engine, depth - 1);
                if (m.points < bestPoints) {
                    bestPoints = m.points;
                    bestMove = branchMoves[i];
                }
            }
            else {
                if( (p = heuristic.evaluateConfiguration(engine)) > bestConfigPoints ){
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
    private void branchSort(PriorityQueue<Move> [] strategiesQueues) {
        // Pour tout tileAction dans la mer
        System.out.println("[Sort] Begin seaPlacements : " + engine.getSeaPlacements().size());
        for (Iterable<SeaTileAction> seaPlacements : engine.getSeaPlacements().values()) {
            for (SeaTileAction placement  : seaPlacements) {
                int points = heuristic.evaluateSeaPlacement(engine, placement);
                engine.placeOnSea(placement);

                // Pour toute construction :
                //System.out.println("    [Sort] Begin buildActions : " + engine.getBuildActions().size());
                HexMap<? extends Iterable<PlaceBuildingAction>> buildActionsMap = engine.getBuildActions();
                for (Iterable<PlaceBuildingAction> buildActions : buildActionsMap.values()) {
                    for (PlaceBuildingAction action : buildActions) {
                        heuristic.evaluateBuildAction(engine, placement, action, points, strategiesQueues);
                    }

                }
                //System.out.println("    [Sort] Begin expandActions : " + engine.getExpandActions().size());
                HexMap<? extends Iterable<ExpandVillageAction>> expandActionsMap = engine.getExpandActions();
                for (Iterable<ExpandVillageAction> expandActions : expandActionsMap.values()) {
                    for (ExpandVillageAction action : expandActions) {
                        heuristic.evaluateExpandAction(engine, placement, action, points, strategiesQueues);
                    }
                }
                engine.cancelLastStep();
            }
        }
        System.out.println("[Sort] Begin volcanoPlacements : " + engine.getSeaPlacements().size());
        // Pour tout tileAction sur la terre
        for (Iterable<VolcanoTileAction> volcanoPlacements : engine.getVolcanoPlacements().values()) {
            for (VolcanoTileAction placement  : volcanoPlacements) {
                int points = heuristic.evaluateVolcanoPlacement(engine, placement);
                engine.placeOnVolcano(placement);

                // Pour toute construction :
                for (Iterable<PlaceBuildingAction> buildActions : engine.getBuildActions().values()) {
                    for (PlaceBuildingAction action : buildActions) {
                        heuristic.evaluateBuildAction(engine, placement, action, points, strategiesQueues);
                    }
                }

                for (Iterable<ExpandVillageAction> expandActions : engine.getExpandActions().values()) {
                    for (ExpandVillageAction action : expandActions) {
                        heuristic.evaluateExpandAction(engine, placement, action, points, strategiesQueues);
                    }
                }
                engine.cancelLastStep();
            }
        }
    }
}
