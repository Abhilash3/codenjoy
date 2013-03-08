package com.codenjoy.bomberman.model;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * User: oleksandr.baglai
 * Date: 3/8/13
 * Time: 8:17 PM
 */
public class MeatChoppers implements Walls, Tickable {

    private Walls walls;
    private int size;
    private int count;

    public MeatChoppers(Walls walls, int size, int count) {
        if (walls.subList(Wall.class).size() + count >= size*size - 1) {
            throw new IllegalArgumentException("No more space at board for MeatChoppers");
        }
        this.walls = walls;
        this.size = size;
        this.count = count;
        randomFill();
    }

    private void randomFill() { // TODO remove duplicates
        int index = 0;
        int counter = 0;
        do {
            int x = new Random().nextInt(size);
            int y = new Random().nextInt(size);

            if (walls.itsMe(x, y)) {
                continue;
            }

            if (y == 1) {  // чтобы бомбермену было откуда начинать
                continue;
            }

            walls.add(new MeatChopper(x, y));

            index++;
            counter++;
        } while (index != count && counter < 10000);
        if (counter == 10000) {
            throw new  RuntimeException("Dead loop at MeatChoppers.randomFill!");
        }
    }

    @Override
    public Iterator<Wall> iterator() {
        return walls.iterator();
    }

    @Override
    public void add(int x, int y) {
        walls.add(x, y);
    }

    @Override
    public boolean itsMe(int x, int y) {
        return walls.itsMe(x, y);
    }

    @Override
    public <T extends Wall> List<T> subList(Class<T> filter) {
        return walls.subList(filter);
    }

    @Override
    public void add(Wall wall) {
        walls.add(wall);
    }

    @Override
    public void destroy(int x, int y) {
        walls.destroy(x, y);
    }

    @Override
    public void tick() {
        List<MeatChopper> meatChoppers = walls.subList(MeatChopper.class);
        for (MeatChopper meatChopper : meatChoppers) {
            Direction direction = meatChopper.getDirection();
            if (direction != null && new Random().nextInt(5) > 0) {
                int x = direction.changeX(meatChopper.getX());
                int y = direction.changeY(meatChopper.getY());
                if (!walls.itsMe(x, y)) {
                    meatChopper.move(x, y);
                    continue;
                }
            }
            meatChopper.setDirection(tryToMove(meatChopper));
        }
    }

    private Direction tryToMove(Point pt) {
        int count = 0;
        int x = pt.getX();
        int y = pt.getY();
        Direction direction = null;
        do {
            int move = new Random().nextInt(4);
            direction = Direction.valueOf(move);

            x = direction.changeX(pt.getX());
            y = direction.changeY(pt.getY());
        } while (walls.itsMe(x, y) && count++ < 10);

        if (count < 10) {
            pt.move(x, y);
            return direction;
        }
        return null;
    }
}
