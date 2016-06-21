package com.epam.dojo.icancode.model.items;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Tickable;
import com.epam.dojo.icancode.model.*;

/**
 * Created by oleksandr.baglai on 20.06.2016.
 */
public class LaserMachine extends BaseItem implements Tickable {

    private static final int CHARGED = 6;
    private final Direction direction;
    private int timer;

    public LaserMachine(Elements element) {
        super(new ItemLogicType[] {ItemLogicType.IMPASSABLE, ItemLogicType.LASER_MACHINE}, element);
        timer = 0;
        this.direction = getDirection(element);
    }

    private Direction getDirection(Elements element) {
        switch (element) {
            case LASER_MACHINE_CHARGING_LEFT: return Direction.LEFT;
            case LASER_MACHINE_CHARGING_RIGHT: return Direction.RIGHT;
            case LASER_MACHINE_CHARGING_UP: return Direction.UP;
            case LASER_MACHINE_CHARGING_DOWN: return Direction.DOWN;
        }
        throw new IllegalStateException("Unexpected element: " + element);
    }

    private Elements getChargedElement(Direction direction) {
        switch (direction) {
            case LEFT: return Elements.LASER_MACHINE_READY_LEFT;
            case RIGHT: return Elements.LASER_MACHINE_READY_RIGHT;
            case UP: return Elements.LASER_MACHINE_READY_UP;
            case DOWN: return Elements.LASER_MACHINE_READY_DOWN;
        }
        throw new IllegalStateException("Unexpected direction: " + direction);
    }

    @Override
    public Elements state(Player player, Object... alsoAtPoint) {
        if (timer == CHARGED) {
            return getChargedElement(direction);
        } else {
            return super.state(player, alsoAtPoint);
        }
    }

    @Override
    public void tick() {
        if (timer == CHARGED) {
            timer = 0;

            int newX = direction.changeX(cell.getX());
            int newY = direction.changeY(cell.getY());

            field.move(newLaser(), newX, newY);
        } else {
            timer++;
        }
    }

    private Laser newLaser() {
        Laser laser = new Laser(direction);
        laser.init(field);
        return laser;
    }
}
