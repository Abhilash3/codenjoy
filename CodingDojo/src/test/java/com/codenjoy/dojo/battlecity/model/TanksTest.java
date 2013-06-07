package com.codenjoy.dojo.battlecity.model;

import static org.junit.Assert.*;

import com.codenjoy.dojo.services.Joystick;
import org.junit.Before;
import org.junit.Test;

public class TanksTest {

    public static int BATTLE_FIELD_SIZE = 7;

    private Tanks game;
    private Joystick tank;
    private Field field;

    public void givenGameWithConstruction(int x, int y) {
        game = new Tanks(BATTLE_FIELD_SIZE, new Construction(x, y), new Tank(1, 1, Direction.UP));
        tank = game.getJoystick();
        field = game.getField();
    }

    public void givenGameWithTankAt(int x, int y) {
        givenGameWithTankAt(x, y, Direction.UP);
    }

    public void givenGameWithTankAt(int x, int y, Direction direction) {
        game = new Tanks(BATTLE_FIELD_SIZE, new Construction(-1, -1), new Tank(x, y, direction));
        tank = game.getJoystick();
        field = game.getField();
    }

    @Before
    public void setup() {
        givenGameWithTankAt(1, 1);
    }

    @Test
    public void shouldDrawField() {
        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X▲****X\n" +
                "XXXXXXX\n");
    }

    private void assertDraw(String field) {
        assertEquals(field, game.getBoardAsString());
    }

    @Test
    public void shouldBeConstruction_whenGameCreated() {
        field.setConstruction(new Construction(3, 3));
        assertNotNull(field.getConstruction());

        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X**■**X\n" +
                "X*****X\n" +
                "X▲****X\n" +
                "XXXXXXX\n");
    }

    @Test
    public void shouldBeConstructionOnFieldAt00() {
        field.setTank(null);
        field.setConstruction(new Construction(1, 1));
        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X■****X\n" +
                "XXXXXXX\n");

    }

    @Test
    public void shouldBeTankOnFieldWhenGameCreated() {
        assertNotNull(field.getTank());
    }

    @Test
    public void shouldTankMove() {
        tank.up();

        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X▲****X\n" +
                "X*****X\n" +
                "XXXXXXX\n");

        tank.down();

        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X▼****X\n" +
                "XXXXXXX\n");

        tank.right();

        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*►***X\n" +
                "XXXXXXX\n");

        tank.left();

        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X◄****X\n" +
                "XXXXXXX\n");
    }

    @Test
    public void shouldTankStayAtPreviousPositionWhenIsNearBorder() {
        tank.down();
        tank.down();

        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X▼****X\n" +
                "XXXXXXX\n");


        tank.left();
        tank.left();

        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X◄****X\n" +
                "XXXXXXX\n");

        Tank someTank = new Tank(5, 5, Direction.UP);
        field.setTank(someTank);

        someTank.right();
        someTank.right();

        assertDraw(
                "XXXXXXX\n" +
                "X****►X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "XXXXXXX\n");

        someTank.up();
        someTank.up();

        assertDraw(
                "XXXXXXX\n" +
                "X****▲X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "XXXXXXX\n");
    }

    @Test
    public void shouldBulletHasSameDirectionAsTank() {
        tank.act();

        Tank realTank = (Tank) tank;
        assertEquals(realTank.getBullet().getDirection(), realTank.getDirection());
    }

    @Test
    public void shouldBulletGoInertiaWhenTankChangeDirection() {
        givenGameWithTankAt(3, 1);
        tank.act();
        game.tick();

        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X**•**X\n" +
                "X*****X\n" +
                "X**▲**X\n" +
                "XXXXXXX\n");

        tank.right();
        game.tick();

        assertDraw(
                "XXXXXXX\n" +
                "X**•**X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X***►*X\n" +
                "XXXXXXX\n");
    }

    @Test
    public void shouldBulletDisappear_whenHittingTheWallUp() {
        tank.act();
        game.tick();
        game.tick();
        assertDraw(
                "XXXXXXX\n" +
                "X•****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X▲****X\n" +
                "XXXXXXX\n");

        game.tick();
        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X▲****X\n" +
                "XXXXXXX\n");
    }

    @Test
    public void shouldBulletDisappear_whenHittingTheWallRight() {
        givenGameWithTankAt(1, 1, Direction.RIGHT);
        tank.act();
        game.tick();
        game.tick();
        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X►***•X\n" +
                "XXXXXXX\n");

        game.tick();
        assertDraw(
                "XXXXXXX\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X►****X\n" +
                "XXXXXXX\n");
    }

    @Test
    public void shouldBulletDisappear_whenHittingTheWallLeft() {
        givenGameWithTankAt(5, 5, Direction.LEFT);
        tank.act();
        game.tick();
        game.tick();
        assertDraw(
                "XXXXXXX\n" +
                "X•***◄X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "XXXXXXX\n");

        game.tick();
        assertDraw(
                "XXXXXXX\n" +
                "X****◄X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "XXXXXXX\n");
    }

    @Test
    public void shouldBulletDisappear_whenHittingTheWallDown() {
        givenGameWithTankAt(5, 5, Direction.DOWN);
        tank.act();
        game.tick();
        game.tick();
        assertDraw(
                "XXXXXXX\n" +
                "X****▼X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X****•X\n" +
                "XXXXXXX\n");

        game.tick();
        assertDraw(
                "XXXXXXX\n" +
                "X****▼X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "X*****X\n" +
                "XXXXXXX\n");
    }

}
