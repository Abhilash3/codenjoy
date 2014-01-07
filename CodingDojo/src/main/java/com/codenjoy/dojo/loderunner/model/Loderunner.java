package com.codenjoy.dojo.loderunner.model;

import com.codenjoy.dojo.loderunner.services.LoderunnerEvents;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.Tickable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.codenjoy.dojo.services.PointImpl.pt;

/**
 * User: sanja
 * Date: 17.12.13
 * Time: 4:56
 */
public class Loderunner implements Tickable, Field {

    private final List<Point> borders;
    private final List<Brick> bricks;
    private final List<Point> pipe;
    private List<Point> gold;
    private List<Point> ladder;
    private List<Enemy> enemies;

    private List<Player> players;

    private final int size;
    private Dice dice;

    public Loderunner(Level level, Dice dice) {
        this.dice = dice;
        borders = level.getBorders();
        bricks = level.getBricks();
        gold = level.getGold();
        ladder = level.getLadder();
        pipe = level.getPipe();

        enemies = level.getEnemies();
        for (Enemy enemy : enemies) {
            enemy.init(this);
        }

        size = level.getSize();
        players = new LinkedList<Player>();
    }

    @Override
    public void tick() {
        Set<Player> die = new HashSet<Player>();
        die.addAll(enemiesGo());
        die.addAll(heroesGo());
        die.addAll(bricksGo());

        for (Player player : die) {
            player.event(LoderunnerEvents.KILL_HERO);
        }
    }

    private List<Player> bricksGo() {
        List<Player> die = new LinkedList<Player>();

        for (Brick brick : bricks) {
            brick.tick();
        }

        for (Player player : players) {
            Hero hero = player.getHero();

            if (!hero.isAlive()) {
                int index = bricks.indexOf(hero);
                if (index == -1) continue;

                // Умер от того что кто-то просверлил стенку
                die.add(player);

                Brick brick = bricks.get(index);
                Hero killer = brick.getDrilledBy();
                Player killerPlayer = getPlayer(killer);
                if (killerPlayer != null && killerPlayer != player) {
                    killerPlayer.event(LoderunnerEvents.KILL_ENEMY);
                }
            }
        }

        return die;
    }

    private List<Player> heroesGo() {
        List<Player> die = new LinkedList<Player>();

        for (Player player : players) {
            Hero hero = player.getHero();

            hero.tick();

            if (!hero.isAlive()) {
                die.add(player);
            }

            if (gold.contains(hero)) {
                gold.remove(hero);
                player.event(LoderunnerEvents.GET_GOLD);

                Point pos = getFreeRandom();
                leaveGold(pos.getX(), pos.getY());
            }
        }

        return die;
    }

    private List<Player> enemiesGo() {
        List<Player> die = new LinkedList<Player>();

        for (Enemy enemy : enemies) {
            enemy.tick();

            if (gold.contains(enemy) && !enemy.withGold()) {
                gold.remove(enemy);
                enemy.getGold();
            }
        }

        for (Player player : players) {
            Hero hero = player.getHero();

            if (!hero.isAlive()) {
                die.add(player);
            }
        }

        return die;
    }

    private Player getPlayer(Hero hero) {
        for (Player player : players) {
            if (player.getHero() == hero) {
                return player;
            }
        }
        return null;
    }

    public int getSize() {
        return size;
    }

    public List<Point> getBorders() {
        return borders;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    @Override
    public boolean isBarrier(int x, int y) {
        Point pt = pt(x, y);
        return x > size - 1 || x < 0 || y < 0 || y > size - 1 || isFullBrick(x, y) || borders.contains(pt) || isHeroAt(x, y);
    }

    @Override
    public boolean tryToDrill(Hero byHero, int x, int y) {
        Point pt = pt(x, y);
        if (!isFullBrick(x, y)) {
            return false;
        }

        Point over = pt(x, y + 1);
        if (ladder.contains(over) || gold.contains(over) || isFullBrick(over.getX(), over.getY())
                || getHeroes().contains(over) || enemies.contains(over)) {
            return false;
        }

        Brick brick = getBrick(pt);
        brick.drill(byHero);

        return true;
    }

    @Override
    public boolean isPit(int x, int y) {
        Point pt = pt(x, y - 1);

        if (!isFullBrick(pt.getX(), pt.getY()) && !ladder.contains(pt) && !borders.contains(pt)
                && !getHeroes().contains(pt) && !enemies.contains(pt)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isFullBrick(int x, int y) {
        Brick brick = getBrick(pt(x, y));
        return brick != null && brick.state() == Elements.BRICK;
    }

    @Override
    public Point getFreeRandom() {
        int rndX = 0;
        int rndY = 0;
        int c = 0;
        do {
            rndX = dice.next(size);
            rndY = dice.next(size);
        } while (!isFree(rndX, rndY) && c++ < 100);

        if (c >= 100) {
            return pt(0, 0);  // этого никогда не должно случиться, но никогда не говори никогда. чтобы заметить поставил координаты 0, 0
        }

        return pt(rndX, rndY);
    }

    @Override
    public boolean isLadder(int x, int y) {
        return ladder.contains(pt(x, y));
    }

    @Override
    public boolean isPipe(int x, int y) {
        return pipe.contains(pt(x, y));
    }

    @Override
    public boolean isFree(int x, int y) {
        Point pt = pt(x, y);

        return !gold.contains(pt) &&
                !borders.contains(pt) &&
                !bricks.contains(pt) &&
                !getHeroes().contains(pt) &&
                !pipe.contains(pt) &&
                !ladder.contains(pt);
    }

    @Override
    public boolean isHeroAt(int x, int y) {
        return getHeroes().contains(pt(x, y));
    }

    @Override
    public boolean isBrick(int x, int y) {
        return bricks.contains(pt(x, y));
    }

    @Override
    public boolean isEnemyAt(int x, int y) {
        return enemies.contains(pt(x, y));
    }

    @Override
    public void leaveGold(int x, int y) {
        gold.add(pt(x, y));
    }

    private Brick getBrick(Point pt) {
        int index = bricks.indexOf(pt);
        if (index == -1) return null;
        return bricks.get(index);
    }

    public List<Point> getGold() {
        return gold;
    }

    public List<Point> getLadder() {
        return ladder;
    }

    public List<Point> getPipe() {
        return pipe;
    }

    public List<Hero> getHeroes() {
        List<Hero> result = new LinkedList<Hero>();
        for (Player player : players) {
            result.add(player.getHero());
        }
        return result;
    }

    public void newGame(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
        player.newHero(this);
    }

    public void remove(Player player) {
        players.remove(player);
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}
