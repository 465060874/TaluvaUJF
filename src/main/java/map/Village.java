package map;

import com.google.common.collect.ListMultimap;
import data.FieldType;
import data.PlayerColor;

public interface Village {

    /**
     * Retourne la couleur des batiments composant
     * ce village
     */
    PlayerColor getColor();

    /**
     * Retourne le nombre d'hexagones sur lesquelles
     * ce village s'étend
     */
    int getHexSize();

    /**
     * Retourne l'ensemble des hexagones dont
     * les batiments composent ce village
     */
    Iterable<Hex> getHexes();

    /**
     * Indique si ce village possède un temple
     */
    boolean hasTemple();

    /**
     * Indique si ce village possède une tour
     */
    boolean hasTower();

    /**
     * Retourne l'ensemble des hexagones sur lequelles
     * ce village peut s'étendre classés par type de champs
     */
    ListMultimap<FieldType, Hex> getExpandableHexes();
}