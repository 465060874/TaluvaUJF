package IA;
import data.PlayerColor;
import engine.*;
import engine.action.BuildAction;
import engine.action.ExpandAction;
import engine.action.SeaPlacement;
import engine.action.VolcanoPlacement;
import map.Hex;
import map.HexMap;
import java.util.*;

public class BOTPlayer {
    // Facteur de branchment pour l'arbre MIN-MAX
    private int branchingFactor = 10;
    PlayerColor color;
    // Strategies possibles pour l'IA
    final static int nbStrategies = 4;
    final static int TEMPLESTRATEGY = 0;
    final static int TOWERSTRATEGY = 1;
    final static int HUTSTRATEGY = 2;
    final static int COUNTERSTRATEGY = 3;

    // Donnees
    Engine engine = null;
    Heuristics heuristic;
    int[] strategyPoints = new int[nbStrategies];

    // Constructeur
    public BOTPlayer(int branchingFactor) {
        this.branchingFactor = branchingFactor;
    }

    // Jouer un coup
    public FullMove play(Engine e) {
        int points;
        engine = e.copyWithoutObservers(); // New engine which can be modified
        PriorityQueue<FullMove> [] strategiesQueues = new PriorityQueue[nbStrategies];
        for (int i = 0; i < nbStrategies; i++)
            strategiesQueues[i] = new PriorityQueue<>((a,b) -> Integer.compare( b.points, a.points));
        color = e.getCurrentPlayer().getColor();

        // 1 -- Determiner le poids de chaque stratégie
        heuristic.chooseStrategies(engine,strategyPoints,branchingFactor);

        // 2 -- Determiner un sous-ensemble pertinent de coups possibles
        branchSort( strategiesQueues );

        // 3 -- MIN-MAX sur l'arbre réduit
        // A ce point : strategiesQueues contient les coups possibles pour chaque stratégie.
        // On va donc appeler la fonction de jeu pour l'adversaire dessus...
        FullMove [] branchMoves = new FullMove[branchingFactor];
        int ind = 0;
        for( int i = 0; i < nbStrategies; i++ )
            for( int j = strategyPoints[i]; j > 0; j-- ) {
                boolean found;
                FullMove m;
                // Check if not already chosen
                do{
                    m = strategiesQueues[i].poll();
                    found = false;
                    for (int k = 0; k < ind; k++) {
                        if( branchMoves[k] == m ){
                            found = true;
                            break;
                        }
                    }
                }while( found );
                // Add to player moves
                branchMoves[ind++] = m;
            }
        // Maintenant il suffit d'appeler la fonction d'evaluation pour l'adversaire
        // Et de choisir le meilleur choix
        FullMove bestMove = null;
        int bestPoints = Integer.MAX_VALUE;
        for (int i = 0; i < branchingFactor; i++) {
            engine.place( branchMoves[i].placement);
            engine.action( branchMoves[i].action);
            FullMove m = play(engine);
            if( m.points < bestPoints ) {
                bestPoints = m.points;
                bestMove = m;
            }
            engine.cancelLastStep();
            engine.cancelLastStep();
        }
        return bestMove;
    }

    // Fonction qui classe les coups selon la stratégie choisie
    private void branchSort( PriorityQueue<FullMove> [] strategiesQueues){
        int points;
        HexMap<? extends Iterable<SeaPlacement>> seaPlacementsMap = engine.getSeaPlacements(); // Hexmap des seaPlacements
        HexMap<? extends Iterable<VolcanoPlacement>> volcanoPlacementsMap = engine.getVolcanoPlacements(); // Hexmap des volcanos placements
        // Pour tout placement dans la mer
        for (Hex seaPlacement : seaPlacementsMap) {
            Iterable<SeaPlacement> seaPlacementsList = seaPlacementsMap.get( seaPlacement );
            for (SeaPlacement placement  : seaPlacementsList) {
                points = heuristic.evaluateSeaPlacement(engine, placement);
                engine.placeOnSea( placement );
                // Pour toute construction :
                HexMap<? extends Iterable<BuildAction>> buildActionsMap = engine.getBuildActions();
                for (Hex buildAction : buildActionsMap) {
                    Iterable<BuildAction> buildActionsList = buildActionsMap.get( buildAction );
                    for (BuildAction action : buildActionsList) {
                        heuristic.evaluateBuildAction(engine, action, placement, points, strategiesQueues);
                    }

                }
                HexMap<? extends Iterable<ExpandAction>> expandActionsMap = engine.getExpandActions();
                for (Hex expandAction : expandActionsMap) {
                    Iterable<ExpandAction> expandActionsList = expandActionsMap.get( expandAction );
                    for (ExpandAction action : expandActionsList) {
                        heuristic.evaluateExpandAction(engine, action, placement, points, strategiesQueues);
                    }
                }
                engine.cancelLastStep();
            }
        }
        // Pour tout placement sur la terre
        for (Hex hex : volcanoPlacementsMap) {
            Iterable<VolcanoPlacement> volcanoPlacementsList = volcanoPlacementsMap.get( hex );
            for (VolcanoPlacement placement  : volcanoPlacementsList) {
                points = heuristic.evaluateVolcanoPlacement(engine, placement);
                engine.placeOnVolcano( placement );
                // Pour toute construction :
                HexMap<? extends Iterable<BuildAction>> buildActionsMap  = engine.getBuildActions();
                for (Hex buildAction : buildActionsMap) {
                    Iterable<BuildAction> buildActionsList = buildActionsMap.get( buildAction );
                    for (BuildAction action : buildActionsList) {
                        heuristic.evaluateBuildAction(engine, action, placement, points, strategiesQueues);
                    }
                }
                HexMap<? extends Iterable<ExpandAction>> expandActionsMap = engine.getExpandActions();
                for (Hex expandAction : expandActionsMap) {
                    Iterable<ExpandAction> expandActionsList = expandActionsMap.get( expandAction );
                    for (ExpandAction action : expandActionsList) {
                        heuristic.evaluateExpandAction(engine, action, placement, points, strategiesQueues);
                    }
                }
                engine.cancelLastStep();
            }
        }
    }
}
