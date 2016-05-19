package IA;
import engine.Engine;
import engine.action.BuildAction;
import engine.action.ExpandAction;
import engine.action.SeaPlacement;
import engine.action.VolcanoPlacement;
import map.HexMap;

import java.util.PriorityQueue;

public class BotPlayer {

    // Facteur de branchment pour l'arbre MIN-MAX
    private int branchingFactor = 16;
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
    public FullMove play(Engine e, int depth) {
        // Créé un nouvel Engine modifiable à souhait sans interference avec
        // l'interface graphique par exemple
        this.engine = e;
        @SuppressWarnings("unchecked")
        PriorityQueue<FullMove> [] strategiesQueues = new PriorityQueue[nbStrategies];
        for (int i = 0; i < nbStrategies; i++)
            strategiesQueues[i] = new PriorityQueue<>((a,b) -> Integer.compare( b.points, a.points));

        // 1 -- Determiner le poids de chaque stratégie
        heuristic.chooseStrategies(engine,strategyPoints,branchingFactor);

        // 2 -- Determiner un sous-ensemble pertinent de coups possibles
        branchSort( strategiesQueues );

        // 3 -- MIN-MAX sur l'arbre réduit
        // A ce point : strategiesQueues contient les coups possibles pour chaque stratégie.
        // On va donc appeler la fonction de jeu pour l'adversaire dessus...
        FullMove[] branchMoves = new FullMove[branchingFactor];
        int ind = 0;
        for( int i = 0; i < nbStrategies; i++ ) {
            for (int j = strategyPoints[i]; j > 0; j--) {
                boolean found;
                FullMove m;
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
        FullMove bestMove = null;
        int bestPoints = Integer.MAX_VALUE;
        int bestConfigPoints = Integer.MIN_VALUE;
        int p;
        for (int i = 0; i < branchingFactor; i++) {
            engine.place(branchMoves[i].placement);
            engine.action(branchMoves[i].action);
            if (depth > 0) {
                FullMove m = play(engine, depth - 1);
                if (m.points < bestPoints) {
                    bestPoints = m.points;
                    bestMove = m;
                }
            }
            else {
                if( (p = heuristic.evaluateConfiguration(engine)) > bestConfigPoints ){
                    bestMove = new FullMove(branchMoves[i].action, branchMoves[i].placement, p);
                    bestConfigPoints = p;
                }
            }
            engine.cancelLastStep();
            engine.cancelLastStep();
        }

        return bestMove;
    }

    // Fonction qui classe les coups selon la stratégie choisie
    private void branchSort(PriorityQueue<FullMove> [] strategiesQueues) {
        // Pour tout placement dans la mer
        for (Iterable<SeaPlacement> seaPlacements : engine.getSeaPlacements().values()) {
            for (SeaPlacement placement  : seaPlacements) {
                int points = heuristic.evaluateSeaPlacement(engine, placement);
                engine.placeOnSea(placement);

                // Pour toute construction :
                HexMap<? extends Iterable<BuildAction>> buildActionsMap = engine.getBuildActions();
                for (Iterable<BuildAction> buildActions : buildActionsMap.values()) {
                    for (BuildAction action : buildActions) {
                        heuristic.evaluateBuildAction(engine, placement, action, points, strategiesQueues);
                    }

                }

                HexMap<? extends Iterable<ExpandAction>> expandActionsMap = engine.getExpandActions();
                for (Iterable<ExpandAction> expandActions : expandActionsMap.values()) {
                    for (ExpandAction action : expandActions) {
                        heuristic.evaluateExpandAction(engine, placement, action, points, strategiesQueues);
                    }
                }
                engine.cancelLastStep();
            }
        }

        // Pour tout placement sur la terre
        for (Iterable<VolcanoPlacement> volcanoPlacements : engine.getVolcanoPlacements().values()) {
            for (VolcanoPlacement placement  : volcanoPlacements) {
                int points = heuristic.evaluateVolcanoPlacement(engine, placement);
                engine.placeOnVolcano(placement);

                // Pour toute construction :
                for (Iterable<BuildAction> buildActions : engine.getBuildActions().values()) {
                    for (BuildAction action : buildActions) {
                        heuristic.evaluateBuildAction(engine, placement, action, points, strategiesQueues);
                    }
                }

                for (Iterable<ExpandAction> expandActions : engine.getExpandActions().values()) {
                    for (ExpandAction action : expandActions) {
                        heuristic.evaluateExpandAction(engine, placement, action, points, strategiesQueues);
                    }
                }
                engine.cancelLastStep();
            }
        }
    }
}
