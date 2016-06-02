package ui.hud;

import data.PlayerColor;
import engine.Engine;
import engine.EngineObserver;
import engine.EngineStatus;
import engine.Player;
import engine.action.ExpandVillageAction;
import engine.action.PlaceBuildingAction;
import engine.action.SeaTileAction;
import engine.action.VolcanoTileAction;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import ui.island.Placement;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class Hud extends AnchorPane implements EngineObserver {

    private final Engine engine;
    private final Placement placement;

    private final PlayerView[] playerViews;

    private final VBox leftButtons;
    private final Button homeButton;
    private final Button saveButton;

    private final Text infoLine;
    private final Text errorLine;
    private final TextFlow textBottom;

    private final IconButton undoButton;
    private final IconButton redoButton;
    private final TileStackCanvas tileStackCanvas;
    private final Text tileStackSize;
    private final VBox tileStackPane;

    public Hud(Engine engine, Placement placement) {
        this.engine = engine;
        this.placement = placement;
        placement.initHud(this);

        List<Player> players = engine.getPlayers();
        this.playerViews = new PlayerView[players.size()];
        for (int i = 0; i < players.size(); i++) {
            playerViews[i] = new PlayerView(engine, i, placement);
            getChildren().add(playerViews[i]);
        }

        this.leftButtons = new VBox();
        this.homeButton = new IconButton("ui/hud/home.png");
        this.saveButton = new IconButton("ui/hud/save.png");
        leftButtons.getChildren().addAll(homeButton, saveButton);
        AnchorPane.setLeftAnchor(leftButtons, 0.0);

        Font font = new Font(18);

        this.infoLine = new Text("");
        infoLine.setFont(font);
        infoLine.setFill(Color.GREEN);
        this.errorLine = new Text("");
        errorLine.setFont(font);
        errorLine.setFill(Color.RED);

        this.textBottom = new TextFlow(infoLine, new Text("\n"), errorLine);
        textBottom.setTextAlignment(TextAlignment.CENTER);
        textBottom.setPadding(new Insets(0, 0, 20, 0));
        textBottom.setBackground(new Background(new BackgroundFill(
                Color.BEIGE,
                CornerRadii.EMPTY,
                Insets.EMPTY)));
        textBottom.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                BorderWidths.FULL,
                Insets.EMPTY)));
        AnchorPane.setBottomAnchor(textBottom, 0.0);

        this.undoButton = new IconButton("ui/hud/undo.png", 0.5);
        this.redoButton = new IconButton("ui/hud/redo.png", 0.5);
        undoButton.setOnAction(this::undo);
        redoButton.setOnAction(this::redo);
        HBox undoRedoPane = new HBox(undoButton, redoButton);

        this.tileStackCanvas = new TileStackCanvas(engine);
        this.tileStackSize = new Text();
        tileStackSize.setFont(new Font(20));
        TextFlow tileStackSizeFlow = new TextFlow(tileStackSize);
        tileStackSizeFlow.setTextAlignment(TextAlignment.CENTER);
        this.tileStackPane = new VBox(undoRedoPane, tileStackCanvas, tileStackSizeFlow);
        AnchorPane.setRightAnchor(tileStackPane, 0.0);

        getChildren().addAll(leftButtons, textBottom, tileStackPane);

        widthProperty().addListener(this::resizeWidth);
        heightProperty().addListener(this::resizeHeight);
        layoutBoundsProperty().addListener(this::resizeWidth);
        layoutBoundsProperty().addListener(this::resizeHeight);

        engine.registerObserver(this);

    }

    private void undo(ActionEvent event) {
        engine.cancelUntil(e -> e.getCurrentPlayer().isHuman() && e.getStatus().getStep() == EngineStatus.TurnStep.TILE);
    }

    private void redo(ActionEvent event) {
        engine.redoUntil(e -> e.getCurrentPlayer().isHuman() && e.getStatus().getStep() == EngineStatus.TurnStep.TILE);
    }

    private void resizeWidth(Observable observable) {
        AnchorPane.setLeftAnchor(textBottom, (getWidth() - textBottom.getWidth()) / 2);
        layoutChildren();
    }

    private void resizeHeight(Observable observable) {
        double y = (getHeight() - leftButtons.getHeight()) / 2;
        AnchorPane.setTopAnchor(leftButtons, y);
        AnchorPane.setTopAnchor(tileStackPane, y);
        layoutChildren();
    }

    private void updateText(Text line, String value) {
        line.setText(value);
        textBottom.setVisible(infoLine.getText().length() > 0 || errorLine.getText().length() > 0);
        resizeWidth(null);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onCancelTileStep() {
        redoButton.setDisable(!engine.canRedo());
    }

    @Override
    public void onCancelBuildStep() {
        redoButton.setDisable(!engine.canRedo());
    }

    @Override
    public void onTileStackChange() {
        tileStackCanvas.redraw();
        tileStackSize.setText(Integer.toString(engine.getVolcanoTileStack().size()));
    }

    @Override
    public void onTileStepStart() {
        undoButton.setDisable(!engine.canUndo());
        redoButton.setDisable(!engine.canRedo());
        for (PlayerView playerView : playerViews) {
            playerView.updateTurn();
        }
    }

    @Override
    public void onBuildStepStart() {
        undoButton.setDisable(!engine.canUndo());
        redoButton.setDisable(!engine.canRedo());
        tileStackCanvas.redraw();
        tileStackSize.setText(Integer.toString(engine.getVolcanoTileStack().size() - 1));
    }

    @Override
    public void onTilePlacementOnSea(SeaTileAction action) {

    }

    @Override
    public void onTilePlacementOnVolcano(VolcanoTileAction action) {

    }

    @Override
    public void onBuild(PlaceBuildingAction action) {

    }

    @Override
    public void onExpand(ExpandVillageAction action) {

    }

    @Override
    public void onEliminated(Player eliminated) {

    }

    @Override
    public void onWin(EngineStatus.FinishReason reason, List<Player> winners) {
        tileStackCanvas.redraw();
        tileStackSize.setText(Integer.toString(engine.getVolcanoTileStack().size()));
        for (PlayerView playerView : playerViews) {
            playerView.updateTurn();
        }

        if (winners.size() == 1) {
            updateText(infoLine, "Le joueur " + winners.get(0).getColor() + " a gagné !");
        } else {
            updateText(infoLine, winners.stream()
                    .map(Player::getColor)
                    .map(PlayerColor::name)
                    .collect(joining(", ", "Les joueurs ", " ont gagné !")));
        }
    }

    public Button getHomeButton() {
        return homeButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public void updateProblems() {
        updateText(errorLine, ProblemTrad.trad(placement.getProblems()));
    }
}
