package engine.action;

import com.google.common.collect.ComparisonChain;
import map.Hex;
import map.Orientation;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public abstract class TileAction implements Action<TileAction> {

    private final Hex volcanoHex;
    private final Orientation orientation;

    public TileAction(Hex volcanoHex, Orientation orientation) {
        this.volcanoHex = volcanoHex;
        this.orientation = orientation;
    }

    public Hex getVolcanoHex() {
        return volcanoHex;
    }

    public Hex getLeftHex() {
        return volcanoHex.getLeftNeighbor(orientation);
    }

    public Hex getRightHex() {
        return volcanoHex.getRightNeighbor(orientation);
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
        writer.write(orientation.name());
        writer.write('\n');
    }

    @Override
    public int compareTo(TileAction o) {
        return ComparisonChain.start()
                .compare(volcanoHex.getLine(), o.volcanoHex.getLine())
                .compare(volcanoHex.getDiag(), o.volcanoHex.getDiag())
                .compare(orientation, o.orientation)
                .result();
    }

    @Override
    public int hashCode() {
        return Objects.hash(volcanoHex, orientation);
    }

    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof TileAction)) {
            return false;
        }

        TileAction other = (TileAction) obj;
        return this.volcanoHex.equals(other.volcanoHex)
                && this.orientation == other.orientation;
    }

    @Override
    public String toString() {
        return this instanceof VolcanoTileAction
                ? "VolcanoTile(" + volcanoHex.toString() + ", " + orientation.toString() + ")"
                : "SeaTile(" + volcanoHex.toString() + ", " + orientation.toString() + ")";
    }
}
