package com.codenjoy.dojo.battlecity.model;

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Joystick;

import java.util.LinkedList;
import java.util.List;

public class Tank extends MovingObject implements Joystick {

    private Dice dice;
    private List<Bullet> bullets;
    private Tanks field;
    private boolean alive;

    public Tank(int x, int y, Direction direction, Dice dice) {
        super(x, y, direction);
        bullets = new LinkedList<Bullet>();
        speed = 1;
        moving = false;
        alive = true;
        this.dice = dice;
    }

    void turn(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void up() {
        direction = Direction.UP;
        moving = true;
    }

    @Override
    public void down() {
        direction = Direction.DOWN;
        moving = true;
    }

    @Override
    public void right() {
        direction = Direction.RIGHT;
        moving = true;
    }

    @Override
    public void left() {
        direction = Direction.LEFT;
        moving = true;
    }

    @Override
    public void moving(int newX, int newY) {
        if (field.isBarrier(newX, newY)) {
            // do nothing
        } else {
            x = newX;
            y = newY;
        }
        moving = false;
    }

    @Override
    public void act() {
        Bullet bullet = new Bullet(field, direction, copy(), this, new OnDestroy() {
            @Override
            public void destroy(Object bullet) {
                Tank.this.bullets.remove(bullet);
            }
        });
        if (!bullets.contains(bullet)) {
            bullets.add(bullet);
        }
    }

    public Iterable<Bullet> getBullets() {
        return new LinkedList<Bullet>(bullets);
    }

    public void setField(Tanks field) {
        this.field = field;
        int xx = x;
        int yy = y;
        while (field.isBarrier(xx, yy)) {
            xx = dice.next(field.getSize());
            yy = dice.next(field.getSize());
        }
        x = xx;
        y = yy;
        alive = true;
    }

    public void kill(Bullet bullet) {
        alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    public void removeBullets() {
        bullets.clear();
    }
}
