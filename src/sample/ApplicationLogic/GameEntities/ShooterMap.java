package sample.ApplicationLogic.GameEntities;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.canvas.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sample.UserInterface.Screen.GameEndPane;
import sample.UserInterface.Screen.Main;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ShooterMap extends AbstractMap {
    private int score;
    private Text head;
    private final String FIRST_LEVEL_BACKGROUND_IMAGE = System.getProperty("user.dir") + "\\src\\sample\\ApplicationLogic\\GameEntities\\images\\dark-castle.png";
    private final String SECOND_LEVEL_BACKGROUND_IMAGE = System.getProperty("user.dir") + "\\src\\sample\\ApplicationLogic\\GameEntities\\images\\old-castle.jpg";
    private final String THIRD_LEVEL_BACKGROUND_IMAGE = System.getProperty("user.dir") + "\\src\\sample\\ApplicationLogic\\GameEntities\\images\\destroyed-castle.jpg";
    private final String[] backgroundImages = {FIRST_LEVEL_BACKGROUND_IMAGE, SECOND_LEVEL_BACKGROUND_IMAGE, THIRD_LEVEL_BACKGROUND_IMAGE};
    private static int mapLevel;
    private int deadCount;
    private Thread t;
    private int totalCountOfEnemies;
    private BackgroundImage backgroundImage;
    private ObjectRandomLocationManager locationManager;
    private Mage mage;

    public void setScore(int score) {
        this.score += score;
    }

    public ShooterMap() throws FileNotFoundException {
        super();
        mapLevel = 1;
        root = new GridPane();
        gameObjects = new ArrayList<>();
        locationManager = new ObjectRandomLocationManager();
        setBackgroundImage(FIRST_LEVEL_BACKGROUND_IMAGE);
        setEnemies();
        try{
            mage = heroFactory.getMage();
        }catch (Exception e){
            e.printStackTrace();
        }
        score = 0;
    }

    @Override
    public void clearGameObjects(){
        totalCountOfEnemies = 0;
        gameObjects.clear();
    }

    public void setBackgroundImage(String backgroundImage){
        this.backgroundImage = new BackgroundImage(new javafx.scene.image.Image("file:\\" + backgroundImage,852,480,false,true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        ((GridPane)this.root).setBackground(new Background(this.backgroundImage));
        ((GridPane)this.root).setPrefSize(852,480);
    }

    public void setEnemies(){
        try{
            gameObjects.clear();
            int loopnumber;
            if(mapLevel == 1)
                loopnumber = 28;
            else if(mapLevel == 2){
                loopnumber = 33;
            }
            else {
                loopnumber = 44;
            }

            totalCountOfEnemies = loopnumber;
            for(int i = 0; i < loopnumber; i++){
                GameObject gameObject;

                if(i == loopnumber - 1){
                    if( !featureExclude.contains("Boss")) {
                        locationManager.generateLocation(4000, 4200, 150, 200);
                        double x = locationManager.getX();
                        double y = locationManager.getY();
                        gameObject = new Boss(x, y, mapLevel);
                        gameObjects.add(gameObject);
                    }
                } else {
                    if(i < 5){
                        if( !featureExclude.contains("Small Enemy")) {
                            locationManager.generateLocation(750, 1100, 50, 370);
                            double x = locationManager.getX();
                            double y = locationManager.getY();
                            gameObject = new SmallEnemy(x, y, true);
                            gameObjects.add(gameObject);
                        }
                    }
                    else{

                        if(i % 4 == 0) {
                            if( !featureExclude.contains("Big Enemy")) {
                                locationManager.generateLocation(1000, 3750, 100, 300);
                                double x = locationManager.getX();
                                double y = locationManager.getY();
                                gameObject = new BigEnemy(x, y, mapLevel);
                                gameObject.setVisible(false);
                                gameObjects.add(gameObject);
                            }
                        } else {
                            if( !featureExclude.contains("Small Enemy")) {
                                locationManager.generateLocation(1100, 3750, 50, 370);
                                double x = locationManager.getX();
                                double y = locationManager.getY();
                                gameObject = new SmallEnemy(x, y, false);
                                gameObjects.add(gameObject);
                            }
                        }

                        if(i % 10 == 0) {
                            if( !featureExclude.contains("Power up")) {
                                GameObject powerUp1;
                                locationManager.generateLocation(1000, 3600, 100, 400);
                                powerUp1 = new PowerUp(locationManager.getX(), locationManager.getY(), (int) (Math.random() * 2 + 1), mapLevel);
                                powerUp1.setVisible(false);
                                gameObjects.add(powerUp1);
                            }
                        }
                    }

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void update(){
        for(int i = 0; i < gameObjects.size(); i++){
            if(gameObjects.get(i).toString().equals("Big Enemy") || gameObjects.get(i).toString().equals("Small Enemy") || gameObjects.get(i).toString().equals("Boss")){
                Enemy enemy = ((Enemy) gameObjects.get(i));
                if(enemy.getHealth() <= 0 || enemy.getXPos() <= 0 || enemy.getYPos() < 0){
                    gameObjects.remove(i);
                    deadCount++;
                }
            }
            else if(gameObjects.get(i).toString().equals("Power Up")){
                PowerUp powerUp = (PowerUp)gameObjects.get(i);
                if(powerUp.getXPos() <= 0 || powerUp.getYPos() <= 0 || powerUp.isUsed()){
                    gameObjects.remove(i);
                }
            }

        }
        if(deadCount == totalCountOfEnemies) {
            updateMap();
        }
    }

    public void updateMap(){
        setBackgroundImage(backgroundImages[mapLevel]);
        if(mapLevel == 1){
            mapLevel = 2;
        }
        else if(mapLevel == 2){
            mapLevel = 3;
        }
        else{
            Platform.runLater(
                    () -> Main.getPrimaryStage().setScene(GameEndPane.getInstance().getScene())
            );
        }
        deadCount = 0;
        setEnemies();
    }


    @Override
    public void createContent(){
        try{

            Canvas canvas = new Canvas(850, 480);
            ((GridPane)root).getChildren().add( canvas );
            GraphicsContext gc = canvas.getGraphicsContext2D();

            at = new AnimationTimer()
            {
                double lastNanoTime = System.nanoTime();
                public void handle(long currentNanoTime)
                {
                    try{
                        head.setText("Score: " + score);
                        head.setFill(Color.WHITE);

                        double elapsedTime = (currentNanoTime - lastNanoTime) / 1000000000.0;
                        lastNanoTime = currentNanoTime;

                        gc.clearRect(0, 0, 850,480);

                        mage.update(elapsedTime);
                        mage.draw(gc);
                        mage.controlHero();
                        for(int k = 0; k < mage.getBullets().size(); k++){
                            mage.getBullets().get(k).update(elapsedTime);
                            mage.getBullets().get(k).draw(gc);
                        }

                        for(int i = 0;i < gameObjects.size(); i++){
                            gameObjects.get(i).update(elapsedTime);
                            gameObjects.get(i).draw(gc);
                            if(gameObjects.get(i).toString().equals("Big Enemy")){
                                ((BigEnemy)gameObjects.get(i)).shoot();
                                for(int j = 0; j < ((BigEnemy)gameObjects.get(i)).getBullets().size(); j++){
                                    ((BigEnemy)gameObjects.get(i)).getBullets().get(j).update(elapsedTime);
                                    ((BigEnemy)gameObjects.get(i)).getBullets().get(j).draw(gc);
                                }
                            }
                            if(gameObjects.get(i).toString().equals("Boss")){
                                ((Boss)gameObjects.get(i)).useAbility();
                                for(int j = 0; j < ((Boss)gameObjects.get(i)).getBullets().size(); j++){
                                    ((Boss)gameObjects.get(i)).getBullets().get(j).update(elapsedTime);
                                    ((Boss)gameObjects.get(i)).getBullets().get(j).draw(gc);
                                }
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            at.start();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    public Parent load(){
        root.getStylesheets().add("sample/UserInterface/Screen/style.css");
        createContent();
        head = new Text("Score: " + score);
        head.setTranslateX(350);
        head.setTranslateY(-200);
        head.setId("header-help");
        if(t == null){
            t = new Thread(this);
            t.start();
        }
        ((GridPane)root).getChildren().add(head);
        return root;
    }

    public GameObject getGameObject(double x, double y){
        GameObject returnval = null;
        for(int i = 0; i < gameObjects.size(); i++){
            if(gameObjects.get(i).getXPos() == x && gameObjects.get(i).getYPos() == y){
                returnval = gameObjects.get(i);
            }
        }
        return returnval;
    }
    public ArrayList<Enemy> getVisibleEnemies(){
        ArrayList <Enemy> enemies = new ArrayList<>();
        int count = gameObjects.size();
        for(int i = 0; i < count; i++){
            if(gameObjects.get(i) != null)
                if(gameObjects.get(i).isVisible())
                    if(gameObjects.get(i).toString().equals("Big Enemy") || gameObjects.get(i).toString().equals("Small Enemy") || gameObjects.get(i).toString().equals("Boss"))
                        enemies.add((Enemy)gameObjects.get(i));
        }
        return enemies;
    }

    public ArrayList<PowerUp> getVisiblePowerUps(){
        ArrayList <PowerUp> powerUps = new ArrayList<>();
        int count = gameObjects.size();
        for(int i = 0; i < count; i++){
            if(gameObjects.get(i) != null)
                if(gameObjects.get(i).isVisible())
                    if(gameObjects.get(i).toString().equals("Power Up"))
                        powerUps.add((PowerUp) gameObjects.get(i));
        }
        return powerUps;
    }

    public Parent pauseGame(boolean gamePaused){
        if(gamePaused){
            at.stop();
        }
        else{
            at.start();
        }
        return pauseRoot;
    }

    @Override
    public void run() {

    }
}
