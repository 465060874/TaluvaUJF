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
import engine.tilestack.VolcanoTileStack;
import ia.IA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class EngineRecord {

    static class Exception extends RuntimeException {

        Exception(String message) {
            super(message);
        }

        Exception(Throwable cause) {
            super(cause);
        }
    }

    private final Gamemode gamemode;
    private final ImmutableList<PlayerColor> colors;
    private final ImmutableList<PlayerHandlerType> playerHandlerTypes;
    private final ImmutableList<VolcanoTile> tiles;
    private final ImmutableList<Action> actions;

    EngineRecord(Gamemode gamemode,
                 List<PlayerColor> colors,
                 List<PlayerHandlerType> playerHandlerTypes,
                 List<VolcanoTile> tiles,
                 List<Action> actions) {
        this.gamemode = gamemode;
        this.colors = ImmutableList.copyOf(colors);
        this.playerHandlerTypes = ImmutableList.copyOf(playerHandlerTypes);
        this.tiles = ImmutableList.copyOf(tiles);
        this.actions = ImmutableList.copyOf(actions);
    }

    public static EngineRecord load(CharSource source) {
        Gamemode gamemode;
        ImmutableList.Builder<PlayerColor> colorsBuilder = ImmutableList.builder();
        ImmutableList.Builder<PlayerHandlerType> handlersBuilder = ImmutableList.builder();
        ImmutableList.Builder<VolcanoTile> tilesBuilder = ImmutableList.builder();
        ImmutableList.Builder<Action> actionsBuilder = ImmutableList.builder();

        try (BufferedReader reader = source.openBufferedStream()) {
            gamemode = Gamemode.valueOf(reader.readLine());
            int colorsCount = Integer.valueOf(reader.readLine());
            for (int i = 0; i < colorsCount; i++) {
                colorsBuilder.add(PlayerColor.valueOf(reader.readLine()));
                handlersBuilder.add(PlayerHandlerType.valueOf(reader.readLine()));
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

            return new EngineRecord(gamemode,
                    colorsBuilder.build(),
                    handlersBuilder.build(),
                    tilesBuilder.build(),
                    actionsBuilder.build());
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
            for (int i = 0; i < colors.size(); i++) {
                writer.write(colors.get(i).name());
                writer.write('\n');
                writer.write(playerHandlerTypes.get(i).name());
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

    private void writeHandler(PlayerHandler handler, Writer writer) throws IOException {
        if (handler instanceof IA) {
            writer.write("IA.");
            writer.write(((IA) handler).name());
        }
        else if (handler.isHuman()) {
            writer.write("Human");
        }
    }

    public EngineBuilder replay(PlayerHandler humanPlayerHandler) {
        UnmodifiableIterator<Action> actionsIt = ImmutableList.copyOf(actions).iterator();
        ImmutableMap.Builder<PlayerColor, PlayerHandler> playersBuilder = ImmutableMap.builder();
        for (int i = 0; i < colors.size(); i++) {
            PlayerColor color = colors.get(i);
            PlayerHandler handler = playerHandlerTypes.get(i).getHandler(humanPlayerHandler);
            PlayerHandler wrappedHandler = new RecordPlayerHandler(actionsIt, handler);
            playersBuilder.put(color, wrappedHandler);
        }

        return EngineBuilder.withPredefinedPlayers(gamemode, playersBuilder.build())
                .tileStack(VolcanoTileStack.predefinedFactory(tiles));
    }
}
