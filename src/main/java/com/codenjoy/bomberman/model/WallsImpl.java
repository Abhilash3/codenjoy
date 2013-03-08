package com.codenjoy.bomberman.model;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.Transformer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * User: oleksandr.baglai
 * Date: 3/7/13
 * Time: 6:04 PM
 */
public class WallsImpl implements Walls {
    private List<Wall> walls;

    public WallsImpl() {
        walls = new LinkedList<Wall>();
    }

    public WallsImpl(Walls sourceWalls) {
        this();
        for (Wall wall : sourceWalls) {
            walls.add(wall.copy());
        }
    }

    @Override
    public void add(int x, int y) {
        add(new Wall(x, y));
    }

    @Override
    public Iterator<Wall> iterator() {
        LinkedList result = new LinkedList();
        result.addAll(walls);
        return result.iterator();
    }

    @Override
    public boolean itsMe(int x, int y) {
        for (Wall wall : walls) {
            if (wall.itsMe(x, y)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Wall> subList(final Class<? extends Wall>...filter) {
        List<Wall> result = new LinkedList<Wall>();
        for (Wall input: walls) {
            for (Class<? extends Wall> clazz : filter) {
                if (input.getClass().equals(clazz)) {
                    result.add(input);
                }
            }
        }
        return result;
    }

    @Override
    public void add(Wall wall) {
        walls.add(wall);
    }

    @Override
    public void destroy(int x, int y) {
        walls.remove(new Wall(x, y));
    }
}
