package engine.tilestack;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import data.FieldType;
import data.VolcanoTile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import static engine.tilestack.VolcanoTileStack.predefinedFactory;

class VolcanoTileStackIO {

    static VolcanoTileStack.Factory read(CharSource source) {
        try (BufferedReader reader = source.openBufferedStream()) {
            ImmutableList.Builder<VolcanoTile> builder = ImmutableList.builder();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(" ");
                FieldType left = readFieldType(split[0]);
                FieldType right = readFieldType(split[1]);
                builder.add(new VolcanoTile(left, right));
            }

            return predefinedFactory(builder.build().reverse());
        }
        catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    private static FieldType readFieldType(String str) {
        switch (str) {
            case "M": return FieldType.ROCK;
            case "P": return FieldType.CLEARING;
            case "F": return FieldType.JUNGLE;
            case "S": return FieldType.SAND;
            case "L": return FieldType.LAKE;
        }

        throw new IllegalStateException();
    }

    static void write(CharSink sink, ImmutableList<VolcanoTile> tiles) {
        try (Writer writer = sink.openBufferedStream()) {
            for (VolcanoTile tile : tiles.reverse()) {
                String leftStr = writeFieldType(tile.getLeft());
                String rightStr = writeFieldType(tile.getRight());
                writer.write(leftStr);
                writer.write(" ");
                writer.write(rightStr);
                writer.write("\n");
            }
        }
        catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    private static String writeFieldType(FieldType fieldType) {
        switch (fieldType) {
            case ROCK:     return "M";
            case CLEARING: return "P";
            case JUNGLE:   return "F";
            case SAND:     return "S";
            case LAKE:     return "L";
        }

        throw new IllegalStateException();
    }
}
