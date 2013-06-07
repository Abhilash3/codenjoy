package com.codenjoy.dojo.battlecity.model;

public class Bullet extends MovingObject {

    private Field field;
    private OnDestroy onDestroy;

    public Bullet(Field field, Direction tankDirection, Point from, OnDestroy onDestroy) {
        super(from.getX(), from.getY(), tankDirection);
        this.field = field;
        this.onDestroy = onDestroy;
        this.speed = 2;
    }

    public void onDestroy() {
        if (onDestroy != null) {
            onDestroy.destroy();
        }
    }

    public void moveUp() {
        if (y + speed < field.getSize()) {
            direction = Direction.UP;
            y += speed;
            field.affect(this);
        } else {
            onDestroy();
        }
    }

    public void moveDown() {
        if (y > 1) {
            direction = Direction.DOWN;
            y -= speed;
            field.affect(this);
        } else {
            onDestroy();
        }
    }

    public void moveRight() {
        if (x + speed < field.getSize()) {
            direction = Direction.RIGHT;
            x += speed;
            field.affect(this);
        } else {
            onDestroy();
        }
    }

    public void moveLeft() {
        if (x > 1) {
            direction = Direction.LEFT;
            x -= speed;
            field.affect(this);
        } else {
            onDestroy();
        }
    }

    public void move() {
        if (this.direction.equals(Direction.UP)) {
            moveUp();
        }
        if (this.direction.equals(Direction.DOWN)) {
            moveDown();
        }
        if (this.direction.equals(Direction.RIGHT)) {
            moveRight();
        }
        if (this.direction.equals(Direction.LEFT)) {
            moveLeft();
        }
    }
}
