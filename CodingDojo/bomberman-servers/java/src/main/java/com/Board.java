package com;

import java.util.LinkedList;
import java.util.List;

/**
 * User: oleksandr.baglai
 */
public class Board {
    public final static char BOMBERMAN = '☺';
    public final static char BOMB_BOMBERMAN = '☻';
    public final static char DEAD_BOMBERMAN = 'Ѡ';
    public final static char BOOM = '҉';
    public final static String BOMBS = "012345";
    public final static char WALL = '☼';
    public final static char DESTROY_WALL = '#';
    public final static char MEAT_CHOPPER = '&';
    public final static char DEAD_MEAT_CHOPPER = 'x';
    public final static char SPACE = ' ';

    private String board;
    private LengthToXY xyl;
    private int size;

    public Board(String boardString) {
        board = boardString.replaceAll("\n", "");
        size = size();
        xyl = new LengthToXY(size);
    }

    public Point getBomberman() {
        Point result = xyl.getXY(board.indexOf(BOMBERMAN));
        if (result == null) {
            result = xyl.getXY(board.indexOf(BOMB_BOMBERMAN));
        }
        if (result == null) {
            result = xyl.getXY(board.indexOf(DEAD_BOMBERMAN));
        }
        return result;
    }

    public boolean isDead() {
        return board.indexOf(DEAD_BOMBERMAN) != -1;
    }

    public boolean isAt(int x, int y, char type) {
        return board.charAt(xyl.getLength(x, y)) == type;
    }

    public int size() {
        return (int) Math.sqrt(board.length());
    }

    public String fix() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i <= size - 1; i++) {
            buffer.append(board.substring(i*size, (i + 1)*size));
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public List<Point> getBarriers() {
        List<Point> all = getFutureBlasts();
        all.addAll(getMeatChoppers());
        all.addAll(getWalls());
        all.addAll(getDestroyWalls());

        List<Point> result = new LinkedList<Point>();
        for (Point point : all) {
            if (!all.contains(point)) {
                all.add(point);
            }
        }

        return result;
    }

    @Override
    public String toString() {
//        return String.format("Board:\n%s\n" +
//            "Bomberman at: %s\n" +
//            "Meat choppers at: %s\n" +
//            "Destroy walls at: %s\n" +
//            "Bombs at: %s\n" +
//            "Blasts: %s\n" +
//            "Expected blasts at: %s",
//                fix(),
//                getBomberman(),
//                getMeatChoppers(),
//                getDestroyWalls(),
//                getBombs(),
//                getBlasts(),
//                getFutureBlasts());
        return "";
    }

    public List<Point> getMeatChoppers() {
        return findAll(MEAT_CHOPPER);
    }

    private List<Point> findAll(char element) {
        List<Point> result = new LinkedList<Point>();
        for (int i = 0; i < size*size; i++) {
            Point pt = xyl.getXY(i);
            if (isAt(pt.x, pt.y, element)) {
                result.add(pt);
            }
        }
        return result;
    }

    public List<Point> getWalls() {
        return findAll(WALL);
    }

    public List<Point> getDestroyWalls() {
        return findAll(DESTROY_WALL);
    }

    public List<Point> getBombs() {
        List<Point> result = new LinkedList<Point>();
        for (int index = 0; index < BOMBS.length(); index++) {
            result.addAll(findAll(BOMBS.charAt(index)));
        }
        result.addAll(findAll(BOMB_BOMBERMAN));
        return result;
    }

    public List<Point> getBlasts() {
        return findAll(BOOM);
    }

    public List<Point> getFutureBlasts() {
        List<Point> result = new LinkedList<Point>();
        List<Point> bombs = getBombs();
        for (Point bomb : bombs) {
            result.add(bomb);
            result.add(new Point(bomb.x - 1, bomb.y));
            result.add(new Point(bomb.x + 1, bomb.y));
            result.add(new Point(bomb.x    , bomb.y - 1));
            result.add(new Point(bomb.x    , bomb.y + 1));
        }
        for (Point blast : result.toArray(new Point[0])) {
            if (blast.isBad(size)) {
                result.remove(blast);
            }
        }
        return result;
    }

    public boolean isAt(Point pt, char c) {
        return isAt(pt.x, pt.y, c);
    }

    public boolean isAt(int x, int y, String chars) {
        for (char c : chars.toCharArray()) {
            if (isAt(x, y, c)) {
                return true;
            }
        }
        return false;
    }
}