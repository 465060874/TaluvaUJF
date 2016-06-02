package engine;

import data.PlayerColor;
import ia.IA;
import ia.IADebug;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class IaBattles {

    private static final int COUNT = 100;
    private static final int COUNT10 = 10;

    //@Test
    public void facileVsDifficile() {
        double durationSum = 0;
        Engine engine = null;
        int[] winCount = new int[]{0, 0};

        for (int i = 0; i < COUNT; i++) {
            engine = EngineBuilder.allVsAll()
                    .logLevel(Level.WARNING)
                    .player(PlayerColor.RED, IA.FACILE)
                    .player(PlayerColor.WHITE, IA.MOYEN)
                    .build();

            engine.logger().warning("* Début de la partie avec la graine {0}", Long.toString(engine.getSeed()));
            long startTime = System.nanoTime();
            engine.start();

            while (!(engine.getStatus() instanceof EngineStatus.Finished)) ;
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            durationSum += duration;

            EngineStatus.Finished statusFinish = (EngineStatus.Finished) engine.getStatus();
            for (Player player : statusFinish.getWinners()) {
                switch (player.getColor()) {
                    case RED:
                        winCount[0]++;
                        break;
                    case WHITE:
                        winCount[1]++;
                        break;
                }
            }
            engine.logger().warning("[{0}]  Fini ({1}) après {2}ms",
                    i, statusFinish.getWinReason(), duration);
        }

        engine.logger().info("Temps moyen d''execution : {0} ms", durationSum / COUNT);
        System.out.println("Nombre de Victoires : IA.FACILE : " + winCount[0] + " IA.MOYEN " + winCount[1]);
    }

    //@Test
    public void debugVsDebug() {
        long durationSum = 0;
        Engine engine = null;
        int[] winCount = new int[]{0, 0, 0};
        int error = 0;

        // Paramètres de IADebug
        int[] branchFactor = new int[]{16, 16};
        int[] depth = new int[]{1, 2};


        for (int i = 0; i < COUNT10; i++) {
            engine = EngineBuilder.allVsAll()
                    .logLevel(Level.WARNING)
                    // (DIFFICILE 16, 3)
                    .player(PlayerColor.RED, new IADebug(branchFactor[0], depth[0]))
                    .player(PlayerColor.WHITE, new IADebug(branchFactor[1], depth[1]))
                    .build();

            engine.logger().warning("* Début de la partie avec la graine {0}", Long.toString(engine.getSeed()));
            try {
                long startTime = System.nanoTime();
                engine.start();

                while (!(engine.getStatus() instanceof EngineStatus.Finished)) ;
                long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
                durationSum += duration;

                EngineStatus.Finished statusFinish = (EngineStatus.Finished) engine.getStatus();
                if (statusFinish.getWinners().size() == 2) {
                    winCount[2]++;
                }
                for (Player player : statusFinish.getWinners()) {
                    switch (player.getColor()) {
                        case RED:
                            winCount[0]++;
                            break;
                        case WHITE:
                            winCount[1]++;
                            break;
                    }
                }
                engine.logger().warning("[{0}]  Fini ({1}) après {2}ms",
                        i, statusFinish.getWinReason(), duration);
            } catch (Exception e) {
                e.getStackTrace();
                error++;
            }
        }

        engine.logger().info("Temps moyen d''execution : {0} ms", durationSum / COUNT10);
        System.out.println("Victoires :\nIA.DEBUG(" + branchFactor[0] + ", " + depth[0] + ") : " + winCount[0] +
                "\nIA.DEBUG(" + branchFactor[1] + ", " + depth[1] + ") : " + + winCount[1] +
        "\nEgalitées : " + winCount[2]);
        System.out.println("Durée Moyenne d'une partie : " + (durationSum / (winCount[0] + winCount[1] + winCount[2]) + "ms"));
        System.out.println("Erreurs : " + error);
    }

}
