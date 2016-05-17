package engine;

import data.BuildingType;
import data.FieldType;
import map.Hex;
import map.Orientation;
import map.Village;

public interface EngineObserver {

    /**
     * Appelé à chaque fois qu'un nouveau tour de joueur
     * commence
     */
    void onTurnStart();

    /**
     * Appelé quand une tuile est placé sur un volcan
     */
    void onPlaceTileOnVolcano(Hex hex, Orientation orientation);

    /**
     * Appelé quand une tuile est placé sur la mer
     */
    void onPlaceTileOnSea(Hex hex, Orientation orientation);

    /**
     * Appelé quand un batiment est construit
     */
    void onBuild(BuildingType buildingType, Hex hex);

    /**
     * Appelé quand une extension de village est faite
     */
    void onExpand(Village village, FieldType fieldType);
}
