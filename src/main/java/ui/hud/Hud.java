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
import javafx.geometry.Insets;
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

    public Hud(Engine engine) {
        this.engine = engine;

        List<Player> players = engine.getPlayers();
        this.playerViews = new PlayerView[players.size()];
        for (int i = 0; i < players.size(); i++) {
            playerViews[i] = new PlayerView(engine, i);
            getChildren().add(playerViews[i]);
        }

        this.leftButtons = new VBox();
        IconButton left1 = new IconButton("hud/home.png");
        left1.setOnAction((e) -> System.out.println("Home !"));
        IconButton left2 = new IconButton("hud/save.png");
        left2.setOnAction((e) -> System.out.println("Save !"));
        leftButtons.getChildren().addAll(left1, left2);
        AnchorPane.setLeftAnchor(leftButtons, 0.0);

        Font font = new Font(18);
        this.textLine = new Text("");
        textLine.setFont(font);
        this.textBottom = new TextFlow(textLine);
        textBottom.setTextAlignment(TextAlignment.CENTER);
        textBottom.setPadding(new Insets(0, 0, 20, 0));
        AnchorPane.setBottomAnchor(textBottom, 0.0);

        this.tileStackCanvas = new TileStackCanvas(engine);
        AnchorPane.setRightAnchor(textBottom, 0.0);

        getChildren().addAll(leftButtons, textBottom);//, tileStackCanvas);

        widthProperty().addListener(this::resizeWidth);
        heightProperty().addListener(this::resizeHeight);
        layoutBoundsProperty().addListener(this::resizeWidth);
        layoutBoundsProperty().addListener(this::resizeHeight);

        engine.registerObserver(this);
    }

    private void resizeWidth(Observable observable) {
        AnchorPane.setLeftAnchor(textBottom, (getWidth() - textBottom.getWidth()) / 2);
        layoutChildren();
    }

    private void resizeHeight(Observable observable) {
        double y = (getHeight() - leftButtons.getHeight()) / 2;
        AnchorPane.setTopAnchor(leftButtons, y);
        AnchorPane.setTopAnchor(textBottom, y);
        layoutChildren();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onTileStackChange(boolean cancelled) {

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
        if (winners.size() == 1) {
            textLine.setText("Le joueur " + winners.get(0).getColor() + " a gagné !");
        }
        else {
            textLine.setText(winners.stream()
                    .map(Player::getColor)
                    .map(PlayerColor::name)
                    .collect(joining(", ", "Les joueurs ", " ont gagné !")));
        }
    }
}
