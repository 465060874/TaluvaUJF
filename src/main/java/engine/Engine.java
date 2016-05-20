package engine;

import engine.action.*;
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
    VolcanoTileStack getVolcanoTileStack();

    /**
     * Retourne la liste des joueurs dans l'ordre
     */
    List<Player> getPlayers();

    /**
     * Demarre la partie
     */
    void start();

    /**
     * Créé une copie de l'engine sans les observers
     */
    Engine copyWithoutObservers();

    /**
     * Annule le dernier coup (placement ou construction)
     */
    void cancelLastStep();

    /**
     * Retourne le joueur dont c'est actuellement
     * le tour
     */
    Player getCurrentPlayer();

    /**
     * Retourne la liste des placements possibles de tuiles
     * sur la mer
     */
    HexMap<? extends Iterable<SeaTileAction>> getSeaPlacements();

    /**
     * Retourne la liste des placements possibles de tuiles
     * sur les volcans
     */
    HexMap<? extends Iterable<VolcanoTileAction>> getVolcanoPlacements();

    /**
     * Retourne la liste des constructions possibles
     */
    HexMap<? extends Iterable<PlaceBuildingAction>> getBuildActions();

    /**
     * Retourne la liste des extensions de villages possibles
     */
    HexMap<? extends Iterable<ExpandVillageAction>> getExpandActions();

    /**
     * Réalise l'action passée en paramètre
     */
    void action(Action Action);
    void placeOnSea(SeaTileAction placement);
    void placeOnVolcano(VolcanoTileAction placement);
    void build(PlaceBuildingAction action);
    void expand(ExpandVillageAction action);
}
