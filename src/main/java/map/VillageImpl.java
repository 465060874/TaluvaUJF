package map;

import data.BuildingType;
import data.PlayerColor;

import java.util.Iterator;
import java.util.List;

public class VillageImpl implements Village {

    Map map;
    private int id;
    private PlayerColor color;
    List<Coords> buildingCoords;

    VillageImpl(Map map, int id, PlayerColor color, List<Coords> buildingCoords) {
        this.id = id;
        this.buildingCoords = buildingCoords;
        this.map = map;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Iterator<Coords> getCoords() {
        return buildingCoords.iterator();
    }

    @Override
    public boolean hasTemple() {
        for (Coords buildingCoord : buildingCoords) {
            if (map.getField(buildingCoord).isPresent()) {
                if(map.getField(buildingCoord).get().getBuilding().getBuildingType() == BuildingType.TEMPLE) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean hasTower() {
        for (Coords buildingCoord : buildingCoords) {
            if (map.getField(buildingCoord).isPresent()) {
                if(map.getField(buildingCoord).get().getBuilding().getBuildingType() == BuildingType.TOWER) {
                    return true;
                }
            }
        }

        return false;
    }

    public PlayerColor getColor() {
        return color;
    }
}
