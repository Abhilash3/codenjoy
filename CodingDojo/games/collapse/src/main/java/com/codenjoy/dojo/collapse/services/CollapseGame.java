package com.codenjoy.dojo.collapse.services;

import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.collapse.client.Board;
import com.codenjoy.dojo.collapse.client.ai.ApofigDirectionSolver;
import com.codenjoy.dojo.collapse.model.*;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;
import com.codenjoy.dojo.services.settings.SettingsImpl;

public class CollapseGame implements GameType {

    private final Settings settings;
    private final Parameter<Integer> size;

    public CollapseGame() {
        settings = new SettingsImpl();
        new CollapsePlayerScores(0, settings);
        size = settings.addEditBox("Field size").type(Integer.class).def(30);
    }

    @Override
    public PlayerScores getPlayerScores(int score) {
        return new CollapsePlayerScores(score, settings);
    }

    @Override
    public Game newGame(EventListener listener, PrinterFactory factory) {
        Integer size = settings.getParameter("Field size").type(Integer.class).getValue();
        LevelBuilder builder = new LevelBuilder(new RandomDice(), size);
        Level level = new LevelImpl(builder.getBoard());
        Collapse collapse = new Collapse(level, new RandomDice());

        Game game = new SingleCollapse(collapse, listener, settings, factory);
        game.newGame();
        return game;
    }

    @Override
    public Parameter<Integer> getBoardSize() {
        return size;
    }

    @Override
    public String name() {
        return "collapse";
    }

    @Override
    public Enum[] getPlots() {
        return Elements.values();
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public boolean isSingleBoard() {
        return false;
    }

    @Override
    public void newAI(String aiName) {
        ApofigDirectionSolver.start(aiName, WebSocketRunner.Host.REMOTE);
    }
}
