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
    void onTileStackChange(boolean cancelled);

    /**
     * Appelé à chaque fois qu'un joueur commence la phase
     * de placement de tuile de son tour
     */
    void onTileStepStart(boolean cancelled);

    /**
     * Appelé à chaque fois qu'un joueur commence la phase
     * de construction de son tour
     */
    void onBuildStepStart(boolean cancelled);

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
    void onWin(EngineStatus.FinishReason reason, List<Player> winners);

}
