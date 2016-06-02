
package ia;

import data.BuildingType;
import engine.Engine;
import engine.action.*;

import java.util.concurrent.atomic.AtomicBoolean;

class EasyAlgorithm implements IAAlgorithm {

    // Donnees
    private final Engine engine;
    private final AtomicBoolean cancelled;

    EasyAlgorithm(Engine engine, AtomicBoolean cancelled) {
        this.engine = engine;
        this.cancelled = cancelled;
    }

    // Jouer un coup
    @Override
    public Move play() {
        Move m =  engine.getStatus().getTurn() == 0
                ? doFirstPlay()
                : doPlay();
        engine.logger().info("[IA] Choosed move with {0} points ", m.points);
        return m;
    }

    private Move doFirstPlay() {
        TileAction seaPlacement = engine.getSeaTileActions().get(0);
        engine.action(seaPlacement);
        BuildingAction buildAction = engine.getPlaceBuildingActions().get(engine.getRandom().nextInt(2));
        return new Move(buildAction, seaPlacement, 0);
    }


    private Move doPlay() {
        int n = engine.getRandom().nextInt(3);
        Move savedMove = null;

        // Temple en premier
        if( n > 0 ){
            for (SeaTileAction tileAction : engine.getSeaTileActions()) {
                engine.placeOnSea(tileAction);
                for (PlaceBuildingAction buildingAction : engine.getPlaceBuildingActions()) {
                    if( buildingAction.getType() == BuildingType.TEMPLE )
                        return new Move( buildingAction, tileAction, 0);
                    else if( buildingAction.getType() == BuildingType.TOWER && savedMove == null )
                        savedMove = new Move( buildingAction, tileAction, 0);
                }
                engine.cancelLastStep();
            }

            // Pour tout tileAction sur la terre
            for (VolcanoTileAction tileActions  : engine.getVolcanoTileActions()) {
                engine.placeOnVolcano(tileActions);
                for (PlaceBuildingAction buildingAction : engine.getPlaceBuildingActions()) {
                    if( buildingAction.getType() == BuildingType.TEMPLE )
                        return new Move( buildingAction, tileActions, 0);
                    else if( buildingAction.getType() == BuildingType.TOWER && savedMove == null )
                        savedMove = new Move( buildingAction, tileActions, 0);
                }
                engine.cancelLastStep();
            }
        }else {
            for (SeaTileAction tileAction : engine.getSeaTileActions()) {
                engine.placeOnSea(tileAction);
                for (PlaceBuildingAction buildingAction : engine.getPlaceBuildingActions()) {
                    if( buildingAction.getType() == BuildingType.TOWER )
                        return new Move( buildingAction, tileAction, 0);
                    else if( buildingAction.getType() == BuildingType.TEMPLE && savedMove == null )
                        savedMove = new Move( buildingAction, tileAction, 0);
                }
                engine.cancelLastStep();
            }

            // Pour tout tileAction sur la terre
            for (VolcanoTileAction tileActions  : engine.getVolcanoTileActions()) {
                engine.placeOnVolcano(tileActions);
                for (PlaceBuildingAction buildingAction : engine.getPlaceBuildingActions()) {
                    if( buildingAction.getType() == BuildingType.TOWER )
                        return new Move( buildingAction, tileActions, 0);
                    else if( buildingAction.getType() == BuildingType.TEMPLE && savedMove == null )
                        savedMove = new Move( buildingAction, tileActions, 0);
                }
                engine.cancelLastStep();
            }
        }
        if( savedMove != null )
            return savedMove;
        // Sinon soit on étend soit on crée un nouveau
        n = engine.getRandom().nextInt(3);
        if( n > 0 ){
            for (SeaTileAction tileAction : engine.getSeaTileActions()) {
                engine.placeOnSea(tileAction);
                for (ExpandVillageAction buildingAction : engine.getExpandVillageActions()) {
                    return new Move( buildingAction, tileAction, 0);
                }
                for (BuildingAction buildingAction : engine.getPlaceBuildingActions()) {
                    return new Move( buildingAction, tileAction, 0);
                }
                engine.cancelLastStep();
            }

            for (VolcanoTileAction tileActions  : engine.getVolcanoTileActions()) {
                engine.placeOnVolcano(tileActions);
                for (ExpandVillageAction buildingAction : engine.getExpandVillageActions()) {
                    return new Move( buildingAction, tileActions, 0 );
                }
                for (BuildingAction buildingAction : engine.getPlaceBuildingActions()) {
                    return new Move( buildingAction, tileActions, 0 );
                }
                engine.cancelLastStep();
            }
        }else{

            for (VolcanoTileAction tileActions  : engine.getVolcanoTileActions()) {
                engine.placeOnVolcano(tileActions);
                for (BuildingAction buildingAction : engine.getPlaceBuildingActions()) {
                    return new Move(buildingAction, tileActions, 0);
                }
                for (ExpandVillageAction buildingAction : engine.getExpandVillageActions()) {
                    return new Move(buildingAction, tileActions, 0);
                }
                engine.cancelLastStep();
            }
            for (SeaTileAction tileAction : engine.getSeaTileActions()) {
                engine.placeOnSea(tileAction);
                for (BuildingAction buildingAction : engine.getPlaceBuildingActions()) {
                    return new Move( buildingAction, tileAction,  0);
                }
                for (ExpandVillageAction buildingAction : engine.getExpandVillageActions()) {
                    return new Move( buildingAction, tileAction, 0);
                }
                engine.cancelLastStep();
            }
        }
        throw new IllegalStateException("No move found for EsayPlayer");
    }
}
