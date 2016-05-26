package engine.action;

import data.FieldType;
import data.VolcanoTile;
import map.Hex;
import map.Orientation;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public abstract class TileAction implements Action {

    private final VolcanoTile tile;
    private final Hex volcanoHex;
    private final Orientation orientation;

    public TileAction(VolcanoTile tile, Hex volcanoHex, Orientation orientation) {
        this.tile = tile;
        this.volcanoHex = volcanoHex;
        this.orientation = orientation;
    }

    public Hex getVolcanoHex() {
        return volcanoHex;
    }

    public Hex getLeftHex() {
        return volcanoHex.getLeftNeighbor(orientation);
    }

    public FieldType getLeftFieldType() {
        return tile.getLeft();
    }

    public Hex getRightHex() {
        return volcanoHex.getRightNeighbor(orientation);
    }

    public FieldType getRightFieldType() {
        return tile.getRight();
    }

    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write(getClass().getSimpleName());
        writer.write('\n');
        writer.write(Integer.toString(volcanoHex.getLine()));
        writer.write('\n');
        writer.write(Integer.toString(volcanoHex.getDiag()));
        writer.write('\n');
        writer.write(tile.getLeft().name());
        writer.write('\n');
        writer.write(tile.getRight().name());
        writer.write('\n');
        writer.write(orientation.name());
        writer.write('\n');
    }

    @Override
    public int hashCode() {
        return Objects.hash(tile, volcanoHex, orientation);
    }


    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof TileAction)) {
            return false;
        }

        TileAction other = (TileAction) obj;
        return this.tile.equals(other.tile)
                && this.volcanoHex.equals(other.volcanoHex)
                && this.orientation == other.orientation;
    }

    @Override
    public String toString() {
        return "[" + tile.toString() + ", " +  volcanoHex.toString() + ", " + orientation.toString() + "]";
    }
}
