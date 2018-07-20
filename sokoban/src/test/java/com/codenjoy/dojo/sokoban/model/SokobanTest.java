package com.codenjoy.dojo.sokoban.model;

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
import com.codenjoy.dojo.sokoban.model.items.Hero;
import com.codenjoy.dojo.utils.TestUtils;
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

public class SokobanTest {

    private Sokoban game;
    private Hero hero;
    private Dice dice;
    private EventListener listener;
    private Player player;
    private PrinterFactory printer = new PrinterFactoryImpl();

    @Before
    public void setup() {
        dice = mock(Dice.class);
    }

    private void dice(int... ints) {
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int i : ints) {
            when = when.thenReturn(i);
        }
    }

    private void givenFl(String board) {
        LevelImpl level = new LevelImpl(board);
        Hero hero = level.getHero().get(0);

        game = new Sokoban(level, dice);
        listener = mock(EventListener.class);
        player = new Player(listener);
        game.newGame(player);
        player.hero = hero;
        hero.init(game);
        this.hero = game.getHeroes().get(0);
    }

    private void assertE(String expected) {
        assertEquals(TestUtils.injectN(expected),
                printer.getPrinter(game.reader(), player).print());
    }


//    DONE: initial map is created (map == walls && boxes && markes)
//    DONE: hero is moving in 4 directions (UP, DOWN, LEFT, RIGHT)
//    DONE: hero is not passing through the walls
//    DONE: hero can push boxes in 4 directions (UP, DOWN, LEFT, RIGHT)
//    DONE: hero cannot push boxes if wall is next element after a box in the direction of hero pushing.
//    TODO: if all boxes in the marks == win, next level.
//    TODO: If all boxes not in the marks and in the corners - lose.

    // initial map is created (map == walls && boxes)
    @Test
    public void shouldFieldWithBoxesAtStart() {

        givenFl("☼☼☼☼☼" +
                "☼■XX☼" +
                "☼ ■X☼" +
                "☼☺ ■☼" +
                "☼☼☼☼☼");

        assertE("☼☼☼☼☼" +
                "☼■XX☼" +
                "☼ ■X☼" +
                "☼☺ ■☼" +
                "☼☼☼☼☼");
    }

    // hero is moving in 4 directions (UP, DOWN, LEFT, RIGHT)
    @Test
    public void shouldMoveHeroin4Directioons() {
            givenFl("☼☼☼☼☼" +
                    "☼   ☼" +
                    "☼ ☺ ☼" +
                    "☼   ☼" +
                    "☼☼☼☼☼");

            hero.left();
            game.tick();
            assertE("☼☼☼☼☼" +
                    "☼   ☼" +
                    "☼☺  ☼" +
                    "☼   ☼" +
                    "☼☼☼☼☼");

            hero.right();
            game.tick();
            assertE("☼☼☼☼☼" +
                    "☼   ☼" +
                    "☼ ☺ ☼" +
                    "☼   ☼" +
                    "☼☼☼☼☼");

            hero.up();
            game.tick();
            assertE("☼☼☼☼☼" +
                    "☼ ☺ ☼" +
                    "☼   ☼" +
                    "☼   ☼" +
                    "☼☼☼☼☼");

            hero.down();
            game.tick();
            assertE("☼☼☼☼☼" +
                    "☼   ☼" +
                    "☼ ☺ ☼" +
                    "☼   ☼" +
                    "☼☼☼☼☼");
        }

//    hero is not passing through the walls
    @Test
        public void shouldNotMoveThroughTheWalls() {
                givenFl("☼☼☼" +
                               "☼☺☼" +
                               "☼☼☼");

                hero.left();
                game.tick();
                assertE("☼☼☼" +
                        "☼☺☼" +
                        "☼☼☼");

                hero.right();
                game.tick();
                assertE("☼☼☼" +
                "☼☺☼" +
                "☼☼☼");

                hero.up();
                game.tick();
                assertE("☼☼☼" +
                "☼☺☼" +
                "☼☼☼");

                hero.down();
                game.tick();
                assertE("☼☼☼" +
                "☼☺☼" +
                "☼☼☼");
            }


    //hero can push boxes in 4 directions (UP, DOWN, LEFT, RIGHT)
    @Test
    public void shouldPushBoxesIn4Direcitons(){
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ■■  ☼" +
                "☼ ■☺  ☼" +
                "☼  ■  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        hero.left();
        game.tick();
        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ■■  ☼" +
                "☼■☺   ☼" +
                "☼  ■  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");


        hero.up();
        game.tick();
        assertE("☼☼☼☼☼☼☼" +
                "☼ ■   ☼" +
                "☼ ☺■  ☼" +
                "☼■    ☼" +
                "☼  ■  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        hero.right();
        game.tick();
        assertE("☼☼☼☼☼☼☼" +
                "☼ ■   ☼" +
                "☼  ☺■ ☼" +
                "☼■    ☼" +
                "☼  ■  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        for (int i = 0; i<2;i++) {
            hero.down();
            game.tick();
        }
        assertE("☼☼☼☼☼☼☼" +
                "☼ ■   ☼" +
                "☼   ■ ☼" +
                "☼■    ☼" +
                "☼  ☺  ☼" +
                "☼  ■  ☼" +
                "☼☼☼☼☼☼☼");
    }


    //hero cannot push boxes if wall is next element after a box in the direction of hero pushing.
    @Test
    public void shouldNotPushBoxesIn4DirecitonsIfNextIsWall() {
        String testBoardInit="☼☼☼☼☼" +
                "☼ ■ ☼" +
                "☼■☺■☼" +
                "☼ ■ ☼" +
                "☼☼☼☼☼";
        givenFl(testBoardInit);

        hero.left();
        game.tick();
        assertE(testBoardInit);

        hero.up();
        game.tick();
        assertE(testBoardInit);

        hero.right();
        game.tick();
        assertE(testBoardInit);

        for (int i = 0; i<2;i++) {
            hero.down();
            game.tick();
        }
        assertE(testBoardInit);
    }

}

