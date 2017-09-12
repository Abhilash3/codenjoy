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


import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.settings.Settings;
import com.codenjoy.dojo.utils.TestUtils;
import com.epam.dojo.expansion.model.levels.Levels;
import com.epam.dojo.expansion.model.levels.items.Hero;
import com.epam.dojo.expansion.services.GameRunner;
import com.epam.dojo.expansion.services.PrinterData;
import com.epam.dojo.expansion.services.SettingsWrapper;
import org.json.JSONObject;
import org.junit.Before;
import org.mockito.stubbing.OngoingStubbing;

import java.util.LinkedList;
import java.util.function.BiConsumer;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Sanja on 15.02.14.
 */
public class AbstractGameRunnerTest {

    public static final int LEVEL1 = 0;
    public static final int LEVEL2 = 1;
    public static final int LEVEL3 = 2;
    public static final int LEVEL4 = 3;

    protected LinkedList<Game> games;
    private GameRunner gameRunner;
    private PrinterFactoryImpl factory;
    private Dice dice;
    private EventListener listener;
    private Settings settings;

    @Before
    public void setup() {
        dice = mock(Dice.class);
        listener = mock(EventListener.class);

        gameRunner = new GameRunner();
        gameRunner.setDice(dice);

        games = new LinkedList<Game>();
        factory = new PrinterFactoryImpl();

        settings = gameRunner.getSettings();
        SettingsWrapper.data
                .lobbyEnable(false)
                .shufflePlayers(false);
    }


    protected void givenLv(String level, int index) {
        String name = "MULTI" + index + "_TEST"; // TODO to use StringWrapper
        settings.getParameter("Multiple level " + (index + 1)).update(name);
        Levels.put(name, level);
        int size = (int) Math.sqrt(level.length());
        settings.getParameter("Board size").update(size);
        settings.changesReacted();
    }

    protected JSONObject board(int player) {
        return (JSONObject)game(player).getBoardAsString();
    }

    protected Game game(int player) {
        return games.get(player);
    }

    protected void createNewGame(int levelOfRoom) {
        levelOrFreeRoom(levelOfRoom);
        createNewGame();
    }

    protected void createNewGame() {
        Game game = gameRunner.newGame(listener, factory, null);
        games.add(game);
    }

    protected void levelOrFreeRoom(int... levelOfRoom) {
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int i : levelOfRoom) {
            when = when.thenReturn(i);
        }
    }

    protected void gotoFreeRoom(int... room) {
        levelOrFreeRoom(room);
    }

    protected void assertE(String expected, int index) {
        Single single = (Single)game(index);
        assertEquals(expected,
                TestUtils.injectN(getBoardAsString(single).getLayers().get(1)));
    }

    protected void assertL(String expected, int index) {
        Single single = (Single)game(index);
        assertEquals(expected,
                TestUtils.injectN(getBoardAsString(single).getLayers().get(0)));
    }

    protected void assertF(String expected, int index) {
        Single single = (Single)game(index);
        assertEquals(expected,
                TestUtils.injectNN(getBoardAsString(single).getForces()));
    }

    protected PrinterData getBoardAsString(Single single) {
        return single.getPrinter().getBoardAsString(single.getPlayer());
    }

    protected void tickAll() {
        for (Game game : games) {
            if (game != null) {
                game.tick();
            }
        }
    }

    protected void doit(int times, Runnable whatToDo) {
        for (int i = 0; i < times; i++) {
            whatToDo.run();
            tickAll();
        }
    }

    protected void destroy(int player) {
        game(player).destroy();
        games.set(player, null);
    }

    protected Joystick goTimes(int player, Point pt, int times) {
        Hero hero = (Hero) joystick(player);
        BiConsumer<Hero, QDirection> command =
                (h, d) -> {
                    // increase and go to
                    h.increaseAndMove(new Forces(pt, 1), new ForcesMoves(pt, 1, d));
                    // change point so next turn from new place
                    pt.change(d);
                };

        return new Joystick() {
            @Override
            public void down() {
                doit(times, () -> command.accept(hero, QDirection.DOWN));
            }

            @Override
            public void up() {
                doit(times, () -> command.accept(hero, QDirection.UP));
            }

            @Override
            public void left() {
                doit(times, () -> command.accept(hero, QDirection.LEFT));
            }

            @Override
            public void right() {
                doit(times, () -> command.accept(hero, QDirection.RIGHT));
            }

            @Override
            public void act(int... p) {
                // do nothing
            }

            @Override
            public void message(String command) {
                // do nothing
            }
        };
    }

    private Joystick joystick(int player) {
        return games.get(player).getJoystick();
    }

    protected void givenLevels() {
        givenLv("╔════┐" +
                "║1..2│" +
                "║....│" +
                "║....│" +
                "║4..3│" +
                "└────┘", LEVEL1);

        givenLv("╔════┐" +
                "║..1.│" +
                "║4...│" +
                "║...2│" +
                "║.3..│" +
                "└────┘", LEVEL2);

        givenLv("╔════┐" +
                "║.1..│" +
                "║...2│" +
                "║4...│" +
                "║..3.│" +
                "└────┘", LEVEL3);

        givenLv("╔════┐" +
                "║....│" +
                "║.12.│" +
                "║.43.│" +
                "║....│" +
                "└────┘", LEVEL4);
    }
}
