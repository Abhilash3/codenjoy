package com.codenjoy.dojo.quadro.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 Codenjoy
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


import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.utils.TestUtils;
import com.codenjoy.dojo.quadro.services.Events;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class QuadroTest {

    private Quadro game;
    private Hero hero;
    private Dice dice;
    private EventListener listener;
    private Player player;
    private PrinterFactory printer = new PrinterFactoryImpl();

    @Before
    public void setup() {
        dice = mock(Dice.class);
    }

    private void dice(int...ints) {
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int i : ints) {
            when = when.thenReturn(i);
        }
    }

    private void givenFl(String board) {
        Level level = new LevelImpl(board);
        game = new Quadro(level, dice);
        listener = mock(EventListener.class);
        player = new Player(listener);
        game.newGame(player);
        hero = game.getHeroes().get(0);
    }

    private void assertE(String expected) {
        assertEquals(TestUtils.injectN(expected),
                printer.getPrinter(game.reader(), player).print());
    }

    // изначально иницализируется пустое поле 7х6
    @Test
    public void shouldFieldAtStart() {
        givenFl( "      " +
                        "      " +
                        "      " +
                        "      " +
                        "      " +
                        "      ");

        assertE("      " +
                         "      " +
                         "      " +
                         "      " +
                         "      " +
                         "      ");

    }

    //я могу походить только заполнив один из нижних рядов
    @Test
    public void shouldPutChipOnBottomLine() {
        givenFl( "      " +
                        "      " +
                        "      " +
                        "      " +
                        "      " +
                        "      ");

        hero.act(2);
        game.tick();

        assertE("      " +
                         "      " +
                         "      " +
                         "      " +
                         "      " +
                         "     x");

    }

    //соперник может положить фишку в ряд, который я изначально положил,либо в оставшиеся из 6 вертикальных рядов
    public void firstEnemyTurn() {
        givenFl( "      " +
                        "      " +
                        "      " +
                        "      " +
                        "      " +
                        "     x");

        hero.act(2);
        game.tick();

        assertE("      " +
                         "      " +
                         "      " +
                         "      " +
                         "      " +
                         "    ox");

    }

    //TODO соперник может положить фишку в ряд, который я изначально положил,либо в оставшиеся из 6 вертикальных рядов
    //TODO я выигрываю в случае когда 4 мои фишки подряд будут выстроены в линию или по диагонали
    //TODO я проигрываю в случае когда 4 фишки соперника подряд будут выстроены в линию либо по диагонали
}
