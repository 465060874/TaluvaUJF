package engine;

import engine.action.*;
import engine.log.EngineLogger;
import engine.tilestack.VolcanoTileStack;
import map.Island;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public interface Engine {

    /**
     * Retourne le logger
     */
    EngineLogger logger();

    /**
     * Retourne la seed utilisée pour le générateur aléatoire commun
     */
    long getSeed();

    /**
     * Retourne une instance commune de la classe Random
     */
    Random getRandom();

    /**
     * Enregistre un observer qui sera notifié des changements se déroulant dans le moteur
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

    boolean canUndo();

    /**
     * Annule les derniers coups jusqu'à ce que le prédicat retourne vrai
     */
    void cancelUntil(Predicate<Engine> engine);

    boolean canRedo();

    /**
     * Rejoue les derniers coups annulées jusqu'à ce que le prédicat retourne vrai
     */
    void redoUntil(Predicate<Engine> engine);

    /**
     * Retourne le numéro de tour (un tour est un placement et une construction d'un joueur)
     */
    EngineStatus getStatus();

    /**
     * Retourne le joueur dont c'est actuellement le tour
     */
    Player getCurrentPlayer();

    /**
     * Retourne la liste des placements possibles de tuiles sur la mer
     */
    List<SeaTileAction> getSeaTileActions();

    /**
     * Retourne la liste des placements possibles de tuiles sur les volcans
     */
    List<VolcanoTileAction> getVolcanoTileActions();

    /**
     * Retourne l'ensemble des constructions possibles pour un tour exceptés
     * ceux liés au placement de tuile du même tour
     */
    List<PlaceBuildingAction> getPlaceBuildingActions();

    /**
     * Retourne la liste des constructions possibles liés au placement
     * de la tuile en paramètre
     */
    List<PlaceBuildingAction> getNewPlaceBuildingActions(TileAction action);

    /**
     * Retourne l'ensemble des extensions possibles pour un tour exceptés
     * ceux liés au placement de tuile du même tour
     */
    List<ExpandVillageAction> getExpandVillageActions();

    /**
     * Retourne la liste des extensions possibles liés au placements
     * de la tuile en paramètre
     */
    List<ExpandVillageAction> getNewExpandVillageActions(TileAction action);

    /**
     * Réalise l'action passée en paramètre
     */
    void action(Action Action);
    void placeOnSea(SeaTileAction placement);
    void placeOnVolcano(VolcanoTileAction placement);
    void build(PlaceBuildingAction action);
    void expand(ExpandVillageAction action);
}
