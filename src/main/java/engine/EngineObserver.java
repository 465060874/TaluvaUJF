package engine;

import engine.action.BuildAction;
import engine.action.ExpandAction;
import engine.action.SeaPlacement;
import engine.action.VolcanoPlacement;

public interface EngineObserver {

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
    void onTilePlacementOnSea(SeaPlacement placement);

    /**
     * Appelé quand une tuile est placé sur un volcan
     */
    void onTilePlacementOnVolcano(VolcanoPlacement placement);

    /**
     * Appelé quand un batiment est construit
     */
    void onBuild(BuildAction action);

    /**
     * Appelé quand une extension de village est faite
     */
    void onExpand(ExpandAction action);
}
