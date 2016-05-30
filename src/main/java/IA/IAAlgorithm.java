package IA;

import engine.Engine;

import java.util.concurrent.atomic.AtomicBoolean;

public interface IAAlgorithm {
    Move play();

    interface Factory {
        IAAlgorithm create(Engine engine, AtomicBoolean cancelled);
    }
}
