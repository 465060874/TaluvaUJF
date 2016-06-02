package engine.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

/**
 * Represente un coup jouable par un jouable
 * Avec la hiérarchie suivante :
 *     Action
 *         TileAction (Placement de tuile)
 *             SeaTileAction (sur la mer
 *             VolcanoTileAtion (sur un volcan)
 *         BuildingAction (Construction)
 *             PlaceBuildingAction (Par placement)
 *             ExpandVillageAction (Par extension
 */
public interface Action<T> extends Comparable<T> {

    void write(Writer writer) throws IOException;

    static Action read(BufferedReader reader) throws IOException {
        String type = reader.readLine();
        if (SeaTileAction.class.getSimpleName().equals(type)) {
            return SeaTileAction.doRead(reader);
        }
        else if (VolcanoTileAction.class.getSimpleName().equals(type)) {
            return VolcanoTileAction.doRead(reader);
        }
        else if (PlaceBuildingAction.class.getSimpleName().equals(type)) {
            return PlaceBuildingAction.doRead(reader);
        }
        else if (ExpandVillageAction.class.getSimpleName().equals(type)) {
            return ExpandVillageAction.doRead(reader);
        }
        else {
            throw new IllegalArgumentException("Unknown action type " + type);
        }
    }
}
