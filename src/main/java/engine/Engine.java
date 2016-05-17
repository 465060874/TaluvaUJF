package engine;

import engine.action.BuildAction;
import engine.action.ExpandAction;
import engine.action.SeaPlacement;
import engine.action.VolcanoPlacement;
import map.HexMap;
import map.Island;

import java.util.List;
import java.util.Random;

public interface Engine {

    /**
     * Retourne une instance commune de la classe Random
     */
    Random getRandom();

    /**
     * Initialise la liste de joueurs pour la partie
     */
    void init(Player... players);

    /**
     * Enregistre un observer qui sera notifié des changements
     * se déroulant dans le moteur
     */
    void registerObserver(EngineObserver observer);

    /**
     * Desenregistre un observer préalablement enregistré
     */
    void unregisterObserver(EngineObserver observer);

    /**
     * Retourne le mode de jeu (1v1, 1v1v1, 1v1v1v1, 2v2)
     */
    Gamemode getGamemode();

    /**
     * Retourne l'île
     */
    Island getIsland();

    /**
     * Retourne la pioche du jeu
     */
    TileStack getVolcanoTileStack();

    /**
     * Retourne la liste des joueurs dans l'ordre
     */
    List<Player> getPlayers();

    void start();

    /**
     * Retourne le joueur dont c'est actuellement
     * le tour
     */
    Player getCurrentPlayer();

    /**
     * Retourne la liste des placements possibles de tuiles
     * sur la mer
     */
    HexMap<SeaPlacement> getSeaPlacements();

    /**
     * Retourne la liste des placements possibles de tuiles
     * sur les volcans
     */
    HexMap<VolcanoPlacement> getVolcanoPlacements();

    /**
     * Retourne la liste des constructions possibles
     */
    HexMap<BuildAction> getBuildActions();

    /**
     * Retourne la liste des extensions de villages possibles
     */
    HexMap<ExpandAction> getExpandActions();

    /**
     * Joue le coup donné en paramètre
     */
    void placeOnSea(SeaPlacement placement);
    void placeOnVolcano(VolcanoPlacement placement);
    void build(BuildAction action);
    void expand(ExpandAction action);
}
