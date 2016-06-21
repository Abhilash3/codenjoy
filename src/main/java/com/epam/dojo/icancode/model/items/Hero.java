package com.epam.dojo.icancode.model.items;

import com.codenjoy.dojo.services.*;
import com.epam.dojo.icancode.model.*;

/**
 * Это реализация героя. Обрати внимание, что он имплементит {@see Joystick}, а значит может быть управляем фреймворком
 * Так же он имплементит {@see Tickable}, что значит - есть возможность его оповещать о каждом тике игры.
 */
public class Hero extends BaseItem implements Joystick, Tickable {

    private boolean alive;
    private boolean win;
    private Direction direction;
    private boolean jump;
    private boolean flying;
    private boolean reset;
    private boolean laser;
    private boolean hole;
    private boolean landOn;
    private int goldCount;

    public Hero(Elements el) {
        super(new ItemLogicType[]{ItemLogicType.PASSABLE, ItemLogicType.HERO}, el);

        resetFlags();
    }

    private void resetFlags() {
        direction = null;
        win = false;
        jump = false;
        landOn = false;
        reset = false;
        flying = false;
        laser = false;
        alive = true;
        goldCount = 0;
    }

    @Override
    public void init(Field field) {
        super.init(field);
        reset(field);
    }

    private void reset(Field field) {
        resetFlags();
        field.getStartPosition().addItem(this);
        field.reset();
    }

    @Override
    public Elements state(Player player, Object... alsoAtPoint) {
        if (flying) {
            return Elements.ROBO_FLYING;
        }
        if (laser) {
            return Elements.ROBO_LASER;
        }

        if (hole) {
            return Elements.ROBO_FALLING;
        }

        return super.state(player, alsoAtPoint);
    }

    @Override
    public void down() {
        if (!alive) return;

        if (!flying) {
            direction = Direction.DOWN;
        }
    }

    @Override
    public void up() {
        if (!alive) return;

        if (!flying) {
            direction = Direction.UP;
        }
    }

    @Override
    public void left() {
        if (!alive) return;

        if (!flying) {
            direction = Direction.LEFT;
        }
    }

    @Override
    public void right() {
        if (!alive) return;

        if (!flying) {
            direction = Direction.RIGHT;
        }
    }

    public void reset() {
        act(0);
    }

    public void jump() {
        act(1);
    }

    @Override
    public void act(int... p) {
        if (!alive) return;

        if (p.length == 0 || p[0] == 1) {
            if (!flying) {
                jump = true;
            }
        } else if (p.length == 1 && p[0] == 0) {
            reset = true;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void tick() {
        laser = false;
        hole = false;
        if (!alive) return;

        if (reset) {
            reset = false;
            reset(field);
            return;
        }

        if (flying) {
            flying = false;
            landOn = true;
        }

        if (jump) {
            flying = true;
            jump = false;
        }

        if (direction != null) {
            int newX = direction.changeX(cell.getX());
            int newY = direction.changeY(cell.getY());

            if (!field.isBarrier(newX, newY)) {
                field.move(this, newX, newY);
            } else {
                if (landOn) {
                    landOn = false;
                    cell.comeIn(this);
                }
            }
        }
        if (!flying) {
            direction = null;
        }
    }

    public Point getPosition() {
        return cell;
    }

    public boolean isAlive()
    {
        return alive;
    }

    @Override
    public void action(BaseItem item) {

    }

    public void setWin() {
        win = true;
    }

    public void die() {
        alive = false;
    }

    public boolean isWin(){
        return win;
    }

    public void pickUpGold() {
        goldCount++;
    }

    public int getGoldCount() {
        return goldCount;
    }

    public boolean isFlying() {
        return flying;
    }

    public void dieOnHole() {
        hole = true;
        die();
    }

    public void dieOnLaser() {
        laser = true;
        die();
    }
}
