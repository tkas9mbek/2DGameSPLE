package sample.UserInterface.Screen;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import sample.FileManagement.FileManager;

import java.net.URL;

/**
 * Created by Okur on 03/05/18.
 */
public class GameOverPane {
    private GridPane gridPaneGO;
    private Scene sceneGO;
    private static GameOverPane gameOverPane;
    private final URL DIR_LOC = getClass().getResource(".");
    private GameOverPane(){
        gridPaneGO = new GridPane();

        gridPaneGO.setHgap(0);
        gridPaneGO.setVgap(0);

        gridPaneGO.getColumnConstraints().add(new ColumnConstraints(10));
        gridPaneGO.getColumnConstraints().add(new ColumnConstraints(370));
        gridPaneGO.getColumnConstraints().add(new ColumnConstraints(72));
        gridPaneGO.getColumnConstraints().add(new ColumnConstraints(370));
        gridPaneGO.getRowConstraints().add(new RowConstraints(120));
        gridPaneGO.getRowConstraints().add(new RowConstraints(120));
        gridPaneGO.getRowConstraints().add(new RowConstraints(120));
        gridPaneGO.getRowConstraints().add(new RowConstraints(120));

        BackgroundImage endImage = new BackgroundImage(new Image(DIR_LOC +  "\\images\\end.png",852,480,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        final Background backg = new Background(endImage);

        gridPaneGO.setBackground(backg);
        gridPaneGO.setPrefSize(852,480);
        //gridPaneGO.setGridLinesVisible(true);

        sceneGO = new Scene(gridPaneGO,852,480);
    }

    /**
     *
     * @return this.Pane
     */
    public GridPane getPane(){ return gridPaneGO; }

    public Scene getScene(){ return sceneGO; }
    public static GameOverPane getInstance(){
        if(gameOverPane == null){
            gameOverPane = new GameOverPane();
        }
        return gameOverPane;
    }
}
