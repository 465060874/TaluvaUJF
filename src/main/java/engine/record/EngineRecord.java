package engine.record;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import data.FieldType;
import data.PlayerColor;
import data.VolcanoTile;
import engine.*;
import engine.action.Action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

public class EngineRecord {

    public static class Exception extends RuntimeException {

        private Exception(String message) {
            super(message);
        }

        private Exception(Throwable cause) {
            super(cause);
        }
    }

    private final Gamemode gamemode;
    private final ImmutableList<PlayerColor> colors;
    private final ImmutableList<VolcanoTile> tiles;
    private final ImmutableList<Action> actions;

    EngineRecord(Gamemode gamemode, List<PlayerColor> colors, List<VolcanoTile> tiles,
                 List<Action> actions) {
        this.gamemode = gamemode;
        this.colors = ImmutableList.copyOf(colors);
        this.tiles = ImmutableList.copyOf(tiles);
        this.actions = ImmutableList.copyOf(actions);
    }

    public static EngineRecord load(CharSource source) {
        Gamemode gamemode;
        ImmutableList.Builder<PlayerColor> colorsBuilder = ImmutableList.builder();
        ImmutableList.Builder<VolcanoTile> tilesBuilder = ImmutableList.builder();
        ImmutableList.Builder<Action> actionsBuilder = ImmutableList.builder();

        try (BufferedReader reader = source.openBufferedStream()) {
            gamemode = Gamemode.valueOf(reader.readLine());
            int colorsCount = Integer.valueOf(reader.readLine());
            for (int i = 0; i < colorsCount; i++) {
                colorsBuilder.add(PlayerColor.valueOf(reader.readLine()));
            }

            int tilesCount = Integer.valueOf(reader.readLine());
            for (int i = 0; i < tilesCount; i++) {
                FieldType left = FieldType.valueOf(reader.readLine());
                FieldType right = FieldType.valueOf(reader.readLine());
                tilesBuilder.add(new VolcanoTile(left, right));
            }

            int actionsCount = Integer.valueOf(reader.readLine());
            for (int i = 0; i < actionsCount; i++) {
                actionsBuilder.add(Action.read(reader));
            }

            return new EngineRecord(gamemode, colorsBuilder.build(), tilesBuilder.build(), actionsBuilder.build());
        }
        catch (IOException e) {
            throw new Exception(e);
        }
    }

    public void save(CharSink sink) {
        try (Writer writer = sink.openBufferedStream()) {
            writer.write(gamemode.name());
            writer.write('\n');

            writer.write(Integer.toString(colors.size()));
            writer.write('\n');
            for (PlayerColor color : colors) {
                writer.write(color.name());
                writer.write('\n');
            }

            writer.write(Integer.toString(tiles.size()));
            writer.write('\n');
            for (VolcanoTile tile : tiles) {
                writer.write(tile.getLeft().name());
                writer.write('\n');
                writer.write(tile.getRight().name());
                writer.write('\n');
            }

            writer.write(Integer.toString(actions.size()));
            writer.write('\n');
            for (Action action : actions) {
                action.write(writer);
            }
        }
        catch (IOException e) {
            throw new Exception(e);
        }
    }

    public Engine replay() {
        UnmodifiableIterator<Action> actionsIt = ImmutableList.copyOf(actions).iterator();
        ImmutableMap.Builder<PlayerColor, PlayerHandler.Factory> playersBuilder = ImmutableMap.builder();
        PlayerHandler.Factory playerHandlerFactory = (engine) -> new RecordPlayerHandler(engine, actionsIt);
        for (PlayerColor color : colors) {
            playersBuilder.put(color, playerHandlerFactory);
        }

        return EngineBuilder.withPredefinedPlayers(gamemode, playersBuilder.build())
                .tileStack(VolcanoTileStack.predefinedFactory(tiles))
                .build();
    }

    private static class RecordPlayerHandler implements PlayerHandler {

        private final Engine engine;
        private final Iterator<Action> actions;

        private RecordPlayerHandler(Engine engine, Iterator<Action> actions) {
            this.engine = engine;
            this.actions = actions;
        }

        @Override
        public void startTileStep() {
            engine.action(actions.next());
        }

        @Override
        public void startBuildStep() {
            engine.action(actions.next());
        }

        @Override
        public void cancel() {
            throw new UnsupportedOperationException();
        }
    }
}
