package engine;

import engine.action.PlaceBuildingAction;
import engine.action.ExpandVillageAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;

import java.util.List;

public interface EngineObserver {

    /**
     * Appelé en tout début de partie
     */
    void onStart();

    /**
     * Appelé à chaque fois que la pioche change
     */
    void onTileStackChange();

    /**
     * Appelé à chaque fois qu'un joueur commence la phase
     * de placement de tuile de son tour
     */
    void onTileStepStart();

    /**
     * Appelé à chaque fois qu'un joueur commence la phase
     * de construction de son tour
     */
    void onBuildStepStart();

    /**
     * Appelé quand une tuile est placé sur la mer
     */
    void onTilePlacementOnSea(SeaTileAction action);

    /**
     * Appelé quand une tuile est placé sur un volcan
     */
    void onTilePlacementOnVolcano(VolcanoTileAction action);

    /**
     * Appelé quand un batiment est construit
     */
    void onBuild(PlaceBuildingAction action);

    /**
     * Appelé quand une extension de village est faite
     */
    void onExpand(ExpandVillageAction action);

    /**
     * Appelé en cas d'élimination
     */
    void onEliminated(Player eliminated);

    /**
     * Appelé en cas de victoire
     */
    void onWin(WinReason reason, List<Player> winners);

    enum WinReason {
        NO_MORE_TILES,
        TWO_BUILDING_TYPES, LAST_STANDING,
    }
}
