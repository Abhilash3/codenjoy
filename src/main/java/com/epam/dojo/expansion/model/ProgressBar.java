package com.epam.dojo.expansion.model;

/*-
 * #%L
 * iCanCode - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 EPAM
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.services.Game;
import com.epam.dojo.expansion.model.interfaces.ILevel;
import com.epam.dojo.expansion.model.levels.Levels;
import com.epam.dojo.expansion.services.Printer;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by oleksandr.baglai on 27.06.2016.
 */
public class ProgressBar {

    private GameFactory factory;
    private Player player;

    private int currentLevel;
    private int lastPassedLevel;
    private boolean finished;
    private boolean nextLevel;

    private Integer backToSingleLevel;

    private Expansion single;
    private Expansion current;
    private Printer printer;
    private Single gameOwner;

    public ProgressBar(GameFactory factory) {
        this.factory = factory;
        this.single = factory.get(Expansion.SINGLE);

        current = single;
        finished = false;
        backToSingleLevel = null;
        currentLevel = 0;
        lastPassedLevel = -1;
        loadLevel();
        buildPrinter();
    }

    public void setNextLevel() {
        nextLevel = true;
    }

    private void buildPrinter() {
        printer = new Printer(current, Levels.size());
    }

    public Integer getBackToSingleLevel() {
        Integer result = backToSingleLevel;
        backToSingleLevel = null;
        return result;
    }

    private void loadLevel() {
        ILevel level = current.getLevels().get(currentLevel);
        current.setLevel(level);

        if (!current.isMultiple() || (current.isMultiple() && current.getPlayers().isEmpty())) {
            level.setField(current);
        }
    }

    void checkLevel() {
        if (nextLevel) {
            nextLevel();
        } else if (!player.getHero().isAlive()) {
            createHeroToPlayer();
        } else if (player.getHero().isChangeLevel()) {
            changeLevel();
        }
    }

    protected void changeLevel() {
        int level = player.getHero().getLevel();
        if (!current.isMultiple()) {
            if (level == -1) {
                level = currentLevel;
            }
            if (level > lastPassedLevel + 1) {
                return;
            }
            if (level >= current.getLevels().size()) {
                finished = true;
                return;
            }
            currentLevel = level;
            loadLevel();
            createHeroToPlayer();
        } else if (level < single.getLevels().size()) {
            if (level != -1) {
                backToSingleLevel = level;
            } else {
                loadLevel();
                createHeroToPlayer();
            }
        }
    }

    //will go to next level
    protected void nextLevel() {
        if (currentLevel < current.getLevels().size() - 1) {
            if (lastPassedLevel < currentLevel) {
                lastPassedLevel = currentLevel;
            }
            currentLevel++;
            loadLevel();
        } else if (!current.isMultiple()) {
            if (lastPassedLevel < currentLevel) {
                lastPassedLevel = currentLevel;
            }
            finished = true;
        }
        createHeroToPlayer();
    }

    protected void createHeroToPlayer() {
        remove(player);
        newGame(player);
        nextLevel = false;
    }

    public void tick() {
        current.tick();
        if (isMultiple()) {
            Integer level = getBackToSingleLevel();
            if (level != null) {
                if (level > single.getLevels().size()) {
                    return;
                }
                loadSingle(level);
            }
        } else {
            if (finished) {
                loadMultiple();
            }
        }
    }

    private void loadMultiple() {
        remove(player);
        current = factory.get(Expansion.MULTIPLE);
        currentLevel = 0;
        loadLevel();
        buildPrinter();
        newGame(player);
    }

    private void loadSingle(Integer level) {
        remove(player);
        current = single;
        finished = false;
        buildPrinter();
        newGame(player);
        player.getHero().loadLevel(level);
        checkLevel();
    }

    // TODO test me
    public List<Game> getPlayerRoom() {
        List<Player> players = current.getPlayers();
        if (!isMultiple()) {
            if (players.size() != 1) {
                throw new IllegalArgumentException("Expected one player in single room!");
            }
            return Arrays.asList(players.get(0).getGame());
        }
        List<Game> result = new LinkedList<>();
        for (Player player : players) {
            result.add(player.getGame());
        }
        return result;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void newGame(Player player) {
        current.newGame(player);
    }

    public void remove(Player player) {
        current.remove(player);
    }

    public Printer getPrinter() {
        return printer;
    }

    public JSONObject printProgress() {
        JSONObject object = new JSONObject();
        object.put("current", currentLevel);
        object.put("lastPassed", lastPassedLevel);
        object.put("total", single.getLevels().size());
        object.put("multiple", isMultiple());
        object.put("scores", enableWinScore());
        return object;
    }

    public boolean isMultiple() {
        return current != single;
    }

    public void loadProgress(String save) {
        try {
            JSONObject object = new JSONObject(save);
            currentLevel = object.getInt("current");
            lastPassedLevel = object.getInt("lastPassed");
            boolean isMultiple = object.getBoolean("multiple");
            if (isMultiple) {
                loadMultiple();
            } else {
                loadSingle(currentLevel);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean enableWinScore() {
        return isMultiple() || (currentLevel > lastPassedLevel);
    }

    public Expansion getCurrent() {
        return current;
    }

    public void setGameOwner(Single gameOwner) {
        this.gameOwner = gameOwner;
    }

    public Single getGameOwner() {
        return gameOwner;
    }
}
