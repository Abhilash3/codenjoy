package com.epam.dojo.icancode.model;

import com.codenjoy.dojo.services.*;

/**
 * А вот тут немного хак :) Дело в том, что фреймворк изначально не поддерживал игры типа "все на однмо поле", а потому
 * пришлось сделать этот декоратор. Борда (@see Sample) - одна на всех, а игры (@see Single) у каждого своя. Кода тут не много.
 */
public class Single implements Game {

    private Printer printerLevel;
    private Printer printerElements;

    private Player player;
    private ICanCode game;

    public Single(ICanCode game, EventListener listener, PrinterFactory factory) {
        this.game = game;

        this.player = new Player(listener);

        this.printerLevel = factory.getPrinter(game.readLevel(), player);
        this.printerElements = factory.getPrinter(game.readElements(), player);
    }

    @Override
    public Joystick getJoystick() {
        return player.getHero();
    }

    @Override
    public int getMaxScore() {
        return player.getMaxScore();
    }

    @Override
    public int getCurrentScore() {
        return player.getScore();
    }

    @Override
    public boolean isGameOver() {
        return !player.hero.isAlive();
    }

    @Override
    public void newGame() {
        game.newGame(player);
    }

    @Override
    public String getBoardAsString() {
        return String.format("{\"layers\":[\"%s\",\"%s\"]}",
                printerLevel.print(),
                printerElements.print());
    }

    @Override
    public void destroy() {
        game.remove(player);
    }

    @Override
    public void clearScore() {
        player.clearScore();
    }

    @Override
    public Point getHero() {
        return player.getHero().getPosition();
    }

    @Override
    public void tick() {
        game.tick();
    }

    public Player getPlayer() {
        return player;
    }

}
