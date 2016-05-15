package map;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import data.BuildingType;
import data.FieldType;
import data.PlayerColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class IslandIO {

    public static class Exception extends RuntimeException {

        private Exception(String message) {
            super(message);
        }

        private Exception(Throwable cause) {
            super(cause);
        }
    }

    private static final Splitter SPLITTER = Splitter.on(",").trimResults();
    private static final Joiner JOINER = Joiner.on(",");

    public static IslandImpl read(CharSource source) {
        IslandImpl island = new IslandImpl();

        try (BufferedReader reader = source.openBufferedStream()) {
            String line;
            while ((line = reader.readLine()) != null) {
                readHex(line, island);
            }
        }
        catch (IOException e) {
            throw new Exception(e);
        }

        return island;
    }

    private static void readHex(String hexStr, IslandImpl island) {
        Iterator<String> split = SPLITTER.split(hexStr).iterator();

        int line = Integer.valueOf(split.next());
        int diag = Integer.valueOf(split.next());

        int level = Integer.valueOf(split.next());
        FieldType fieldType = FieldType.valueOf(split.next());
        Orientation orientation = Orientation.valueOf(split.next());

        Field field;
        if (split.hasNext()) {
            BuildingType buildingType = BuildingType.valueOf(split.next());
            PlayerColor buildingColor = PlayerColor.valueOf(split.next());
            int buildingCount = Integer.valueOf(split.next());
            FieldBuilding building = new FieldBuilding(buildingType, buildingColor, buildingCount);
            field = new Field(level, fieldType, orientation, building);
        }
        else {
            field = new Field(level, fieldType, orientation);
        }

        Hex hex = Hex.at(line, diag);
        island.putHex(hex, field);
    }

    public static void write(CharSink sink, Island island) {
        try (Writer writer = sink.openBufferedStream()) {
            for (Hex hex : island.getFields()) {
                Field field = island.getField(hex);
                FieldBuilding building = field.getBuilding();

                ImmutableList.Builder<Object> builder = ImmutableList.builder()
                        .add(hex.getLine())
                        .add(hex.getDiag())
                        .add(field.getLevel())
                        .add(field.getType())
                        .add(field.getOrientation());
                if (building.getType() != BuildingType.NONE) {
                    builder.add(building.getType())
                            .add(building.getColor())
                            .add(building.getColor());
                }

                String line = JOINER.join(builder.build());
                writer.write(line);
                writer.write('\n');
            }
        }
        catch (IOException e) {
            throw new Exception(e);
        }
    }
}