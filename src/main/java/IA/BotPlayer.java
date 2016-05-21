package IA;

import engine.Engine;
import engine.action.ExpandVillageAction;
import engine.action.PlaceBuildingAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;

import java.util.PriorityQueue;

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
        return doPlay(realEngine.copyWithoutObservers(), depth);
    }

    private Move doPlay(Engine engine, int depth) {
        engine.logger().fine("PLAY : depth {0}", depth);
        @SuppressWarnings("unchecked")
        PriorityQueue<Move>[] strategiesQueues = new PriorityQueue[NB_STRATEGIES];
        for (int i = 0; i < NB_STRATEGIES; i++)
            strategiesQueues[i] = new PriorityQueue<>((a,b) -> Integer.compare( b.points, a.points));

        // 1 -- Determiner le poids de chaque stratégie
        heuristics.chooseStrategies(engine,strategyPoints,branchingFactor);
        engine.logger().fine("-> Strategy Chosen\n" +
                "\tTemples {0}\n\tTours {1}\n\tHuttes {2}\n\tContre {3}",
                strategyPoints[0], strategyPoints[1], strategyPoints[2], strategyPoints[3]);

        // 2 -- Determiner un sous-ensemble pertinent de coups possibles
        branchSort(engine, strategiesQueues);
        engine.logger().fine("-> Branch Chosen");

        // 3 -- MIN-MAX sur l'arbre réduit
        // A ce point : strategiesQueues contient les coups possibles pour chaque stratégie.
        // On va donc appeler la fonction de jeu pour l'adversaire dessus...
        Move[] branchMoves = new Move[branchingFactor];
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
                Move m = doPlay(engine, depth - 1);
                if (m.points < bestPoints) {
                    bestPoints = m.points;
                    bestMove = branchMoves[i];
                }
            }
            else {
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
    private void branchSort(Engine engine, PriorityQueue<Move> [] strategiesQueues) {
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
                    }

                }

                engine.logger().finer("    [Sort] Begin expandActions : {0}", engine.getExpandActions().size());
                for (Iterable<ExpandVillageAction> expandActions : engine.getExpandActions().values()) {
                    for (ExpandVillageAction action : expandActions) {
                        heuristics.evaluateExpandAction(engine, placement, action, points, strategiesQueues);
                    }
                }
                engine.cancelLastStep();
            }
        }

        engine.logger().fine("[Sort] Begin volcanoPlacements : {0}", engine.getSeaPlacements().size());
        // Pour tout tileAction sur la terre
        for (Iterable<VolcanoTileAction> volcanoPlacements : engine.getVolcanoPlacements().values()) {
            for (VolcanoTileAction placement  : volcanoPlacements) {
                int points = heuristics.evaluateVolcanoPlacement(engine, placement);
                engine.placeOnVolcano(placement);

                // Pour toute construction :
                for (Iterable<PlaceBuildingAction> buildActions : engine.getBuildActions().values()) {
                    for (PlaceBuildingAction action : buildActions) {
                        heuristics.evaluateBuildAction(engine, placement, action, points, strategiesQueues);
                    }
                }

                for (Iterable<ExpandVillageAction> expandActions : engine.getExpandActions().values()) {
                    for (ExpandVillageAction action : expandActions) {
                        heuristics.evaluateExpandAction(engine, placement, action, points, strategiesQueues);
                    }
                }
                engine.cancelLastStep();
            }
        }
    }
}
