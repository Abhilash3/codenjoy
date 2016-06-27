package com.epam.dojo.icancode.model;

import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PrinterFactory;
import com.epam.dojo.icancode.services.Levels;
import com.epam.dojo.icancode.services.Printer;

/**
 * А вот тут немного хак :) Дело в том, что фреймворк изначально не поддерживал игры типа "все на однмо поле", а потому
 * пришлось сделать этот декоратор. Борда (@see Sample) - одна на всех, а игры (@see Single) у каждого своя. Кода тут не много.
 */
public class Single implements Game {

    private ProgressBar progressBar;
    private Player player;

    public Single(ICanCode single, ICanCode multiple, EventListener listener, PrinterFactory factory) {
        progressBar = new ProgressBar(single, multiple);
        player = new Player(listener, progressBar);
        progressBar.setPlayer(player);
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
        progressBar.newGame(player);
    }

    @Override
    public String getBoardAsString() {
        return getPrinter().print(player);
    }

    @Override
    public void destroy() {
        progressBar.remove(player);
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
        progressBar.tick();
    }

    public Player getPlayer() {
        return player;
    }

    public Printer getPrinter() {
        return progressBar.getPrinter();
    }
}
