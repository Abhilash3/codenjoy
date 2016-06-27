package com.epam.dojo.icancode.model.items;

import com.codenjoy.dojo.services.*;
import com.epam.dojo.icancode.model.*;

import java.util.Arrays;

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
    private Integer resetToLevel;
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
        resetToLevel = null;
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
        if (player.getHero() == this || Arrays.asList(alsoAtPoint).contains(player.getHero())) {
            if (flying) {
                return Elements.ROBO_FLYING;
            }
            if (laser) {
                return Elements.ROBO_LASER;
            }
            if (hole) {
                return Elements.ROBO_FALLING;
            }
            return Elements.ROBO;
        } else {
            if (flying) {
                return Elements.ROBO_OTHER_FLYING;
            }
            if (laser) {
                return Elements.ROBO_OTHER_LASER;
            }

            if (hole) {
                return Elements.ROBO_OTHER_FALLING;
            }
            return Elements.ROBO_OTHER;
        }
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

    public void loadLevel(int level) {
        act(0, level);
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
        } else if (p[0] == 0) {
            if (p.length == 2) {
                resetToLevel = p[1];
            } else {
                resetToLevel = -1;
            }
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

        if (resetToLevel != null) {
            resetToLevel = null;
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

    public boolean isAlive() {
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

    public boolean isChangeLevel() {
        return resetToLevel != null;
    }

    public int getLevel() {
        int result = resetToLevel;
        resetToLevel = null;
        return result;
    }
}
