package games.UserInterface.Screen;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import games.ApplicationLogic.GameManagement.EngineFactory;
import games.ApplicationLogic.GameManagement.EscapeEngine;
import games.ApplicationLogic.GameManagement.QuestEngine;
import games.ApplicationLogic.GameManagement.ShooterEngine;
import java.io.*;

public class ScreenManager {

    private final String PROJECT_DIR = System.getProperty("user.dir") + "\\game-assets\\";
    private final String CONFIGURATION_FILE = PROJECT_DIR + "configuration.txt";
    private final String DIR_LOC = "file:/" + System.getProperty("user.dir").replace("\\", "/") +
            "/game-assets/UserInterface/Screen/images/";
    private int width;
    private int height;
    static private Parent root;
    private static ScreenManager sm;
    private ScreenManager(int width, int height, Parent root){
        this.height = height;
        this.width = width;
        ScreenManager.root = root;

        BackgroundImage myBI= new BackgroundImage(new Image(DIR_LOC + "back.png",852,480,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        ScreenManager.root = new GridPane();

        ((GridPane) ScreenManager.root).setBackground(new Background(myBI));
        ((GridPane) ScreenManager.root).setPrefSize(852,480);
        ScreenManager.root.getStylesheets().add("games/UserInterface/Screen/style.css");
    }
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        ScreenManager.root = root;
    }

    public int getWidth() {

        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public static void setSm(ScreenManager sm) {
        ScreenManager.sm = sm;
    }

    public Parent update(String text) throws IOException {

        EngineFactory engineFactory = new EngineFactory();

        File file = new File(CONFIGURATION_FILE);

        // creates the file
        FileWriter writer = new FileWriter(file);
        writer.write(text.substring(5));
        writer.flush();
        writer.close();

        switch (text) {
            case "Play Shooter": {
                ShooterEngine ge = engineFactory.getShooterEngine();
                ge.gameLoop();
                root = ge.mapFactory.getShooterMap().load();
                break;
            }
            case "Play Escape": {
                EscapeEngine ge = engineFactory.getEscapeEngine();
                ge.gameLoop();
                root = ge.mapFactory.getEscapeMap().load();
                break;
            }
            case "Play Quest": {
                QuestEngine ge = engineFactory.getQuestEngine();
                ge.gameLoop();
                root = ge.mapFactory.getQuestMap().load();
                break;
            }
        }

        return root;
    }
    public static ScreenManager getInstance(int width, int height, Parent root){
        sm = new ScreenManager(width, height, root);
        return sm;
    }
}
