package com.codenjoy.dojo.a2048.services;

import com.codenjoy.dojo.a2048.model.A2048;
import com.codenjoy.dojo.a2048.model.SingleA2048;
import com.codenjoy.dojo.a2048.model.*;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import org.apache.commons.lang.StringUtils;

import static com.codenjoy.dojo.services.settings.SimpleParameter.v;

public class A2048Game implements GameType {

    private final Settings settings;
    private final Parameter<Integer> size;
    private final Parameter<Integer> newAdd;

    private Level level;
    private A2048 game;

    public A2048Game() {
        settings = new SettingsImpl();
        new A2048PlayerScores(0, settings);
        size = settings.addEditBox("Size").type(Integer.class).def(5);
        newAdd = settings.addEditBox("New numbers").type(Integer.class).def(3);
    }

    private A2048 newGame() {
        Integer sizeValue = size.type(Integer.class).getValue();
        level = new LevelImpl(StringUtils.leftPad("", sizeValue*sizeValue, ' '));
        return new A2048(level, newAdd.getValue(), new RandomDice());
    }

    @Override
    public PlayerScores getPlayerScores(int score) {
        return new A2048PlayerScores(score, settings);
    }

    @Override
    public Game newGame(EventListener listener) {
        game = newGame();

        Game game = new SingleA2048(this.game, listener);
        game.newGame();
        return game;
    }

    @Override
    public Parameter<Integer> getBoardSize() {
        return v(level.getSize());
    }

    @Override
    public String gameName() {
        return "a2048";
    }

    @Override
    public Enum[] getPlots() {
        return Elements.values();
    }

    @Override
    public Settings getGameSettings() {
        return settings;
    }

    @Override
    public boolean isSingleBoardGame() {
        return false;
    }
}
