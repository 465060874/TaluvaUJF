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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class Hud extends AnchorPane implements EngineObserver {

    private final Engine engine;

    private final PlayerView[] playerViews;

    private final VBox leftButtons;

    private final Text textLine;
    private final TextFlow textBottom;

    private final TileStackCanvas tileStackCanvas;
    private final Text tileStackSize;
    private final VBox tileStackPane;
    private Button homeButton;

    public Hud(Engine engine) {
        this.engine = engine;

        List<Player> players = engine.getPlayers();
        this.playerViews = new PlayerView[players.size()];
        for (int i = 0; i < players.size(); i++) {
            playerViews[i] = new PlayerView(engine, i);
            getChildren().add(playerViews[i]);
        }

        this.leftButtons = new VBox();
        this.homeButton = new IconButton("ui/hud/home.png");
        homeButton.setOnAction((e) -> System.out.println("Home !"));
        IconButton left2 = new IconButton("ui/hud/save.png");
        left2.setOnAction((e) -> System.out.println("Save !"));
        leftButtons.getChildren().addAll(homeButton, left2);
        AnchorPane.setLeftAnchor(leftButtons, 0.0);

        Font font = new Font(18);
        this.textLine = new Text("");
        textLine.setFont(font);
        this.textBottom = new TextFlow(textLine);
        textBottom.setTextAlignment(TextAlignment.CENTER);
        textBottom.setPadding(new Insets(0, 0, 20, 0));
        AnchorPane.setBottomAnchor(textBottom, 0.0);

        IconButton undoButton = new IconButton("ui/hud/undo.png");
        undoButton.setOnAction(this::undo);
        this.tileStackCanvas = new TileStackCanvas(engine);
        this.tileStackSize = new Text();
        tileStackSize.setFont(new Font(20));
        TextFlow tileStackSizeFlow = new TextFlow(tileStackSize);
        tileStackSizeFlow.setTextAlignment(TextAlignment.CENTER);
        this.tileStackPane = new VBox(undoButton, tileStackCanvas, tileStackSizeFlow);
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

    private void updateText(String value) {
        textLine.setText(value);
        resizeWidth(null);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onTileStackChange(boolean cancelled) {
        tileStackCanvas.redraw();
        tileStackSize.setText(Integer.toString(engine.getVolcanoTileStack().size()));
    }

    @Override
    public void onTileStepStart(boolean cancelled) {
        for (PlayerView playerView : playerViews) {
            playerView.updateTurn();
        }
    }

    @Override
    public void onBuildStepStart(boolean cancelled) {

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
            updateText("Le joueur " + winners.get(0).getColor() + " a gagné !");
        } else {
            updateText(winners.stream()
                    .map(Player::getColor)
                    .map(PlayerColor::name)
                    .collect(joining(", ", "Les joueurs ", " ont gagné !")));
        }
    }

    public Button getHomeButton() {
        return homeButton;
    }
}
