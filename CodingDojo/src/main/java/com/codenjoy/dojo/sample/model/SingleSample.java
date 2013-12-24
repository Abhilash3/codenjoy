package com.codenjoy.dojo.sample.model;

import com.codenjoy.dojo.services.*;

/**
 * А вот тут немного хак :) Дело в том, что фреймворк изначально не поддерживал игры типа "все на однмо поле", а потому
 * пришлось сделать этот декоратор. Борда (@see Sample) - одна на всех, а игры (@see SingleSample) у каждого своя. Кода тут не много.
 * Самое непонятное, как по мне - {@see LazyJoystick} и {@see Ticker}.
 */
public class SingleSample implements Game {

    private Printer printer;
    private Player player;
    private Sample sample;
    private LazyJoystick joystick;
    private Ticker ticker;

    public SingleSample(Sample sample, Ticker ticker, EventListener listener) {
        this.sample = sample;
        this.ticker = ticker;
        this.player = new Player(listener);
        this.joystick = new LazyJoystick();
        this.printer = new Printer(sample, player);
    }

    @Override
    public Joystick getJoystick() {
        return joystick;
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
        sample.newGame(player);
        joystick.setJoystick(player.getHero());
    }

    @Override
    public String getBoardAsString() {
        return printer.toString();
    }

    @Override
    public void destroy() {
        sample.remove(player);
    }

    @Override
    public void clearScore() {
        player.clearScore();
    }

    @Override
    public void tick() {
        if (ticker.collectTicks()) return;

        sample.tick();
    }

    public Player getPlayer() {
        return player;
    }

}
