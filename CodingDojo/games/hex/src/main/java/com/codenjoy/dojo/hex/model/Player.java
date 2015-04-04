package com.codenjoy.dojo.hex.model;

import com.codenjoy.dojo.hex.services.HexEvent;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.Tickable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Player implements Tickable {

    class Heroes implements Iterable<Hero> {
        private List<Hero> heroes;

        Heroes() {
            heroes = new LinkedList<Hero>();
        }

        public void clear() {
            for (Hero hero : heroes) {
                hero.newOwner(null);
            }
            heroes.clear();
        }

        @Override
        public Iterator<Hero> iterator() {
            return new LinkedList<Hero>(heroes).iterator();
        }

        public boolean remove(Hero hero) {
            hero.newOwner(null);
            return heroes.remove(hero);
        }

        public void add(Hero hero, Player player) {
            heroes.add(hero);
            hero.newOwner(player);
        }

        public boolean contains(Hero hero) {
            return heroes.contains(hero);
        }

        public boolean isEmpty() {
            return heroes.isEmpty();
        }

        public int size() {
            return heroes.size();
        }
    }

    private EventListener listener;
    private int maxScore;
    private int score;
    private Hero active;
    private boolean alive;
    private Heroes heroes;
    private Field field;
    Hero newHero;
    private Elements element;
    private int loose;
    private int win;

    public Player(EventListener listener, Field field) {
        this.listener = listener;
        this.field = field;
        this.alive = true;
        heroes = new Heroes();
        clearScore();
    }

    public Heroes getHeroes() {
        return heroes;
    }

    private void increaseScore() {
        score = score + 1;
        maxScore = Math.max(maxScore, score);
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getScore() {
        return score;
    }

    public void event(HexEvent event) {
        switch (event.getType()) {
            case LOOSE: resetScore(); break;
            case WIN: increaseScore(); break;
        }

        if (listener != null) {
            listener.event(event);
        }
    }

    private void resetScore() {
        score = 0;
    }

    public void clearScore() {
        score = 0;
        maxScore = 0;
    }

    public void newHero() {
        Point pt = field.getFreeRandom();
        Hero hero = new Hero(pt, element);
        hero.init(field);
        heroes.clear();
        heroes.add(hero, this);
        alive = true;
    }

    public void addHero(Hero newHero) {
        this.newHero = newHero;
    }

    public Joystick getJoystick() {
        return new Joystick() {
            @Override
            public void down() {
                if (active == null || !alive) return;
                active.down();
            }

            @Override
            public void up() {
                if (active == null || !alive) return;
                active.up();
            }

            @Override
            public void left() {
                if (active == null || !alive) return;
                active.left();
            }

            @Override
            public void right() {
                if (active == null || !alive) return;
                active.right();
            }

            @Override
            public void act(int... p) {
                if (!alive) return;
                int x = p[0]; // TODO validation
                int y = field.size() - 1 - p[1];
                boolean jump = p.length == 3 && p[2] == 1;

                Hero hero = field.getHero(x, y);
                if (hero != null && heroes.contains(hero)) {
                    active = hero;
                    active.isJump(jump);
                    if (jump) {
                        addHero(hero);
                    }
                } else {
                    active = null;
                }
            }
        };
    }

    public boolean isAlive() {
        return alive;
    }

    public void remove(Hero hero) {
        if (newHero == hero) {
            boolean remove = heroes.remove(hero);
            if (remove) {
                loose(1);
            }
            newHero = null;
        }
    }

    public void applyNew() {
        if (newHero != null && !heroes.contains(newHero)) {
            heroes.add(newHero, this);
            newHero = null;
            win(1);
        }
    }

    @Override
    public void tick() {
        if (heroes.isEmpty() && newHero == null) {
            die();
            return;
        }
        for (Hero hero : heroes) {
            hero.tick();
        }
        if (newHero != null) {
            newHero.tick();
        }

        active = null;
    }

    public void die() {
        alive = false;
        heroes.clear();
        newHero = null;
    }

    public boolean itsMine(Hero hero) {
        return heroes.contains(hero) || hero == newHero;
    }

    public Elements getElement() {
        return element;
    }

    public void setElement(Elements element) {
        this.element = element;
    }

    public void loose(int count) {
        loose += count;
    }

    public void win(int count) {
        win += count;
    }

    public void fireEvents() {
        if (loose > 0) {
            listener.event(new HexEvent(HexEvent.Event.LOOSE, loose));
            loose = 0;
        }

        if (win > 0) {
            listener.event(new HexEvent(HexEvent.Event.WIN, win));
            win = 0;
        }
    }
}