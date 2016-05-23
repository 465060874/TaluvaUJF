package map;

import com.google.common.collect.SetMultimap;
import data.FieldType;
import data.PlayerColor;

import java.util.Set;

public interface Village {

    /**
     * Retourne la couleur des batiments composant
     * ce village
     */
    PlayerColor getColor();

    /**
     * Retourne l'ensemble des hexagones dont
     * les batiments composent ce village
     */
    Set<Hex> getHexes();

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
    SetMultimap<FieldType, Hex> getExpandableHexes();
}