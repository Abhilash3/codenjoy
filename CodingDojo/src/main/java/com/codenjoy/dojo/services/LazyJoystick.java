package com.codenjoy.dojo.services;

/**
 * Когда пользователь зарегистрировался в игре создается новая игра в движке и джойстик игрока где-то там сохраняется во фреймворке.
 * Часто джойстик неразрывно связан с героем игрока, который бегает по полю. Так вот если этот герой помрет, и на его место появится новый
 * надо как-то вот тот изначально сохраненный в недрах фреймворка джойстик обновить. Приходится дергать постоянно game.
 */
public class LazyJoystick implements Joystick {

    private final Game game;
    private PlayerSpy player;

    public LazyJoystick(Game game, PlayerSpy player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void down() {
        player.act();
        game.getJoystick().down();
    }

    @Override
    public void up() {
        player.act();
        game.getJoystick().up();
    }

    @Override
    public void left() {
        player.act();
        game.getJoystick().left();
    }

    @Override
    public void right() {
        player.act();
        game.getJoystick().right();
    }

    @Override
    public void act(int... p) {
        player.act();
        game.getJoystick().act(p);
    }
}
