package map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import data.FieldType;
import data.PlayerColor;

import java.util.*;

public class MapImpl implements Map {
    final static int MAPSIZE = 100;
    Field[][] fields;

    public MapImpl() {
        fields = new Field[MAPSIZE][MAPSIZE];
    }

    @Override
    public Optional<Field> getField(Coords c) {
        return Optional.ofNullable(fields[c.getL()][c.getD()]);
    }

    @Override
    public Iterator<Coords> getSeaLevel() {
        List<Coords> coordsList = new LinkedList<>();

        for (int l = 0; l < fields.length; l++) {
            for (int d = 0; d < fields.length; d++) {
                Coords c = Coords.of(l, d);
                if (fields[l][d] == null) {
                    coordsList.add(c);
                }
            }
        }

        return coordsList.iterator();
    }

    @Override
    public Iterator<Coords> getFields() {
        List<Coords> coordsList = new LinkedList<>();

        for (int l = 0; l < fields.length; l++) {
            for (int d = 0; d < fields.length; d++) {
                Coords c = Coords.of(l, d);
                if (fields[l][d] != null) {
                    coordsList.add(c);
                }
            }
        }

        return coordsList.iterator();
    }

    @Override
    public Iterator<Coords> getVolcanos() {
        List<Coords> coordsList = new LinkedList<>();

        for (int l = 0; l < fields.length; l++) {
            for (int d = 0; d < fields.length; d++) {
                Coords c = Coords.of(l, d);
                if (fields[l][d] != null && (fields[l][d].getType() == FieldType.VOLCANO)) {
                    coordsList.add(c);
                }
            }
        }

        return coordsList.iterator();
    }

    @Override
    public Iterator<Village> getVillages(PlayerColor color) {

        //initialisation
        Partition partition = new Partition(MAPSIZE * MAPSIZE);

        for (int l = 0; l < fields.length; l++) {
            for (int d = 0; d < fields.length; d++) {
                if (fields[l][d] != null
                        && (fields[l][d].getBuilding().getBuildingCount() > 0)
                        && (fields[l][d].getBuilding().getBuildingColor() == color)) {
                    Coords current = Coords.of(l, d);
                    List<Coords> neighbors = current.getNeighbors();
                    for (Coords coords : neighbors) {
                        int nl = coords.getL();
                        int nd = coords.getD();
                        boolean hasNeighbors = false;

                        // Exploration des couleurs voisines
                        if (fields[nl][nd] != null && fields[nl][nd].getBuilding().getBuildingCount() > 0
                            && fields[nl][nd].getBuilding().getBuildingColor() == color) {

                            int currentId = l * MAPSIZE + d;
                            int neighborsId = nl * MAPSIZE + nd;

                            if (!partition.sontConnectes(currentId, neighborsId)) {
                                partition.unir(currentId, neighborsId);
                                break;
                            }
                        }
                    }
                }
            }
        }


        // Regroupement des villages par id
        ListMultimap<Integer, Coords> villagesCoords = ArrayListMultimap.create();

        for (int l = 0; l < fields.length; l++) {
            for (int d = 0; d < fields.length; d++) {
                int id = l * MAPSIZE + d;
                if (id != partition.trouver(id)) {
                    villagesCoords.put(id, Coords.of(l, d));
                }
            }
        }

        // Construction de la liste de villages
        ArrayList<Village> villages = new ArrayList<>();

        int villageId = 0;
        for (List<Coords> coords : Multimaps.asMap(villagesCoords).values()) {
            VillageImpl village = new VillageImpl(this, villageId, color, new ArrayList<>(coords));
            villages.add(village);
        }

        return villages.iterator();
    }
}
