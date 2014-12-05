package com.codenjoy.dojo.collapse.model;

import com.codenjoy.dojo.collapse.services.CollapseEvents;
import com.codenjoy.dojo.services.*;

import java.util.*;

public class Collapse implements Tickable, Field {

    private Container<Point, Cell> cells;
    private Player player;

    private final int size;
    private List<Wall> walls;

    private Point act;
    private Direction direction;

    private boolean gameOver;

    class Container<T extends Point, V extends Point> implements Iterable<V> {
        private Map<T, V> data;

        public Container() {
            data = new HashMap<T, V>();
        }

        public Container(List<V> list) {
            this();
            for (V v : list) {
                data.put((T)v, v);
            }
        }

        public V get(T key) {
            return data.get(key);
        }

        public void add(V value) {
            data.put((T)value, value);
        }

        public boolean isEmpty() {
            return data.isEmpty();
        }

        public V removeLast() {
            Iterator<T> iterator = data.keySet().iterator();
            if (!iterator.hasNext()) return null;
            T key = iterator.next();
            V result = data.get(key);
            data.remove(key);
            return result;
        }

        @Override
        public Iterator<V> iterator() {
            return data.values().iterator();
        }

        public void remove(V value) {
            data.remove(value);
        }

        public Collection<V> values() {
            return data.values();
        }

        public int size() {
            return data.size();
        }

        public boolean contains(V value) {
            return data.containsKey(value);
        }
    }

    public Collapse(Level level) {
        cells = new Container(level.getCells());
        walls = level.getWalls();
        size = level.getSize();
        gameOver = false;
    }

    @Override
    public void tick() {
        if (gameOver) return;
        if (act == null || direction == null) return;

        Cell cell = cells.get(act);
        if (cell == null) return;

        Point to = direction.change(act);
        Cell cellTo = cells.get(to);
        if (cellTo == null) return;

        cell.exchange(cellTo);

        checkClear(cell, cellTo);
        fillNew();

        act = null;
        direction = null;
    }

    private void checkClear(Cell cell1, Cell cell2) {
        Container<Point, Cell> toCheck = new Container();
        Container<Point, Cell> forRemove = new Container();
        toCheck.add(cell1);
        toCheck.add(cell2);

        while (!toCheck.isEmpty()) {
            Cell current = toCheck.removeLast();

            for (Direction dir : Direction.values()) {
                Point pt = dir.change(current);
                Cell next = cells.get(pt);
                if (next == null) continue;

                if (current.getNumber() == next.getNumber()) {
                    if (!forRemove.contains(next)) {
                        toCheck.add(next);
                    }
                    forRemove.add(current);
                    forRemove.add(next);
                }
            }
        }

        if (!forRemove.isEmpty()) {
            int count = forRemove.size();

            for (Cell remove : forRemove) {
                cells.remove(remove);
            }

            CollapseEvents success = CollapseEvents.SUCCESS;
            success.setCount(count);
            player.event(success);
        }
    }

    private void fillNew() {
        // TODO implement me
    }

    public int getSize() {
        return size;
    }

    public void newGame(Player player) {
        this.player = player;
        gameOver = false;
        player.newHero(this);
    }

    public void remove(Player player) {
        this.player = null;
    }

    public Collection<Cell> getCells() {
        return cells.values();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    @Override
    public Joystick getJoystick() {
        return new Joystick() {
            @Override
            public void down() {
                direction = Direction.DOWN;
            }

            @Override
            public void up() {
                direction = Direction.UP;
            }

            @Override
            public void left() {
                direction = Direction.LEFT;
            }

            @Override
            public void right() {
                direction = Direction.RIGHT;
            }

            @Override
            public void act(int... p) {
                if (gameOver) return;

                if (p.length == 1 && p[0] == 0) {
                    gameOver = true;
                    player.event(CollapseEvents.NEW_GAME);
                    return;
                }

                if (p.length != 2) {
                    return;
                }

                if (check(p[0])) return;
                if (check(p[1])) return;

                int x = p[0];
                int y = p[1];
                act = PointImpl.pt(x, y);
            }
        };
    }

    private boolean check(int i) {
        return (i >= size || i < 0);
    }

    public BoardReader reader() {
        return new BoardReader() {
            private int size = Collapse.this.size;

            @Override
            public int size() {
                return size;
            }

            @Override
            public Iterable<? extends Point> elements() {
                List<Point> result = new LinkedList<Point>();
                result.addAll(Collapse.this.walls);
                result.addAll(Collapse.this.cells.values());
                return result;
            }
        };
    }
}
