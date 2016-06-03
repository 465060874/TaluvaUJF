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
     * Appelé quand une étape de placement de tuile est annulée
     */
    void onCancelTileStep();

    /**
     * Appelé quand une étape de placement de bâtiment est annulée
     */
    void onCancelBuildStep();

    /**
     * Appelé quand une étape de placement de tuile est refaite
     */
    void onRedoTileStep();

    /**
     * Appelé quand une étape de placement de bâtiment est refaite
     */
    void onRedoBuildStep();

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
    void onWin(EngineStatus.FinishReason reason, List<Player> winners);

    void onBeforeExpand(ExpandVillageAction action);

    abstract class Dummy implements EngineObserver {

        @Override
        public void onStart() {
        }

        @Override
        public void onCancelTileStep() {
        }

        @Override
        public void onCancelBuildStep() {
        }

        @Override
        public void onRedoTileStep() {
        }

        @Override
        public void onRedoBuildStep() {
        }

        @Override
        public void onTileStackChange() {
        }

        @Override
        public void onTileStepStart() {
        }

        @Override
        public void onBuildStepStart() {
        }

        @Override
        public void onTilePlacementOnSea(SeaTileAction action) {
        }

        @Override
        public void onTilePlacementOnVolcano(VolcanoTileAction action) {
        }

        @Override
        public void onBuild(PlaceBuildingAction action) {
        }

        @Override
        public void onExpand(ExpandVillageAction action) {
        }

        @Override
        public void onEliminated(Player eliminated) {
        }

        @Override
        public void onWin(EngineStatus.FinishReason reason, List<Player> winners) {
        }
    }
}
