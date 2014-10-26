package com.codenjoy.dojo.bomberman.model;

import com.codenjoy.dojo.services.GamePrinter;
import com.codenjoy.dojo.services.Point;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.bomberman.model.Elements.*;

public class BombermanPrinter implements GamePrinter {

    private final IBoard board;
    private int size;
    private Player player;

    private Object[][] field;

    public BombermanPrinter(IBoard board, Player player) {
        this.board = board;
        this.player = player;
    }

    @Override
    public boolean init() {
        size = board.size();
        field = new Object[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                field[x][y] = new ArrayList<Point>(3);
            }
        }

        addAll(board.getWalls());
        addAll(board.getBombermans());
        addAll(board.getBombs());
        addAll(board.getBlasts());

        return false;
    }

    private void addAll(Iterable<? extends Point> elements) {
        for (Point el : elements) {
            ((List<Point>)field[el.getX()][el.getY()]).add(el);
        }
    }

    @Override
    public Enum get(Point pt) {
        return EMPTY;
    }

    @Override
    public void printAll(Filler filler) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                List<Point> elements = (List<Point>) field[x][y];
                if (elements.isEmpty()) {
                    filler.set(x, y, EMPTY);
                    continue;
                }

                Blast blast = null;
                Bomberman bomberman = null;
                MeatChopper meatChopper = null;
                DestroyWall destroyWall = null;
                Wall wall = null;
                Bomb bomb = null;
                for (Point element : elements) {
                    if (element instanceof Blast) {
                        blast = (Blast)element;
                    }
                    if (element instanceof Bomberman) {
                        bomberman = (Bomberman)element;
                    }
                    if (element instanceof MeatChopper) {
                        meatChopper = (MeatChopper)element;
                    } else if (element instanceof DestroyWall) {
                        destroyWall = (DestroyWall)element;
                    } else if (element instanceof Wall) {
                        wall = (Wall)element;
                    }
                    if (element instanceof Bomb) {
                        bomb = (Bomb)element;
                    }
                }

                if (wall != null) {
                    filler.set(x, y, wall.state(player));
                    continue;
                }

                if (blast != null && bomb == null) {
                    filler.set(x, y, blast.state(player, bomberman, meatChopper, destroyWall));
                    continue;
                }

                if (destroyWall != null) {
                    filler.set(x, y, destroyWall.state(player));
                    continue;
                }

                if (bomberman != null) {
                    filler.set(x, y, bomberman.state(player, bomb));
                    continue;
                }

                if (meatChopper != null) {
                    filler.set(x, y, meatChopper.state(player));
                    continue;
                }

                if (bomb != null) {
                    filler.set(x, y, bomb.state(player));
                    continue;
                }

                throw new RuntimeException();
            }
        }
    }
}
