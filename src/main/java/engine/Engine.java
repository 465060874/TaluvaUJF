package engine;

import data.BuildingType;
import data.FieldType;
import data.VolcanoTile;
import map.Hex;
import map.Island;
import map.Orientation;
import map.Village;

import java.util.List;

public interface Engine {

    /**
     * Enregistre un observer qui sera notifié des changements
     * se déroulant dans le moteur
     */
    void registerObserver(EngineObserver observer);

    /**
     * Desenregistre un observer préalablement enregistré
     */
    void unregisterObserver(EngineObserver observer);

    Gamemode getGamemode();

    Island getIsland();

    TileStack getStack();

    /**
     * Retourne la liste des joueurs dans l'ordre
     */
    List<Player> getPlayersFromFirst();

    /**
     * Retourne le joueur dont c'est actuellement
     * le tour
     */
    Player getCurrentPlayer();

    /**
     * Retourne un iterable cyclique des joueurs
     * depuis le joueur dont c'est actuellement
     * le tour
     */
    Iterable<Player> getPlayersFromCurrent();

    boolean canPlaceTileOnVolcano(VolcanoTile tile, Hex hex, Orientation orientation);

    void placeTileOnVolcano(VolcanoTile tile, Hex hex, Orientation orientation);

    boolean canPlaceTileOnSea(VolcanoTile tile, Hex hex, Orientation orientation);

    void placeTileOnSea(VolcanoTile tile, Hex hex, Orientation orientation);

    boolean canBuild(BuildingType buildingType, Hex hex);

    void build(BuildingType buildingType, Hex hex);

    boolean canExpandVillage(Village village, FieldType fieldType);

    void expandVillage(Village village, FieldType fieldType);
}
