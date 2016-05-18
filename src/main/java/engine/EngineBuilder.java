package engine;

import data.PlayerColor;
import data.StandardVolcanoTiles;
import map.Island;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Verify.verify;

/**
 * Permet de configurer la création d'une nouvelle instance d'Engine
 * Usage typique :
 *     Engine engine = new EngineBuilder()
 *         .addPlayer(PlayerColor.RED, engine -> new MonImplementationDePlayerHandler(engine))
 *         .addPlayer(PlayerColor.WHITE, engine -> new MonAutreImplementationDePlayerHandler(engine))
 *         .build();
 *
 * Ou encore :
 *     HumanPlayerHandlerFactory humanFactory = new HumanPlayerHandlerFactory();
 *     IAPlayerHandlerFactory iaFactory = new IAPlayerHandlerFactory();
 *     Engine engine = new EngineBuilder()
 *         .gamemode(Gamemode.TeamVsTeam)
 *         .player(PlayerColor.RED, humanFactory)
 *         .player(PlayerColor.WHITE, humanFactory)
 *         .player(PlayerColor.BROWN, iaFactory)
 *         .player(PlayerColor.YELLOW, iaFactory)
 *         .build();
 */
public class EngineBuilder {

    long seed;
    Gamemode gamemode;
    Island island;
    VolcanoTileStack.Factory volcanoTileStackFactory;
    final Map<PlayerColor, PlayerHandler.Factory> players;

    public EngineBuilder() {
        this.seed = seedUniquifier() ^ System.nanoTime();
        this.gamemode = Gamemode.TwoPlayer;
        this.island = Island.createEmpty();
        this.volcanoTileStackFactory = VolcanoTileStack.randomFactory(StandardVolcanoTiles.LIST);
        this.players = new EnumMap<>(PlayerColor.class);
    }

    public EngineBuilder seed(long seed) {
        this.seed = seed;
        return this;
    }

    public EngineBuilder gamemode(Gamemode gamemode) {
        this.gamemode = gamemode;
        return this;
    }

    public EngineBuilder island(Island island) {
        this.island = island;
        return this;
    }

    public EngineBuilder tileStack(VolcanoTileStack.Factory factory) {
        this.volcanoTileStackFactory = factory;
        return this;
    }

    public EngineBuilder player(PlayerColor color, PlayerHandler.Factory handlerFactory) {
        this.players.put(color, handlerFactory);
        return this;
    }

    public Engine build() {
        verify(gamemode.getPlayerCount() == players.size());
        return new EngineImpl(this);
    }

    private static long seedUniquifier() {
        for (;;) {
            long current = seedUniquifier.get();
            long next = current * 181783497276652981L;
            if (seedUniquifier.compareAndSet(current, next))
                return next;
        }
    }

    private static final AtomicLong seedUniquifier
            = new AtomicLong(8682522807148012L);
}
