package ui.hud.trad;

import engine.rules.Problem;

public class ProblemText {

    public static String trad(Problem problem) {
        switch (problem) {
            case NOT_ON_SEA_OR_VOLCANO:
                return "La tuile à placer doit se trouver sur la mer, ou son volcan sur un autre volcan";
            case NOT_ALL_ON_SEA:
                return "Certaines cases ne se trouvent pas sur la mer";
            case NOT_ADJACENT_TO_COAST:
                return "La tuile n'est pas à côté de la côte";
            case NOT_ON_VOLCANO:
                return "La tuile n'est pas à côté d'un volcan";
            case SAME_VOLCANO_ORIENTATION:
                return "Impossible de placer une tuile sur une autre tuile ayant la même orientation";
            case NOT_ON_SAME_LEVEL:
                return "Certaines cases de la tuile ne se trouvent pas à la même hauteur";
            case CANT_DESTROY_TOWER_OR_TEMPLE:
                return "Impossible de détruire un temple ou une tour en placant une tuile";
            case CANT_DESTROY_VILLAGE:
                return "Impossible de détruire un village entièrement en placant une tuile";
            case CANT_BUILD_ON_SEA:
                return "Impossible de construire sur la mer";
            case CANT_BUILD_ON_VOLCANO:
                return "Impossible de construire sur une case volcan";
            case CANT_BUILD_ON_EXISTING_BUILDING:
                return "Impossible de construire sur un bâtiment existant";
            case PLACE_BUILDING_NOT_ENOUGH_BUILDINGS:
                return "Vous n'avez pas de bâtiment restant à construire de ce type";
            case HUT_TOO_HIGH:
                return "Les huttes ne peuvent être placés qu'au premier niveau";
            case TEMPLE_NOT_IN_VILLAGE_OF_3:
                return "Les temples doivent être adjacents à un village d'au moins trois cases quand elles sont placées";
            case TOWER_NOT_HIGH_ENOUGH:
                return "Les tours ne peuvent se trouver que sur des hexagones de hauteur 3 ou plus";
            case TOWER_NOT_IN_VILLAGE:
                return "Les tours doivent être adjacentes à un village quand elles sont placées";
            case NOT_A_VILLAGE:
                return "Hmmm problem (NOT_A_VILLAGE)";
            case EXPAND_NO_ADJACENT_TILE:
                return "Impossible d'étendre vers cette case";
            case EXPAND_NOT_ENOUGH_BUILDING:
                return "Vous n'avez pas suffisament de huttes pour faire cette extension de village";
        }

        return "";
    }
}
