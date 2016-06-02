package engine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import data.PlayerColor;
import data.StandardVolcanoTiles;
import engine.tilestack.VolcanoTileStack;
import map.Island;

import java.util.*;
import java.util.logging.Level;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Verify.verify;

/**
 * Permet de configurer la création d'une nouvelle instance d'Engine
 * Usage typique :
 *     Engine engine = EngineBuilder.allVsAll()
 *         .player(PlayerColor.RED, monImplementationDePlayerHandler)
 *         .player(PlayerColor.WHITE, monAutreImplementationDePlayerHandler)
 *         .build();
 *
 * Ou encore :
 *     Engine engine = EngineBuilder.teamVsTeam()
 *         .team(PlayerColor.RED, PlayerColor.WHITE, HumanPlayerHandler)
 *         .team(PlayerColor.BROWN, PlayerColor.YELLOW, IAPlayerHandler)
 *         .build();
 */
public abstract class EngineBuilder<B extends EngineBuilder> {

    private static final Random SEEDER = new Random();

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
            ImmutableMap<PlayerColor, PlayerHandler> players) {
        return new WithPredefinedPlayer(gamemode, players);
    }

    private EngineBuilder(Gamemode gamemode) {
        this.gamemode = gamemode;
        this.logLevel = Level.INFO;
        this.seed = SEEDER.nextLong();
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

        private final PlayerHandler[] playerHandlers;

        private AllVsAll() {
            super(Gamemode.AllVsAll);
            this.playerHandlers = new PlayerHandler[PlayerColor.values().length];
        }

        @Override
        AllVsAll self() {
            return this;
        }

        public AllVsAll player(PlayerColor color, PlayerHandler handler) {
            checkNotNull(color);
            checkNotNull(handler);
            checkState(playerHandlers[color.ordinal()] == null, "Color already taken");

            playerHandlers[color.ordinal()] = handler;
            return this;
        }

        ImmutableList<Player> createPlayers(Engine engine) {
            List<Player> players = new ArrayList<>();

            for (PlayerColor color : PlayerColor.values()) {
                PlayerHandler handler = playerHandlers[color.ordinal()];
                if (handler == null) {
                    continue;
                }

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
        private PlayerHandler playerHandler1;
        private PlayerColor color21;
        private PlayerColor color22;
        private PlayerHandler playerHandler2;

        private TeamVsTeam() {
            super(Gamemode.TeamVsTeam);
            this.color11 = null;
            this.color12 = null;
            this.playerHandler1 = null;
            this.color21 = null;
            this.color22 = null;
            this.playerHandler2 = null;

        }

        @Override
        EngineBuilder self() {
            return this;
        }

        public TeamVsTeam team(PlayerColor color1, PlayerColor color2, PlayerHandler playerHandler) {
            checkNotNull(color1);
            checkNotNull(color2);
            checkNotNull(playerHandler);
            checkArgument(color1 != color2, "Can't use the same color");
            checkState(playerHandler2 == null, "Can't add more than 2 team");

            if (playerHandler1 == null) {
                color11 = color1;
                color12 = color2;
                playerHandler1 = playerHandler;
            }
            else {
                checkArgument(color1 != color12 && color1 != color21, "Color already taken");
                checkArgument(color2 != color12 && color2 != color21, "Color already taken");
                color21 = color1;
                color22 = color2;
                playerHandler2 = playerHandler;
            }

            return this;
        }

        @Override
        ImmutableList<Player> createPlayers(Engine engine) {
            verify(playerHandler2 != null, "Not enough team");

            ImmutableList.Builder<Player> builder = ImmutableList.builder();
            if (engine.getRandom().nextBoolean()) {
                // Team 1 is first
                builder.add(new Player(color11, playerHandler1));
                builder.add(new Player(color21, playerHandler2));
                builder.add(new Player(color12, playerHandler1));
                builder.add(new Player(color22, playerHandler2));
            }
            else {
                // Team 2 is first
                builder.add(new Player(color21, playerHandler2));
                builder.add(new Player(color11, playerHandler1));
                builder.add(new Player(color22, playerHandler2));
                builder.add(new Player(color12, playerHandler1));
            }

            return builder.build();
        }
    }

    private static class WithPredefinedPlayer extends EngineBuilder<WithPredefinedPlayer> {
        private final ImmutableMap<PlayerColor, PlayerHandler> playersMap;

        public WithPredefinedPlayer(Gamemode gamemode, ImmutableMap<PlayerColor, PlayerHandler> playersMap) {
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
            for (Map.Entry<PlayerColor, PlayerHandler> entry : playersMap.entrySet()) {
                PlayerColor color = entry.getKey();
                PlayerHandler handler = entry.getValue();
                builder.add(new Player(color, handler));
            }

            return builder.build();
        }
    }
}
