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
    private final ImmutableList<PlayerHandler> handlers;
    private final ImmutableList<VolcanoTile> tiles;
    private final ImmutableList<Action> actions;

    EngineRecord(Gamemode gamemode,
                 List<PlayerColor> colors,
                 List<PlayerHandler> handlers,
                 List<VolcanoTile> tiles,
                 List<Action> actions) {
        this.gamemode = gamemode;
        this.colors = ImmutableList.copyOf(colors);
        this.handlers = ImmutableList.copyOf(handlers);
        this.tiles = ImmutableList.copyOf(tiles);
        this.actions = ImmutableList.copyOf(actions);
    }

    public static EngineRecord load(CharSource source) {
        Gamemode gamemode;
        ImmutableList.Builder<PlayerColor> colorsBuilder = ImmutableList.builder();
        ImmutableList.Builder<PlayerHandler> handlersBuilder = ImmutableList.builder();
        ImmutableList.Builder<VolcanoTile> tilesBuilder = ImmutableList.builder();
        ImmutableList.Builder<Action> actionsBuilder = ImmutableList.builder();

        try (BufferedReader reader = source.openBufferedStream()) {
            gamemode = Gamemode.valueOf(reader.readLine());
            int colorsCount = Integer.valueOf(reader.readLine());
            for (int i = 0; i < colorsCount; i++) {
                colorsBuilder.add(PlayerColor.valueOf(reader.readLine()));
                handlersBuilder.add(readHandler(reader.readLine()));
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

    private static PlayerHandler readHandler(String handlerStr) {
        // TODO
        return PlayerHandler.dummy();
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
        ImmutableMap.Builder<PlayerColor, PlayerHandler> playersBuilder = ImmutableMap.builder();
        PlayerHandler playerHandlerFactory = new RecordPlayerHandler(actionsIt);
        for (PlayerColor color : colors) {
            playersBuilder.put(color, playerHandlerFactory);
        }

        return EngineBuilder.withPredefinedPlayers(gamemode, playersBuilder.build())
                .tileStack(VolcanoTileStack.predefinedFactory(tiles))
                .build();
    }

    private static class RecordPlayerHandler implements PlayerHandler {

        private final Iterator<Action> actions;

        private RecordPlayerHandler(Iterator<Action> actions) {
            this.actions = actions;
        }

        @Override
        public boolean isHuman() {
            return false;
        }

        @Override
        public PlayerTurn startTurn(Engine engine, EngineStatus.TurnStep step) {
            Action nextAction = actions.next();
            return step == EngineStatus.TurnStep.TILE
                ? new RecordPlayerTurn(engine, nextAction, actions.next())
                : new RecordPlayerTurn(engine, nextAction);
        }
    }

    private static class RecordPlayerTurn implements PlayerTurn {

        private final Engine engine;
        private final Action[] actions;

        public RecordPlayerTurn(Engine engine, Action... actions) {
            this.engine = engine;
            this.actions = actions;

            for (Action action : actions) {
                engine.action(action);
            }
        }

        @Override
        public void cancel() {
            throw new IllegalStateException();
        }
    }
}
