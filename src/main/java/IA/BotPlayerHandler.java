package IA;

import engine.Engine;
import engine.EngineStatus;
import engine.PlayerHandler;
import engine.PlayerTurn;
import javafx.application.Platform;

public class BotPlayerHandler implements PlayerHandler {

    public static PlayerHandler.Factory factory(int branch, int depth) {
        return engine -> new BotPlayerHandler(engine, branch, depth);
    }

    private final Engine engine;
    private final int branch;
    private final int depth;
    private final Heuristics heuristics;

    public BotPlayerHandler(Engine engine, int branch, int depth) {
        this.engine = engine;
        this.branch = branch;
        this.depth = depth;
        this.heuristics = new BasicHeuristics();
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public PlayerTurn startTurn(EngineStatus.TurnStep step) {
        return Platform.isFxApplicationThread()
                ? new BotPlayerTurnFx(engine, branch, depth, heuristics, step)
                : new BotPlayerTurn(engine, branch, depth, heuristics, step);
    }
}
