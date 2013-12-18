package com.codenjoy.dojo.loderunner.model;

import com.codenjoy.dojo.services.*;

import java.util.List;

/**
 * User: sanja
 * Date: 17.12.13
 * Time: 4:56
 */
public class Loderunner implements Tickable, Game, Field {

    private final List<Point> borders;
    private final List<Brick> bricks;
    private List<Point> gold;
    private final Hero hero;
    private final int size;
    private final Printer printer;
    private Level level;
    private Dice dice;

    public Loderunner(Level level, Dice dice) {
        this.level = level;
        this.dice = dice;
        borders = level.getBorders();
        bricks = level.getBricks();
        gold = level.getGold();
        hero = level.getHero().iterator().next();
        hero.init(this);
        size = level.getSize();
        printer = new Printer(this);
    }

    @Override
    public void tick() {
        hero.tick();
        if (gold.contains(hero)) {
            gold.remove(hero);

            Point pos = getFreeRandom();
            gold.add(new PointImpl(pos.getX(), pos.getY()));
        }
        for (Brick brick : bricks) {
            brick.tick();
        }
        hero.checkAlive();
    }

    public int getSize() {
        return size;
    }

    @Override
    public Joystick getJoystick() {
        return hero; 
    }

    @Override
    public int getMaxScore() {
        return 0;  
    }

    @Override
    public int getCurrentScore() {
        return 0;  
    }

    @Override
    public boolean isGameOver() {
        return false;  
    }

    @Override
    public void newGame() {
        
    }

    @Override
    public String getBoardAsString() {
        return printer.toString();
    }

    @Override
    public void destroy() {
        
    }

    @Override
    public void clearScore() {
        
    }

    public List<Point> getBorders() {
        return borders;
    }

    public Hero getHero() {
        return hero;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    @Override
    public boolean isBarrier(int x, int y) {
        int index = bricks.indexOf(new PointImpl(x, y));
        return x >= size - 1 || x <= 0 || (index != -1 && bricks.get(index).state() == Elements.BRICK);   // TODO тут учет только стен основных
    }

    @Override
    public boolean tryToDrill(int x, int y) {
        Brick brick = getBrick(x, y);

        if (brick == null) {
            return false;
        }

        if (brick.state() != Elements.BRICK) {
            return false;
        }

        brick.drill();

        return true;
    }

    @Override
    public boolean isPit(int x, int y) {
        Brick brick = getBrick(x, y - 1);
        if (brick != null && brick.state() != Elements.BRICK) {
            return true;
        }

        // TODO проверить что под низом не Wall и на верху нет трубы

        return false;
    }

    @Override
    public Point getFreeRandom() {
        int rndX = 0;
        int rndY = 0;

        do {
            rndX = dice.next(size);
            rndY = dice.next(size);
        } while (!isFree(rndX, rndY));

        return new PointImpl(rndX, rndY);
    }

    private boolean isFree(int x, int y) {
        Point pt = new PointImpl(x, y);

        return !gold.contains(pt) && !borders.contains(pt) && !bricks.contains(pt) && !hero.itsMe(pt); // TODO потестить кажддое условие
    }

    private Brick getBrick(int x, int y) {
        for (Brick brick : bricks) {
            if (brick.itsMe(x, y)) return brick;
        }
        return null;
    }

    public List<Point> getGold() {
        return gold;
    }
}
