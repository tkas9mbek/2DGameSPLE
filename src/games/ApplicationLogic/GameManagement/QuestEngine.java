package games.ApplicationLogic.GameManagement;

import javafx.application.Platform;
import games.ApplicationLogic.GameEntities.*;
import games.UserInterface.Screen.GameOverPane;
import games.UserInterface.Screen.Main;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class QuestEngine extends AbstractEngine{

    private final QuestMap questMap;
    private final ArrayList<String> quests;
    private final ArrayList<String> inventory;
    private int currentMapNo;

    public QuestEngine() throws FileNotFoundException {
        isGamePaused = false;
        cm = CollisionManager.getInstance();
        quests = new ArrayList<>();
        inventory = new ArrayList<>();
        currentMapNo = 1;
        quests.add("Find lumberjack and kill him");
        questMap = mapFactory.getQuestMap(quests, inventory, currentMapNo);
    }

    public void run(){
        try{
            while (true) {
                questMap.update();
                checkCollision();
                Hunter hero = questMap.heroFactory.getHunter();

                if(currentMapNo == 1 && hero.getXPos() <= 2){
                    currentMapNo = 2;
                    hero.setLocation(700, hero.getYPos());
                    questMap.setMapNo(currentMapNo);
                    questMap.setEnemies();
                }

                if(currentMapNo == 2 && hero.getXPos() >= 750){
                    currentMapNo = 1;
                    hero.setLocation(50, hero.getYPos());
                    questMap.setMapNo(currentMapNo);
                    questMap.setEnemies();
                }

                if(hero.getHealth().getHealthAmount() <= 0){
                    Platform.runLater(
                            () -> Main.getPrimaryStage().setScene(GameOverPane.getInstance(false).getScene())
                    );
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void gameLoop(){
        if(t == null){
            t = new Thread(this);
            t.start();
        }
    }

    @Override
    public void checkCollision() throws FileNotFoundException {
        boolean flag;
        QuestMap map = mapFactory.getQuestMap();
        ArrayList<Enemy> enemies;
        ArrayList<Obstacle> obstacles;
        ArrayList<Neutral> neutrals;
        ArrayList<Item> items;
        ArrayList<Neutral> homes;
        homes = map.getVisibleHome();
        items = map.getVisibleItems();
        obstacles = map.getVisibleObstacles();
        neutrals = map.getVisibleNeutrals();
        enemies = map.getVisibleEnemies();
        Hunter hero = map.heroFactory.getHunter();

        for (Enemy enemy : enemies) {
            try {
                flag = cm.checkGameObjectCollision(enemy,hero);

                for (int j = 0; j < hero.getBullets().size() && !flag; j++) {
                    flag = cm.checkGameObjectCollision(enemy, hero.getBullets().get(j));
                    if (flag) {
                        enemy.decreaseHealth(hero.getBullets().get(j).getDamage());
                        hero.getBullets().remove(j);
                    }
                }

                if (enemy.toString().equals("Big Enemy") ) {
                    ArrayList<Bullet> bullets;
                    if(  enemy.getHealth() <= 0 ) {
                        GameObject gold = new Item(enemy.getXPos(), enemy.getYPos(), 1, "Bag of gold");
                        map.removeObject(enemy);
                        quests.remove(0);
                        quests.add("Pick up a bag of gold");
                        questMap.setQuests(quests);
                        map.addObject(gold);
                    } else {
                        bullets = ((BigEnemy) enemy).getBullets();
                        for (int j = 0; j < bullets.size() && !flag; j++) {
                            flag = cm.checkGameObjectCollision(hero, bullets.get(j));
                            if (flag) {
                                hero.healthDecrease(((BigEnemy) enemy).getBullets().get(j).getDamage());
                                ((BigEnemy) enemy).getBullets().remove(j);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Obstacle obstacle : obstacles) {
            try {
                flag = cm.checkGameObjectCollision(obstacle,hero);
                if(flag) {
                    if(currentMapNo == 1) {
                        hero.setLocation( hero.getXPos() - 10, hero.getYPos());
                    }
                    if(currentMapNo == 2) {
                        hero.setLocation( hero.getXPos() + 10, hero.getYPos());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Item item : items) {
            try {
                flag = cm.checkGameObjectCollision(item,hero);
                if(flag) {
                    map.addToInventory("Bag of gold");
                    quests.remove(0);
                    quests.add("Take bag of gold to home");
                    questMap.setQuests(quests);
                    map.removeObject(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Neutral neutral : neutrals) {
            try {
                flag = cm.checkGameObjectCollision(neutral,hero);
                if( flag ) {
                    map.setHint(neutral.getHint());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Neutral home : homes) {
            try {
                flag = cm.checkGameObjectCollision(home,hero);
                if( flag && map.getInventory().size() >= 1) {
                    Platform.runLater(
                            () -> Main.getPrimaryStage().setScene(GameOverPane.getInstance(true).getScene())
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(neutrals.size() == 0) {
            map.setHint(null);
        }
    }
}