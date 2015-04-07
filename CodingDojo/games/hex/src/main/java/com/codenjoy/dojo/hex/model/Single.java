package com.codenjoy.dojo.hex.model;

import com.codenjoy.dojo.services.*;

import static com.codenjoy.dojo.services.PointImpl.pt;

public class Single implements Game {

    private Printer printer;
    private Player player;
    private Hex game;

    public Single(Hex game, EventListener listener, PrinterFactory factory) {
        this.game = game;
        this.player = new Player(listener, this.game);
        this.printer = factory.getPrinter(game.reader(), player);
    }

    @Override
    public Joystick getJoystick() {
        return player.getJoystick();
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
        return !player.isAlive();
    }

    @Override
    public void newGame() {
        game.newGame(player);
    }

    @Override
    public String getBoardAsString() {
        return printer.print();
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
        return pt(-1, -1); // TODO
    }

    @Override
    public void tick() {
        game.tick();
    }

    public Player getPlayer() {
        return player;
    }

}
