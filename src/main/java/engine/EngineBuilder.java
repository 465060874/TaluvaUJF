package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import data.PlayerColor;
import data.StandardVolcanoTiles;
import map.Island;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Verify.verify;

/**
 * Permet de configurer la création d'une nouvelle instance d'Engine
 * Usage typique :
 *     Engine engine = EngineBuilder.allVsAll()
 *         .player(PlayerColor.RED, MonImplementationDePlayerHandler::new)
 *         .player(PlayerColor.WHITE, MonAutreImplementationDePlayerHandler::new)
 *         .build();
 *
 * Ou encore :
 *     Engine engine = EngineBuilder.teamVsTeam()
 *         .team(PlayerColor.RED, PlayerColor.WHITE, HumanPlayerHandlerFactory::new)
 *         .team(PlayerColor.BROWN, PlayerColor.YELLOW, IAPlayerHandlerFactory::new)
 *         .build();
 */
public abstract class EngineBuilder<B extends EngineBuilder> {

    final Gamemode gamemode;
    Level logLevel;
    long seed;
    Island island;
    VolcanoTileStack.Factory volcanoTileStackFactory;

    public static EngineBuilder.AllVsAll allVsAll() {
        return new EngineBuilder.AllVsAll();
    }

    public static EngineBuilder.TeamVsTeam teamVsTeam() {
        return new EngineBuilder.TeamVsTeam();
    }

    public static EngineBuilder<?> withPredefinedPlayers(
            Gamemode gamemode,
            ImmutableMap<PlayerColor, PlayerHandler.Factory> players) {
        return new WithPredefinedPlayer(gamemode, players);
    }

    private EngineBuilder(Gamemode gamemode) {
        this.gamemode = gamemode;
        this.logLevel = Level.INFO;
        this.seed = seedUniquifier() ^ System.nanoTime();
        this.island = Island.createEmpty();
        this.volcanoTileStackFactory = VolcanoTileStack.randomFactory(StandardVolcanoTiles.LIST);
    }

    abstract B self();

    public B logLevel(Level level) {
        this.logLevel = level;
        return self();
    }

    public B seed(long seed) {
        this.seed = seed;
        return self();
    }

    public B island(Island island) {
        this.island = island;
        return self();
    }

    public B tileStack(VolcanoTileStack.Factory factory) {
        this.volcanoTileStackFactory = factory;
        return self();
    }

    abstract ImmutableList<Player> createPlayers(Engine engine);

    public Engine build() {
        return new EngineImpl(this);
    }

    public static class AllVsAll extends EngineBuilder<AllVsAll> {

        private final PlayerHandler.Factory[] handlerFactories;

        private AllVsAll() {
            super(Gamemode.AllVsAll);
            this.handlerFactories = new PlayerHandler.Factory[PlayerColor.values().length];
        }

        @Override
        AllVsAll self() {
            return this;
        }

        public AllVsAll player(PlayerColor color, PlayerHandler.Factory factory) {
            checkNotNull(color);
            checkNotNull(factory);
            checkState(handlerFactories[color.ordinal()] == null, "Color already taken");

            handlerFactories[color.ordinal()] = factory;
            return this;
        }

        ImmutableList<Player> createPlayers(Engine engine) {
            List<Player> players = new ArrayList<>();

            for (PlayerColor color : PlayerColor.values()) {
                PlayerHandler.Factory factory = handlerFactories[color.ordinal()];
                if (factory == null) {
                    continue;
                }

                PlayerHandler handler = factory.create(engine);
                players.add(new Player(color, handler));
            }

            verify(players.size() > 1 && players.size() <= 4);
            Collections.shuffle(players, engine.getRandom());
            return ImmutableList.copyOf(players);
        }
    }

    public static class TeamVsTeam extends EngineBuilder {

        private PlayerColor color11;
        private PlayerColor color12;
        private PlayerHandler.Factory handlerFactory1;
        private PlayerColor color21;
        private PlayerColor color22;
        private PlayerHandler.Factory handlerFactory2;

        private TeamVsTeam() {
            super(Gamemode.TeamVsTeam);
            this.color11 = null;
            this.color12 = null;
            this.handlerFactory1 = null;
            this.color21 = null;
            this.color22 = null;
            this.handlerFactory2 = null;

        }

        @Override
        EngineBuilder self() {
            return this;
        }

        public TeamVsTeam team(PlayerColor color1, PlayerColor color2, PlayerHandler.Factory factory) {
            checkNotNull(color1);
            checkNotNull(color2);
            checkNotNull(factory);
            checkArgument(color1 != color2, "Can't use the same color");
            checkState(handlerFactory2 == null, "Can't add more than 2 team");

            if (handlerFactory1 == null) {
                color11 = color1;
                color12 = color2;
                handlerFactory1 = factory;
            }
            else {
                checkArgument(color1 != color12 && color1 != color21, "Color already taken");
                checkArgument(color2 != color12 && color2 != color21, "Color already taken");
                color21 = color1;
                color22 = color2;
                handlerFactory2 = factory;
            }

            return this;
        }

        @Override
        ImmutableList<Player> createPlayers(Engine engine) {
            verify(handlerFactory2 != null, "Not enough team");

            ImmutableList.Builder<Player> builder = ImmutableList.builder();
            PlayerHandler handler1 = handlerFactory1.create(engine);
            PlayerHandler handler2 = handlerFactory2.create(engine);
            if (engine.getRandom().nextBoolean()) {
                // Team 1 is first
                builder.add(new Player(color11, handler1));
                builder.add(new Player(color21, handler2));
                builder.add(new Player(color12, handler1));
                builder.add(new Player(color22, handler2));
            }
            else {
                // Team 2 is first
                builder.add(new Player(color21, handler2));
                builder.add(new Player(color11, handler1));
                builder.add(new Player(color22, handler2));
                builder.add(new Player(color12, handler1));
            }

            return builder.build();
        }
    }

    private static class WithPredefinedPlayer extends EngineBuilder<WithPredefinedPlayer> {
        private final ImmutableMap<PlayerColor, PlayerHandler.Factory> playersMap;

        public WithPredefinedPlayer(Gamemode gamemode, ImmutableMap<PlayerColor, PlayerHandler.Factory> playersMap) {
            super(gamemode);
            this.playersMap = playersMap;
        }

        @Override
        WithPredefinedPlayer self() {
            return this;
        }

        @Override
        ImmutableList<Player> createPlayers(Engine engine) {
            ImmutableList.Builder<Player> builder = ImmutableList.builder();
            for (Map.Entry<PlayerColor, PlayerHandler.Factory> entry : playersMap.entrySet()) {
                PlayerColor color = entry.getKey();
                PlayerHandler handler = entry.getValue().create(engine);
                builder.add(new Player(color, handler));
            }

            return builder.build();
        }
    }

    // Ripped from java.lang.Random
    private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);
    private static long seedUniquifier() {
        for (;;) {
            long current = seedUniquifier.get();
            long next = current * 181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next))
                return next;
        }
    }
}
