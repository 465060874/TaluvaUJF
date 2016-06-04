package ui.hud;

import engine.Engine;
import engine.EngineStatus;
import engine.Player;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import ui.hud.trad.PlayerText;
import ui.hud.trad.ProblemText;
import ui.island.Placement;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class Hud extends AnchorPane {

    private static final double TEXT_VISIBLE_HEIGHT = 80;
    private static final double TEXT_HIDDEN_HEIGHT = 10;

    private final Engine engine;
    private final boolean versusIa;
    private final Placement placement;

    private final Timeline playerTimeline;
    private final PlayerView[] playerViews;

    private final Button homeButton;
    private final Button saveButton;

    private final Text infoLine;
    private final Text errorLine;
    private final IconButton textUpDownButton;
    private final IconButton rulesButton;
    private boolean textUp;
    private final TextFlow textBottom;

    private final IconButton undoButton;
    private final IconButton redoButton;
    private final TileStackCanvas tileStackCanvas;

    private final VBox leftButtons;
    private final HBox textBox;
    private final VBox rightPane;

    private final IconButton showForbidenHexButton;
    private final IconButton showForbiddenBuildingsButton;

    public Hud(Engine engine, Placement placement) {
        this.engine = engine;
        this.versusIa = engine.getPlayers().size() == 2
                && !engine.getPlayers().get(0).isHuman()
                || !engine.getPlayers().get(1).isHuman();

        this.placement = placement;
        placement.initHud(this);

        this.playerTimeline = new Timeline(new KeyFrame(Duration.millis(50), this::playerTick));
        playerTimeline.setCycleCount(Animation.INDEFINITE);
        playerTimeline.play();

        List<Player> players = engine.getPlayers();
        this.playerViews = new PlayerView[players.size()];
        for (int i = 0; i < players.size(); i++) {
            playerViews[i] = new PlayerView(engine, i, placement);
            getChildren().add(playerViews[i]);
        }

        this.leftButtons = new VBox();
        this.homeButton = new IconButton("ui/hud/home.png", 0.7);
        this.saveButton = new IconButton("ui/hud/save.png", 0.7);

        final Tooltip saveToolTip = new Tooltip();
        saveToolTip.setText("Sauvegarder");
        saveButton.setTooltip(saveToolTip);

        final Tooltip homeToolTip = new Tooltip();
        homeToolTip.setText("Retour au menu");
        homeButton.setTooltip(homeToolTip);

        leftButtons.getChildren().addAll(homeButton, saveButton);
        AnchorPane.setLeftAnchor(leftButtons, 0.0);

        Font font = new Font(18);

        this.infoLine = new Text("Info");
        infoLine.setFont(font);
        infoLine.setFill(Color.GREEN);
        this.errorLine = new Text("");
        errorLine.setFont(font);
        errorLine.setFill(Color.RED);

        this.textBottom = new TextFlow(infoLine, new Text("\n"), errorLine);
        textBottom.setPadding(new Insets(10, 0, 0, 0));
        textBottom.setTextAlignment(TextAlignment.CENTER);
        textBottom.setBackground(new Background(new BackgroundFill(
                Color.BEIGE,
                new CornerRadii(0.2, 0.2, 0, 0, true),
                Insets.EMPTY)));
        textBottom.setBorder(new Border(new BorderStroke(
                Color.BLACK,
                BorderStrokeStyle.SOLID,
                new CornerRadii(0.2, 0.2, 0, 0, true),
                BorderWidths.DEFAULT,
                Insets.EMPTY)));
        textBottom.setMinWidth(500);
        textBottom.setPrefWidth(500);
        textBottom.setMaxWidth(500);
        textBottom.setMinHeight(TEXT_VISIBLE_HEIGHT);
        textBottom.setPrefHeight(TEXT_VISIBLE_HEIGHT);
        textBottom.setMaxHeight(TEXT_VISIBLE_HEIGHT);

        this.showForbidenHexButton = new IconButton("ui/hud/forbiddenHexagonDisabled.png", 0.5);
        final Tooltip showForbiddenHexToolTip = new Tooltip();
        showForbiddenHexToolTip.setText("Afficher les placements de tuiles interdits");
        showForbidenHexButton.setTooltip(showForbiddenHexToolTip);
        showForbidenHexButton.setOnAction(this::drawForbiddenPlacement);

        this.showForbiddenBuildingsButton = new IconButton("ui/hud/forbiddenHutDisabled.png", 0.5);
        final Tooltip showForbiddenBuildingsToolTip = new Tooltip();
        showForbiddenBuildingsToolTip.setText("Afficher les placements de Batiments interdits");
        showForbiddenBuildingsButton.setTooltip(showForbiddenBuildingsToolTip);
        showForbiddenBuildingsButton.setOnAction(this::drawForbiddenBuildings);

        this.rulesButton = new IconButton("ui/hud/rules.png", 0.5);
        final Tooltip rulesToolTip = new Tooltip();
        rulesToolTip.setText("Règles du jeu");
        rulesButton.setTooltip(rulesToolTip);
        rulesButton.setOnAction(this::rules);

        this.textUpDownButton = new IconButton("ui/hud/down.png", 0.5);
        textUpDownButton.setOnAction(this::textUpDown);
        textUp = true;
        VBox forbiddenBox = new VBox(showForbiddenBuildingsButton, showForbidenHexButton);
        Node rulesTextUpDownBox = new VBox(rulesButton, textUpDownButton);
        this.textBox = new HBox(forbiddenBox, textBottom, rulesTextUpDownBox);
        textBox.setAlignment(Pos.BOTTOM_LEFT);
        AnchorPane.setBottomAnchor(textBox, 0.0);

        this.undoButton = new IconButton("ui/hud/undo.png", 0.5);
        this.redoButton = new IconButton("ui/hud/redo.png", 0.5);

        final Tooltip cancelTooltip = new Tooltip();
        cancelTooltip.setText("Annuler");
        undoButton.setTooltip(cancelTooltip);

        final Tooltip redoTooltip = new Tooltip();
        redoTooltip.setText("Refaire");
        redoButton.setTooltip(redoTooltip);

        undoButton.setOnAction(this::undo);
        redoButton.setOnAction(this::redo);
        HBox undoRedoPane = new HBox(undoButton, redoButton);
        undoRedoPane.setAlignment(Pos.CENTER);

        this.tileStackCanvas = new TileStackCanvas(engine);
        this.rightPane = new VBox(undoRedoPane, tileStackCanvas);
        AnchorPane.setRightAnchor(rightPane, 0.0);

        getChildren().addAll(leftButtons, textBox, rightPane);

        widthProperty().addListener(this::resizeWidth);
        heightProperty().addListener(this::resizeHeight);
        layoutBoundsProperty().addListener(this::resizeWidth);
        layoutBoundsProperty().addListener(this::resizeHeight);

        engine.registerObserver(new EngineObserver());

    }

    private void playerTick(ActionEvent event) {
        for (PlayerView playerView : playerViews) {
            playerView.tick();
        }
    }

    private void drawForbiddenBuildings(ActionEvent actionEvent){
        if (placement.changeForbiddenBuildingsDraw()) {
            showForbiddenBuildingsButton.updateImage("ui/hud/forbiddenHut.png", 0.5);
        } else {
            showForbiddenBuildingsButton.updateImage("ui/hud/forbiddenHutDisabled.png", 0.5);
        }
    }

    private void drawForbiddenPlacement(ActionEvent actionEvent) {
        if (placement.changeForbiddenPlacementDraw()) {
            showForbidenHexButton.updateImage("ui/hud/forbiddenHexagon.png", 0.5);
        } else {
            showForbidenHexButton.updateImage("ui/hud/forbiddenHexagonDisabled.png", 0.5);
        }
    }

    private void rules(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Règles");
        alert.setHeaderText("Pas encore implementé");
        alert.setContentText("Ici devrait s'afficher les régles du jeu");
        alert.showAndWait();
    }

    private void textUpDown(ActionEvent actionEvent) {
        this.textUp = !textUp;
        updateTextUpDown();
    }

    private void updateTextUpDown() {
        rulesButton.setVisible(textUp);
        showForbidenHexButton.setVisible(textUp);
        showForbiddenBuildingsButton.setVisible(textUp);

        if (textUp) {
            textUpDownButton.updateImage("ui/hud/down.png", 0.5);
            textBottom.getChildren().setAll(infoLine, new Text("\n"), errorLine);
            textBottom.setMinHeight(TEXT_VISIBLE_HEIGHT);
            textBottom.setPrefHeight(TEXT_VISIBLE_HEIGHT);
            textBottom.setMaxHeight(TEXT_VISIBLE_HEIGHT);
            AnchorPane.setBottomAnchor(textBox, 0.0);
        }
        else {
            textUpDownButton.updateImage("ui/hud/up.png", 0.5);
            textBottom.getChildren().setAll();
            textBottom.setMinHeight(TEXT_HIDDEN_HEIGHT);
            textBottom.setPrefHeight(TEXT_HIDDEN_HEIGHT);
            textBottom.setMaxHeight(TEXT_HIDDEN_HEIGHT);
            AnchorPane.setBottomAnchor(textBox, 0.0);
        }
    }

    private boolean undoRedoPredicate(Engine engine) {
        return engine.getCurrentPlayer().isHuman() && engine.getStatus().getStep() == EngineStatus.TurnStep.TILE;
    }

    private void undo(ActionEvent event) {
        engine.cancelUntil(this::undoRedoPredicate);
    }

    private void redo(ActionEvent event) {
        engine.redoUntil(this::undoRedoPredicate);
    }

    private void resizeWidth(Observable observable) {
        AnchorPane.setLeftAnchor(textBox, (getWidth() - textBottom.getWidth()) / 2);
        layoutChildren();
    }

    private void resizeHeight(Observable observable) {
        double y = (getHeight() - leftButtons.getHeight()) / 2;
        AnchorPane.setTopAnchor(leftButtons, y);
        AnchorPane.setTopAnchor(rightPane, y);
        layoutChildren();
    }

    private void updateText(Text line, String value) {
        line.setText(value);
        resizeWidth(null);
    }

    public Button getHomeButton() {
        return homeButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public void updateProblems() {
        updateText(errorLine, ProblemText.trad(placement.getProblem()));
    }

    private class EngineObserver extends engine.EngineObserver.Dummy {

        private void updateUndoRedo() {
            undoButton.setDisable(!engine.canUndo());
            redoButton.setDisable(!engine.canRedo());
        }

        @Override
        public void onCancelTileStep() {
            updateUndoRedo();
        }

        @Override
        public void onCancelBuildStep() {
            updateUndoRedo();
        }

        @Override
        public void onRedoTileStep() {
            updateUndoRedo();
        }

        @Override
        public void onRedoBuildStep() {
            updateUndoRedo();
        }

        @Override
        public void onTileStackChange() {
            tileStackCanvas.redraw();
        }

        @Override
        public void onTileStepStart() {
            tileStackCanvas.redraw();
            updateUndoRedo();
            for (PlayerView playerView : playerViews) {
                playerView.updateTurn();
            }

            if (versusIa) {
                if (engine.getCurrentPlayer().isHuman()) {
                    updateText(infoLine, "C'est à votre tour de placer une tuile");
                } else {
                    updateText(infoLine, "Placement de la tuile");
                }
            } else {
                String playerText = PlayerText.of(engine.getCurrentPlayer().getColor());
                updateText(infoLine, "C'est au tour du joueur " + playerText + " de placer une tuile");
            }
        }

        @Override
        public void onBuildStepStart() {
            updateUndoRedo();
            tileStackCanvas.redraw();

            if (versusIa) {
                if (engine.getCurrentPlayer().isHuman()) {
                    updateText(infoLine, "C'est à votre tour de construire");
                } else {
                    updateText(infoLine, "Construction");
                }
            } else {
                String playerText = PlayerText.of(engine.getCurrentPlayer().getColor());
                updateText(infoLine, "C'est au tour du joueur " + playerText + " de construire");
            }
        }

        @Override
        public void onWin(EngineStatus.Finished finished) {
            tileStackCanvas.redraw();
            for (PlayerView playerView : playerViews) {
                playerView.updateTurn();
            }

            List<Player> winners = finished.getWinners();
            if (winners.size() == 1) {
                updateText(infoLine, "Le joueur " + PlayerText.of(winners.get(0).getColor()) + " a gagné !");
            } else {
                updateText(infoLine, winners.stream()
                        .map(Player::getColor)
                        .map(PlayerText::of)
                        .collect(joining(", ", "Les joueurs ", " ont gagné !")));
            }

            textUp = true;
            updateTextUpDown();
        }
    }
}
