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
    HexMap<? extends Iterable<SeaPlacement>> getSeaPlacements();

    /**
     * Retourne la liste des placements possibles de tuiles
     * sur les volcans
     */
    HexMap<? extends Iterable<VolcanoPlacement>> getVolcanoPlacements();

    /**
     * Retourne la liste des constructions possibles
     */
    HexMap<? extends Iterable<BuildAction>> getBuildActions();

    /**
     * Retourne la liste des extensions de villages possibles
     */
    HexMap<? extends Iterable<ExpandAction>> getExpandActions();

    /**
     * Réalise le placement passé en paramètre
     */
    void place(Placement placement);
    void placeOnSea(SeaPlacement placement);
    void placeOnVolcano(VolcanoPlacement placement);
    void action(Action action);
    void build(BuildAction action);
    void expand(ExpandAction action);
}
