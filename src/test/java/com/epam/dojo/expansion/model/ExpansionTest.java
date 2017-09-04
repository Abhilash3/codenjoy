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


import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.QDirection;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.utils.TestUtils;
import com.epam.dojo.expansion.model.items.Hero;
import com.epam.dojo.expansion.model.levels.OneMultipleGameFactory;
import com.epam.dojo.expansion.services.Events;
import com.epam.dojo.expansion.model.levels.Levels;
import com.epam.dojo.expansion.services.Printer;
import com.epam.dojo.expansion.services.PrinterData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * User: sanja
 * Date: 17.12.13
 * Time: 4:47
 */
public class ExpansionTest {

    public static final int FIRE_TICKS = 6;
    private Expansion game;
    private Printer printer;

    private Hero hero;
    private Dice dice;
    private EventListener listener;
    private Player player;

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

    private void givenFl(String... boards) {
        Levels.VIEW_SIZE = Levels.VIEW_SIZE_TESTING;

        GameFactory factory = new OneMultipleGameFactory(dice,
                Levels.collectYours(boards),
                Levels.none());
        listener = mock(EventListener.class);
        ProgressBar progressBar = new ProgressBar(factory);
        player = new Player(listener, progressBar);
        progressBar.newGame(player);
        game = progressBar.getCurrent();
        hero = game.getHeroes().get(0);

        printer = new Printer(game, Levels.size());
    }


    private void assertL(String expected) {
        assertEquals(TestUtils.injectN(expected),
                TestUtils.injectN(getBoardAsString().getLayers().get(0)));
    }

    private PrinterData getBoardAsString() {
        return printer.getBoardAsString(Printer.LAYERS_TOTAL, player);
    }

    private void assertE(String expected) {
        assertEquals(TestUtils.injectN(expected),
                TestUtils.injectN(getBoardAsString().getLayers().get(1)));
    }

    private void assertF(String expected) {
        assertEquals(expected,
                TestUtils.injectN2(getBoardAsString().getForces()));
    }

    @Test
    public void shouldFieldAtStart() {
        // given
        givenFl("╔═════════┐" +
                "║.........│" +
                "║.1.┌─╗...│" +
                "║...│ ║...│" +
                "║.┌─┘ └─╗.│" +
                "║.│     ║.│" +
                "║.╚═┐ ╔═╝.│" +
                "║...│ ║...│" +
                "║...╚═╝...│" +
                "║........E│" +
                "└─────────┘");

        // then
        assertL("╔═════════┐" +
                "║.........│" +
                "║.1.┌─╗...│" +
                "║...│ ║...│" +
                "║.┌─┘ └─╗.│" +
                "║.│     ║.│" +
                "║.╚═┐ ╔═╝.│" +
                "║...│ ║...│" +
                "║...╚═╝...│" +
                "║........E│" +
                "└─────────┘");

        assertE("-----------" +
                "-----------" +
                "--♥--------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------" +
                "-----------");

        assertF("-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=0A-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=\n");
    }

    @Test
    public void shouldIncreaseExistingForces() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.increase(new Forces(pt(2, 2), 1));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0B-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.increase(new Forces(pt(2, 2), 3));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0E-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.increase(new Forces(pt(2, 2), 5));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertL("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0J-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldIncreaseExistingForces_notMoreThan() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.increase(new Forces(pt(2, 2), 100));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0K-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldDoNothingWhenNoCommands() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        assertL("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");
    }

    @Test
    public void shouldMoveForces_down() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), 6, QDirection.DOWN));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "--♥--" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=04-=-=\n" +
                "-=-=06-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldMoveForces_up() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), 6, QDirection.UP));
        game.tick();

        // then
        assertE("-----" +
                "--♥--" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=06-=-=\n" +
                "-=-=04-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldMoveForces_left() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), 6, QDirection.LEFT));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "-♥♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=0604-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldMoveForces_right() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), 6, QDirection.RIGHT));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0406-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldMoveForces_leftUp() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), 6, QDirection.LEFT_UP));
        game.tick();

        // then
        assertE("-----" +
                "-♥---" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=06-=-=-=\n" +
                "-=-=04-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldMoveForces_leftDown() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), 6, QDirection.LEFT_DOWN));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-♥---" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=04-=-=\n" +
                "-=06-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldMoveForces_rightUp() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), 6, QDirection.RIGHT_UP));
        game.tick();

        // then
        assertE("-----" +
                "---♥-" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=06-=\n" +
                "-=-=04-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldMoveForces_rightDown() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), 6, QDirection.RIGHT_DOWN));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "---♥-" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=04-=-=\n" +
                "-=-=-=06-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldMoveForces_allSidesSameTime() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(
                new ForcesMoves(pt(2, 2), 1, QDirection.LEFT),
                new ForcesMoves(pt(2, 2), 1, QDirection.RIGHT),
                new ForcesMoves(pt(2, 2), 1, QDirection.UP),
                new ForcesMoves(pt(2, 2), 1, QDirection.DOWN),
                new ForcesMoves(pt(2, 2), 1, QDirection.RIGHT_DOWN),
                new ForcesMoves(pt(2, 2), 1, QDirection.RIGHT_UP),
                new ForcesMoves(pt(2, 2), 1, QDirection.LEFT_DOWN),
                new ForcesMoves(pt(2, 2), 1, QDirection.LEFT_UP)
        );
        game.tick();

        // then
        assertE("-----" +
                "-♥♥♥-" +
                "-♥♥♥-" +
                "-♥♥♥-" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=010101-=\n" +
                "-=010201-=\n" +
                "-=010101-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldCantMoveForcesOnWall() {
        // given
        givenFl("╔═┐" +
                "║1│" +
                "└─┘");

        assertF("-=-=-=\n" +
                "-=0A-=\n" +
                "-=-=-=\n");

        // when
        hero.move(
                new ForcesMoves(pt(1, 1), 1, QDirection.LEFT),
                new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT),
                new ForcesMoves(pt(1, 1), 1, QDirection.UP),
                new ForcesMoves(pt(1, 1), 1, QDirection.DOWN),
                new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT_DOWN),
                new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT_UP),
                new ForcesMoves(pt(1, 1), 1, QDirection.LEFT_DOWN),
                new ForcesMoves(pt(1, 1), 1, QDirection.LEFT_UP)
        );
        game.tick();

        // then
        assertE("---" +
                "-♥-" +
                "---");

        assertF("-=-=-=\n" +
                "-=0A-=\n" +
                "-=-=-=\n");

        assertL("╔═┐" +
                "║1│" +
                "└─┘");
    }

    @Test
    public void shouldCantMoveForcesOnWall_otherWalls() {
        // given
        givenFl("┌─╗" +
                "│1│" +
                "╚═╝");

        assertF("-=-=-=\n" +
                "-=0A-=\n" +
                "-=-=-=\n");

        // when
        hero.move(
                new ForcesMoves(pt(1, 1), 1, QDirection.LEFT),
                new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT),
                new ForcesMoves(pt(1, 1), 1, QDirection.UP),
                new ForcesMoves(pt(1, 1), 1, QDirection.DOWN),
                new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT_DOWN),
                new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT_UP),
                new ForcesMoves(pt(1, 1), 1, QDirection.LEFT_DOWN),
                new ForcesMoves(pt(1, 1), 1, QDirection.LEFT_UP)
        );
        game.tick();

        // then
        assertE("---" +
                "-♥-" +
                "---");

        assertF("-=-=-=\n" +
                "-=0A-=\n" +
                "-=-=-=\n");

        assertL("┌─╗" +
                "│1│" +
                "╚═╝");
    }

    @Test
    public void shouldWin() {
        // given
        givenFl("     " +
                "╔═══┐" +
                "║.1E│" +
                "└───┘" +
                "     ");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), 1, QDirection.RIGHT));
        game.tick();

        // then
        verify(listener).event(Events.WIN(0));

        assertE("-----" +
                "-----" +
                "--♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0901-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        assertL("     " +
                "╔═══┐" +
                "║.1E│" +
                "└───┘" +
                "     ");
    }

    @Test
    public void shouldNewGameAfterWin() {
        // given
        givenFl("     " +
                "╔═══┐" +
                "║.1E│" +
                "└───┘" +
                "     ");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), 1, QDirection.RIGHT));
        game.tick();

        // then
        assertTrue(hero.isWin());

        // when
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        assertL("     " +
                "╔═══┐" +
                "║.1E│" +
                "└───┘" +
                "     ");

        assertFalse(hero.isWin());
    }

    @Test
    public void shouldStartInNewPlaceWhenAct() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║1..│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=0A-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(1, 2), 2, QDirection.RIGHT));
        game.tick();

        hero.move(new ForcesMoves(pt(2, 2), 1, QDirection.RIGHT));
        game.tick();

        assertE("-----" +
                "-----" +
                "-♥♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=080101-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.reset();

        assertE("-----" +
                "-----" +
                "-♥♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=080101-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "-♥---" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=0A-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldCantMoveForcesFromEmptySpace() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(
                new ForcesMoves(pt(1, 1), 6, QDirection.DOWN),
                new ForcesMoves(pt(3, 3), 6, QDirection.LEFT),
                new ForcesMoves(pt(1, 3), 6, QDirection.UP),
                new ForcesMoves(pt(3, 1), 6, QDirection.RIGHT),
                new ForcesMoves(pt(2, 1), 6, QDirection.LEFT_UP),
                new ForcesMoves(pt(2, 3), 6, QDirection.RIGHT_DOWN),
                new ForcesMoves(pt(1, 2), 6, QDirection.RIGHT_UP),
                new ForcesMoves(pt(3, 2), 6, QDirection.LEFT_DOWN)
        );
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldCantMoveMoreThanExistingSoOneMustBeLeaved() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), 10, QDirection.DOWN));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "--♥--" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=01-=-=\n" +
                "-=-=09-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldCantMoveMoreThanExistingSoOneMustBeLeaved_caseMultipleMovements() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        hero.move(
                new ForcesMoves(pt(2, 2), 3, QDirection.LEFT),
                new ForcesMoves(pt(2, 2), 3, QDirection.RIGHT)
        );
        game.tick();

        assertE("-----" +
                "-----" +
                "-♥♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=030403-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(
                new ForcesMoves(pt(1, 2), 5, QDirection.UP),
                new ForcesMoves(pt(2, 2), 5, QDirection.UP),
                new ForcesMoves(pt(3, 2), 5, QDirection.UP)
        );
        game.tick();

        // then
        assertE("-----" +
                "-♥♥♥-" +
                "-♥♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=020302-=\n" +
                "-=010101-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    // я могу переместить на то место где уже что-то есть, тогда армии сольются
    @Test
    public void shouldCantMergeForces() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        hero.move(
                new ForcesMoves(pt(2, 2), 3, QDirection.LEFT),
                new ForcesMoves(pt(2, 2), 3, QDirection.RIGHT)
        );
        game.tick();

        assertE("-----" +
                "-----" +
                "-♥♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=030403-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(
                new ForcesMoves(pt(1, 2), 2, QDirection.RIGHT),
                new ForcesMoves(pt(3, 2), 2, QDirection.LEFT)
        );
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "-♥♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=010801-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    // я не могу увеличить количество войск на пустом месте
    @Test
    public void shouldCantIncreaseNonExistingForces() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.increase(
                new Forces(pt(1, 1), 1),
                new Forces(pt(1, 3), 5),
                new Forces(pt(3, 1), 10),
                new Forces(pt(3, 3), 100)
        );
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    // если на месте осталось 1 войско и я увеличил в следующем тике, то сейчас я снова могу перемещать
    @Test
    public void shouldCanMoveForceAfterIncrease() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(2, 2), 9, QDirection.DOWN));
        game.tick();

        assertE("-----" +
                "-----" +
                "--♥--" +
                "--♥--" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=01-=-=\n" +
                "-=-=09-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        // try but fail
        hero.move(new ForcesMoves(pt(2, 2), 2, QDirection.UP));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "--♥--" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=01-=-=\n" +
                "-=-=09-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.increaseAndMove(
                new Forces(pt(2, 2), 5),
                new ForcesMoves(pt(2, 2), 2, QDirection.UP)
        );
        game.tick();

        // then
        assertE("-----" +
                "--♥--" +
                "--♥--" +
                "--♥--" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=02-=-=\n" +
                "-=-=04-=-=\n" +
                "-=-=09-=-=\n" +
                "-=-=-=-=-=\n");
    }

    // я не могу оперировать в перемещении отрицательным числом войск
    @Test
    public void shouldCantMoveNegativeForces() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 2), -1, QDirection.DOWN));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    // я не могу оперировать в добавлении отрицательного числа войск
    @Test
    public void shouldCantIncreaseNegativeForces() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.increase(new Forces(pt(2, 2), -1));
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    // если я делаю какие-то перемещения, то я не могу переместить с только что перемещенного до тика
    @Test
    public void shouldCantMoveJustMovedForces() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(
                new ForcesMoves(pt(2, 2), 4, QDirection.DOWN), // can do this
                new ForcesMoves(pt(2, 1), 2, QDirection.LEFT)  // cant do this
        );
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "--♥--" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=06-=-=\n" +
                "-=-=04-=-=\n" +
                "-=-=-=-=-=\n");
    }

    // не брать во внимание перемещения войск без указания direction
    @Test
    public void shouldCantMoveWithoutDirections() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(
                new ForcesMoves(pt(2, 2), 2, QDirection.DOWN), // can do this
                new Forces(pt(2, 2), 2)  // cant do this
        );
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "--♥--" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=08-=-=\n" +
                "-=-=02-=-=\n" +
                "-=-=-=-=-=\n");
    }

    // не брать во внимания direction во время увеличения числа войск
    @Test
    public void shouldIgnoreDirectionWhenIncreaseForces() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.increase(new ForcesMoves(pt(2, 2), 10, QDirection.DOWN)); // ignore direction
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0K-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    // я могу переместить в другое место только что выставленные войска
    @Test
    public void shouldCanMoveJustIncreased() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.increaseAndMove(
                new Forces(pt(2, 2), 20),
                new ForcesMoves(pt(2, 2), 19, QDirection.DOWN)
        );
        game.tick();

        // then
        assertE("-----" +
                "-----" +
                "--♥--" +
                "--♥--" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=01-=-=\n" +
                "-=-=0J-=-=\n" +
                "-=-=-=-=-=\n");
    }

    // я могу увеличивать армии всего на заданное число, а не каждую отдельную
    @Test
    public void shouldIncreaseExistingForces_notMoreThan_totalForAllArmies() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        hero.move(
                new ForcesMoves(pt(2, 2), 1, QDirection.LEFT),
                new ForcesMoves(pt(2, 2), 1, QDirection.RIGHT)
        );
        game.tick();

        assertE("-----" +
                "-----" +
                "-♥♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=010801-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.increase(
                new Forces(pt(1, 2), 4),
                new Forces(pt(2, 2), 4),
                new Forces(pt(3, 2), 4)
        );
        game.tick();

        assertE("-----" +
                "-----" +
                "-♥♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=050C03-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldIncreaseExistingForces_notMoreThan_totalForAllArmies_case2() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║.1.│" +
                "║...│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        hero.move(
                new ForcesMoves(pt(2, 2), 1, QDirection.LEFT),
                new ForcesMoves(pt(2, 2), 1, QDirection.RIGHT)
        );
        game.tick();

        assertE("-----" +
                "-----" +
                "-♥♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=010801-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.increase(
                new Forces(pt(1, 2), 10),
                new Forces(pt(2, 2), 10),
                new Forces(pt(3, 2), 10)
        );
        game.tick();

        assertE("-----" +
                "-----" +
                "-♥♥♥-" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=0B0801-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

    @Test
    public void shouldNextLevelWhenFinishCurrent() {
        // given
        givenFl("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘",
                "╔══┐" +
                "║1.│" +
                "║E.│" +
                "└──┘");

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(1, 2), 1, QDirection.RIGHT));
        game.tick();

        // then
        verify(listener).event(Events.WIN(0));

        assertL("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘");

        assertE("----" +
                "-♥♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0901-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when
        game.tick();

        // then
        assertL("╔══┐" +
                "║1.│" +
                "║E.│" +
                "└──┘");

        assertE("----" +
                "-♥--" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");
    }

    @Test
    public void shouldAllLevelsAreDone() {
        // given
        givenFl("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘",
                "╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘",
                "╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘",
                "╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘"
        );

        assertL("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘");

        assertE("----" +
                "-♥--" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when done 1 level - go to 2
        hero.move(new ForcesMoves(pt(1, 2), 1, QDirection.RIGHT));
        game.tick();
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "--♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when done 2 level - go to 3
        hero.move(new ForcesMoves(pt(2, 2), 1, QDirection.DOWN));
        game.tick();
        game.tick();

        // then
        assertL("╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘");

        assertE("----" +
                "----" +
                "--♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n");

        // when done 3 level - go to 4
        hero.move(new ForcesMoves(pt(2, 1), 1, QDirection.LEFT));
        game.tick();
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        // when done 4 level - start 4 again
        hero.move(new ForcesMoves(pt(1, 1), 1, QDirection.UP));
        game.tick();
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        // when done 4 level - start 4 again
        hero.move(new ForcesMoves(pt(1, 1), 1, QDirection.UP));
        game.tick();
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");
    }

    @Test
    public void shouldChangeLevelWhenAllLevelsAreDone() {
        // given
        shouldAllLevelsAreDone();

        // when try to change level 1  - success
        hero.loadLevel(0);
        game.tick();

        // then
        assertL("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘");

        assertE("----" +
                "-♥--" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 4 - success
        hero.loadLevel(3);
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 2  - success
        hero.loadLevel(1);
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "--♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 4 - success
        hero.loadLevel(3);
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 3  - success
        hero.loadLevel(2);
        game.tick();

        // then
        assertL("╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘");

        assertE("----" +
                "----" +
                "--♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n");

        // when try to change level 4 - success
        hero.loadLevel(3);
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 500 - fail
        hero.move(new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT));
        game.tick();
        hero.loadLevel(500);
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0901-=\n" +
                "-=-=-=-=\n");

        // when try to change level 2 - success
        hero.loadLevel(1);
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "--♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");
    }

    @Test
    public void shouldWinOnPassedLevelThanCanSelectAnother() {
        // given
        shouldAllLevelsAreDone();

        // when win on level then try to change to last - success
        hero.loadLevel(1);
        game.tick();

        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "--♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(2, 2), 1, QDirection.DOWN));

        game.tick();
        game.tick();

        // then
        assertL("╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘");

        assertE("----" +
                "----" +
                "--♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n");

        // when try to change level 4 - success
        hero.loadLevel(3);
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");
    }

    @Test
    public void shouldWinOnPassedLevelThanCanSelectAnother_caseWhenGoToMultiple() {
        // given
        shouldAllLevelsAreDone();

        // when win on level then try to change to last - success
        hero.loadLevel(2);
        game.tick();

        assertL("╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘");

        assertE("----" +
                "----" +
                "--♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(2, 1), 1, QDirection.LEFT));
        game.tick();
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 4 - success
        hero.loadLevel(3);
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");
    }

    @Test
    public void shouldWinOnPassedLevelThanCanSelectAnother_caseGoFromMultiple() {
        // given
        shouldAllLevelsAreDone();

        // when win on level then try to change to last - success
        hero.loadLevel(3);
        game.tick();

        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT));
        game.tick();
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0901-=\n" +
                "-=-=-=-=\n");

        // when try to change level 3 (previous) - success
        hero.loadLevel(2);
        game.tick();

        // then
        assertL("╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘");

        assertE("----" +
                "----" +
                "--♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n");
    }

    @Test
    public void shouldResetLevelWhenAllLevelsAreDone() {
        // given
        givenFl("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘",
                "╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘",
                "╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘",
                "╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘"
        );

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(1, 2), 1, QDirection.DOWN));
        game.tick();

        assertL("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘");

        assertE("----" +
                "-♥--" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=09-=-=\n" +
                "-=01-=-=\n" +
                "-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘");

        assertE("----" +
                "-♥--" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when done 1 level - go to 2
        hero.move(new ForcesMoves(pt(1, 2), 1, QDirection.RIGHT));
        game.tick();
        game.tick();

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(2, 2), 1, QDirection.LEFT));
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "-♥♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0109-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "--♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when done 2 level - go to 3
        hero.move(new ForcesMoves(pt(2, 2), 1, QDirection.DOWN));
        game.tick();
        game.tick();

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(2, 1), 1, QDirection.UP));
        game.tick();

        // then
        assertL("╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘");

        assertE("----" +
                "--♥-" +
                "--♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=01-=\n" +
                "-=-=09-=\n" +
                "-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘");

        assertE("----" +
                "----" +
                "--♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n");

        // when done 3 level - go to 4
        hero.move(new ForcesMoves(pt(2, 1), 1, QDirection.LEFT));
        game.tick();
        game.tick();

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT));
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0901-=\n" +
                "-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        // when done 4 level - start 4 again
        hero.move(new ForcesMoves(pt(1, 1), 1, QDirection.UP));
        game.tick();
        game.tick();

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT));
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0901-=\n" +
                "-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 1  - success
        hero.loadLevel(0);
        game.tick();
        game.tick();

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(1, 2), 1, QDirection.DOWN));
        game.tick();

        // then
        assertL("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘");

        assertE("----" +
                "-♥--" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=09-=-=\n" +
                "-=01-=-=\n" +
                "-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘");

        assertE("----" +
                "-♥--" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 2  - success
        hero.loadLevel(1);
        game.tick();
        game.tick();

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(2, 2), 1, QDirection.LEFT));
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "-♥♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0109-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "--♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 3  - success
        hero.loadLevel(2);
        game.tick();
        game.tick();

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(2, 1), 1, QDirection.UP));
        game.tick();

        // then
        assertL("╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘");

        assertE("----" +
                "--♥-" +
                "--♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=01-=\n" +
                "-=-=09-=\n" +
                "-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘");

        assertE("----" +
                "----" +
                "--♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n");

        // when try to change level 4 - success
        hero.loadLevel(3);
        game.tick();
        game.tick();

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT));
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0901-=\n" +
                "-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 500 - fail
        hero.move(new ForcesMoves(pt(1, 1), 1, QDirection.RIGHT));
        game.tick();
        game.tick();

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0901-=\n" +
                "-=-=-=-=\n");

        hero.loadLevel(500);
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥♥-" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0901-=\n" +
                "-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘");

        assertE("----" +
                "----" +
                "-♥--" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 2 - success
        hero.loadLevel(1);
        game.tick();
        game.tick();

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(2, 2), 1, QDirection.LEFT));
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "-♥♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0109-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "--♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");
    }

    @Test
    public void shouldSelectLevelWhenNotAllLevelsAreDone() {
        // given
        givenFl("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘",
                "╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘",
                "╔══┐" +
                "║..│" +
                "║E1│" +
                "└──┘",
                "╔══┐" +
                "║E.│" +
                "║1.│" +
                "└──┘"
        );

        assertL("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘");

        assertE("----" +
                "-♥--" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when done level 1 - go to level 2
        hero.move(new ForcesMoves(pt(1, 2), 1, QDirection.RIGHT));
        game.tick();
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "--♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level to 1 - success
        hero.loadLevel(0);
        game.tick();

        // then
        assertL("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘");

        assertE("----" +
                "-♥--" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level to 2 - success
        hero.loadLevel(1);
        game.tick();

        assertF("-=-=-=-=\n" +
                "-=-=0A-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(2, 2), 1, QDirection.LEFT));
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "-♥♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0109-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level to 3 - fail
        hero.loadLevel(2);
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "-♥♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0109-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level 4 - fail
        hero.loadLevel(3);
        game.tick();

        // then
        assertL("╔══┐" +
                "║.1│" +
                "║.E│" +
                "└──┘");

        assertE("----" +
                "-♥♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0109-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when try to change level to 1 - success
        hero.loadLevel(0);
        game.tick();

        // then
        assertL("╔══┐" +
                "║1E│" +
                "║..│" +
                "└──┘");

        assertE("----" +
                "-♥--" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");
    }

    @Test
    public void shouldAfterNextLevelHeroCanMove() {
        // given
        shouldNextLevelWhenFinishCurrent();

        assertL("╔══┐" +
                "║1.│" +
                "║E.│" +
                "└──┘");

        assertE("----" +
                "-♥--" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0A-=-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(1, 2), 1, QDirection.RIGHT));
        game.tick();

        // then
        assertL("╔══┐" +
                "║1.│" +
                "║E.│" +
                "└──┘");

        assertE("----" +
                "-♥♥-" +
                "----" +
                "----");

        assertF("-=-=-=-=\n" +
                "-=0901-=\n" +
                "-=-=-=-=\n" +
                "-=-=-=-=\n");
    }

    @Test
    public void shouldOneMoreArmyToTheMaxCountWhenGetGold() {
        // given
        givenFl("╔═══┐" +
                "║...│" +
                "║1$E│" +
                "└───┘" +
                "     ");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=0A-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        assertEquals(10, hero.getForcesPerTick());

        hero.move(new ForcesMoves(pt(1, 2), 1, QDirection.RIGHT)); // pick up gold
        game.tick();

        assertL("╔═══┐" +
                "║...│" +
                "║1.E│" +
                "└───┘" +
                "     ");

        assertE("-----" +
                "-----" +
                "-♥♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=0901-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        assertEquals(11, hero.getForcesPerTick());

        // when
        hero.increase(new Forces(pt(2, 2), 20)); // only 11 can increase
        game.tick();

        // then
        assertL("╔═══┐" +
                "║...│" +
                "║1.E│" +
                "└───┘" +
                "     ");

        assertE("-----" +
                "-----" +
                "-♥♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=090C-=-=\n" + // 1+11
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        assertEquals(11, hero.getForcesPerTick());

        // when
        hero.increase(new Forces(pt(2, 2), 20)); // only 11 can increase
        game.tick();

        // then
        assertL("╔═══┐" +
                "║...│" +
                "║1.E│" +
                "└───┘" +
                "     ");

        assertE("-----" +
                "-----" +
                "-♥♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=090N-=-=\n" + // 12+11
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        assertEquals(11, hero.getForcesPerTick());
    }

    @Test
    public void shouldResetGoldCountAlso() {
        // given
        shouldOneMoreArmyToTheMaxCountWhenGetGold();

        // when
        hero.reset(); // reset all gold scores also
        game.tick();

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=0A-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        assertEquals(10, hero.getForcesPerTick());

        hero.move(new ForcesMoves(pt(1, 2), 1, QDirection.UP));
        game.tick();

        assertL("╔═══┐" +
                "║...│" +
                "║1$E│" +
                "└───┘" +
                "     ");

        assertE("-----" +
                "-♥---" +
                "-♥---" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=01-=-=-=\n" +
                "-=09-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        assertEquals(10, hero.getForcesPerTick());

        // when
        hero.increase(new Forces(pt(1, 3), 20)); // only 10 can increase
        game.tick();

        // then
        assertL("╔═══┐" +
                "║...│" +
                "║1$E│" +
                "└───┘" +
                "     ");

        assertE("-----" +
                "-♥---" +
                "-♥---" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=0B-=-=-=\n" + // 1+10
                "-=09-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        assertEquals(10, hero.getForcesPerTick());

        // when
        hero.increase(new Forces(pt(1, 3), 20)); // only 10 can increase
        game.tick();

        // then
        assertL("╔═══┐" +
                "║...│" +
                "║1$E│" +
                "└───┘" +
                "     ");

        assertE("-----" +
                "-♥---" +
                "-♥---" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=0L-=-=-=\n" + // 11+10
                "-=09-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        assertEquals(10, hero.getForcesPerTick());
    }

    @Test
    public void shouldDoubleScoreArmyIncreaseWhenGetTwoGold() {
        // given
        givenFl("      " +
                "╔════┐" +
                "║1$$E│" +
                "└────┘" +
                "      " +
                "      ");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=0A-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");

        assertEquals(10, hero.getForcesPerTick());

        hero.move(new ForcesMoves(pt(1, 3), 2, QDirection.RIGHT));
        game.tick();

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=0802-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");

        assertEquals(11, hero.getForcesPerTick());

        hero.move(new ForcesMoves(pt(2, 3), 1, QDirection.RIGHT));
        game.tick();

        assertL("      " +
                "╔════┐" +
                "║1..E│" +
                "└────┘" +
                "      " +
                "      ");

        assertE("------" +
                "------" +
                "-♥♥♥--" +
                "------" +
                "------" +
                "------");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=080101-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");

        assertEquals(12, hero.getForcesPerTick());

        // when
        hero.increase(
                // 7 + 7 = 14, but only 12 possible
                new Forces(pt(2, 3), 7),  // 7 here
                new Forces(pt(3, 3), 7)   // 12 - 7 = 5 here
        );
        game.tick();

        // then
        assertL("      " +
                "╔════┐" +
                "║1..E│" +
                "└────┘" +
                "      " +
                "      ");

        assertE("------" +
                "------" +
                "-♥♥♥--" +
                "------" +
                "------" +
                "------");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=080806-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n"); // 1+5

        assertEquals(12, hero.getForcesPerTick());

        // when
        hero.increase(new Forces(pt(3, 3), 20)); // only 12 possible to increase
        game.tick();

        // then
        assertL("      " +
                "╔════┐" +
                "║1..E│" +
                "└────┘" +
                "      " +
                "      ");

        assertE("------" +
                "------" +
                "-♥♥♥--" +
                "------" +
                "------" +
                "------");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=08080I-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n"); //6+12

        assertEquals(12, hero.getForcesPerTick());
    }

    @Test
    public void shouldNoScoreAfterGetGold() {
        // given
        givenFl("     " +
                "╔═══┐" +
                "║1$E│" +
                "└───┘" +
                "     ");

        // when
        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=0A-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        hero.move(new ForcesMoves(pt(1, 2), 2, QDirection.RIGHT));
        game.tick();

        // then
        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=0802-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        verifyNoMoreInteractions(listener);
    }

    @Test
    public void shouldHideGoldWhenGet() {
        // given
        givenFl("      " +
                "╔════┐" +
                "║1$$E│" +
                "└────┘" +
                "      " +
                "      ");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=0A-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(1, 3), 2, QDirection.RIGHT));
        game.tick();

        // then
        assertL("      " +
                "╔════┐" +
                "║1.$E│" +
                "└────┘" +
                "      " +
                "      ");

        assertE("------" +
                "------" +
                "-♥♥---" +
                "------" +
                "------" +
                "------");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=0802-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");

        // when
        hero.move(new ForcesMoves(pt(2, 3), 1, QDirection.RIGHT));
        game.tick();

        // then
        assertL("      " +
                "╔════┐" +
                "║1..E│" +
                "└────┘" +
                "      " +
                "      ");

        assertE("------" +
                "------" +
                "-♥♥♥--" +
                "------" +
                "------" +
                "------");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=080101-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");
    }

    @Test
    public void shouldHiddenGoldCantGetAgain() {
        // given
        shouldHideGoldWhenGet();

        assertL("      " +
                "╔════┐" +
                "║1..E│" +
                "└────┘" +
                "      " +
                "      ");

        assertE("------" +
                "------" +
                "-♥♥♥--" +
                "------" +
                "------" +
                "------");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=080101-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");

        hero.remove(new Forces(pt(2, 3), 1));
        game.tick();

        assertE("------" +
                "------" +
                "-♥-♥--" +
                "------" +
                "------" +
                "------");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=08-=01-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");

        // when
        // cant get hidden gold
        hero.move(new ForcesMoves(pt(1, 3), 1, QDirection.RIGHT));
        game.tick();

        // then
        assertEquals(12, hero.getForcesPerTick());
    }

    @Test
    public void shouldReNewGoldWhenReset() {
        // given
        shouldHideGoldWhenGet();

        assertL("      " +
                "╔════┐" +
                "║1..E│" +
                "└────┘" +
                "      " +
                "      ");

        assertE("------" +
                "------" +
                "-♥♥♥--" +
                "------" +
                "------" +
                "------");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=080101-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");

        // when
        hero.reset();
        game.tick();

        // then
        assertL("      " +
                "╔════┐" +
                "║1$$E│" +
                "└────┘" +
                "      " +
                "      ");

        assertE("------" +
                "------" +
                "-♥----" +
                "------" +
                "------" +
                "------");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=0A-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");
    }

    private void ticks(int count) {
        for (int i = 0; i < count; i++) {
            game.tick();
        }
    }

    @Test
    public void shouldScrollingView() {
        //given
        givenFl("╔══════════════════┐" +
                "║1.................│" +
                "║..................│" +
                "║....┌──╗..........│" +
                "║....│  ║..........│" +
                "║..┌─┘  └─╗........│" +
                "║..│      ║........│" +
                "║..│      ║........│" +
                "║..╚═┐  ╔═╝........│" +
                "║....│  ║..........│" +
                "║....╚══╝..........│" +
                "║..................│" +
                "║..................│" +
                "║..................│" +
                "║..................│" +
                "║..................│" +
                "║..................│" +
                "║..................│" +
                "║.................E│" +
                "└──────────────────┘");

        //then
        assertL("╔═══════════════" +
                "║1.............." +
                "║..............." +
                "║....┌──╗......." +
                "║....│  ║......." +
                "║..┌─┘  └─╗....." +
                "║..│      ║....." +
                "║..│      ║....." +
                "║..╚═┐  ╔═╝....." +
                "║....│  ║......." +
                "║....╚══╝......." +
                "║..............." +
                "║..............." +
                "║..............." +
                "║..............." +
                "║...............");

        assertF("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=0A-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");

        // when
        for (int i = 1; i <= 10; ++i) {
            Point from = pt(i, 18);
            hero.increaseAndMove(
                    new Forces(from, 1),
                    new ForcesMoves(from, 1, QDirection.RIGHT)
            );
            game.tick();
        }

        // then
        assertL("╔═══════════════" +
                "║1.............." +
                "║..............." +
                "║....┌──╗......." +
                "║....│  ║......." +
                "║..┌─┘  └─╗....." +
                "║..│      ║....." +
                "║..│      ║....." +
                "║..╚═┐  ╔═╝....." +
                "║....│  ║......." +
                "║....╚══╝......." +
                "║..............." +
                "║..............." +
                "║..............." +
                "║..............." +
                "║...............");

        assertE("----------------" +
                "-♥♥♥♥♥♥♥♥♥♥♥----" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------");

        assertF("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=0A01010101010101010101-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");

        // when
        Point from = pt(11, 18);
        hero.increaseAndMove(
                new Forces(from, 1),
                new ForcesMoves(from, 1, QDirection.DOWN)
        );
        game.tick();

        from = pt(11, 17);
        hero.increaseAndMove(
                new Forces(from, 1),
                new ForcesMoves(from, 1, QDirection.RIGHT)
        );
        game.tick();

        // then
        assertL("════════════════" +
                "1..............." +
                "................" +
                "....┌──╗........" +
                "....│  ║........" +
                "..┌─┘  └─╗......" +
                "..│      ║......" +
                "..│      ║......" +
                "..╚═┐  ╔═╝......" +
                "....│  ║........" +
                "....╚══╝........" +
                "................" +
                "................" +
                "................" +
                "................" +
                "................");

        assertE("----------------" +
                "♥♥♥♥♥♥♥♥♥♥♥-----" +
                "----------♥♥----" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------");

        assertF("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "0A01010101010101010101-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=0101-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");


        // when
        for (int i = 12; i <= 12+11; ++i) {
            from = pt(i, 17);
            hero.increaseAndMove(
                    new Forces(from, 1),
                    new ForcesMoves(from, 1, QDirection.RIGHT)
            );
            game.tick();
        }

        // then
        assertL("═══════════════┐" +
                "...............│" +
                "...............│" +
                ".┌──╗..........│" +
                ".│  ║..........│" +
                "─┘  └─╗........│" +
                "      ║........│" +
                "      ║........│" +
                "═┐  ╔═╝........│" +
                ".│  ║..........│" +
                ".╚══╝..........│" +
                "...............│" +
                "...............│" +
                "...............│" +
                "...............│" +
                "...............│");

        assertE("----------------" +
                "♥♥♥♥♥♥♥♥--------" +
                "-------♥♥♥♥♥♥♥♥-" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------");

        assertF("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "0101010101010101-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=0101010101010102-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");

        // when
        from = pt(7, 18);
        hero.increaseAndMove(
                new Forces(from, 1),
                new ForcesMoves(from, 1, QDirection.DOWN)
        );
        game.tick();

        // then
        assertL("════════════════" +
                "................" +
                "................" +
                "..┌──╗.........." +
                "..│  ║.........." +
                "┌─┘  └─╗........" +
                "│      ║........" +
                "│      ║........" +
                "╚═┐  ╔═╝........" +
                "..│  ║.........." +
                "..╚══╝.........." +
                "................" +
                "................" +
                "................" +
                "................" +
                "................");

        assertE("----------------" +
                "♥♥♥♥♥♥♥♥♥-------" +
                "----♥---♥♥♥♥♥♥♥♥" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------" +
                "----------------");

        assertF("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "010101010101010101-=-=-=-=-=-=-=\n" +
                "-=-=-=-=01-=-=-=0101010101010102\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");

        // when
        for (int i = 18; i >= 2; i--) {
            from = pt(11, i);
            hero.increaseAndMove(
                    new Forces(from, 1),
                    new ForcesMoves(from, 1, QDirection.DOWN)
            );
            game.tick();
        }

        assertL("..│  ║.........." +
                "┌─┘  └─╗........" +
                "│      ║........" +
                "│      ║........" +
                "╚═┐  ╔═╝........" +
                "..│  ║.........." +
                "..╚══╝.........." +
                "................" +
                "................" +
                "................" +
                "................" +
                "................" +
                "................" +
                "................" +
                "...............E" +
                "────────────────");

        assertE("--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "--------♥-------" +
                "----------------");

        assertF("-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=01-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");

        // when
        for (int i = 11; i >= 1; i--) {
            from = pt(i, 1);
            hero.increaseAndMove(
                    new Forces(from, 1),
                    new ForcesMoves(from, 1, QDirection.LEFT)
            );
            game.tick();
        }

        // then
        assertL("║....│  ║......." +
                "║..┌─┘  └─╗....." +
                "║..│      ║....." +
                "║..│      ║....." +
                "║..╚═┐  ╔═╝....." +
                "║....│  ║......." +
                "║....╚══╝......." +
                "║..............." +
                "║..............." +
                "║..............." +
                "║..............." +
                "║..............." +
                "║..............." +
                "║..............." +
                "║..............." +
                "└───────────────");

        assertE("-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-----------♥----" +
                "-♥♥♥♥♥♥♥♥♥♥♥----" +
                "----------------");

        assertF("-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=01-=-=-=-=\n" +
                "-=0201010101010101010101-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");
    }

    @Test
    public void shouldStartOnCenter() {
        //given
        givenFl("╔════════════════════════════════════┐" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║.................1..................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║....................................│" +
                "║...................................E│" +
                "└────────────────────────────────────┘");

        //then
        assertL("................" +
                "................" +
                "................" +
                "................" +
                "................" +
                "................" +
                "................" +
                "........1......." +
                "................" +
                "................" +
                "................" +
                "................" +
                "................" +
                "................" +
                "................" +
                "................");

        assertF("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=0A-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");
    }

    @Test
    public void shouldStartWhenSeveralStarts_case1() {
        // given
        givenFl("╔═════┐" +
                "║1...2│" +
                "║.....│" +
                "║.....│" +
                "║.....│" +
                "║4...3│" +
                "└─────┘");

        // when
        game.tick();

        // then
        assertL("╔═════┐" +
                "║1...2│" +
                "║.....│" +
                "║.....│" +
                "║.....│" +
                "║4...3│" +
                "└─────┘");

        assertE("-------" +
                "-♥-----" +
                "-------" +
                "-------" +
                "-------" +
                "-------" +
                "-------");

        assertF("-=-=-=-=-=-=-=\n" +
                "-=0A-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n");
    }

    @Test
    public void shouldStartWhenSeveralStarts_case2() {
        // given
        givenFl("╔═════┐" +
                "║4...1│" +
                "║.....│" +
                "║.....│" +
                "║.....│" +
                "║3...2│" +
                "└─────┘");

        // when
        game.tick();

        // then
        assertL("╔═════┐" +
                "║4...1│" +
                "║.....│" +
                "║.....│" +
                "║.....│" +
                "║3...2│" +
                "└─────┘");

        assertE("-------" +
                "-----♥-" +
                "-------" +
                "-------" +
                "-------" +
                "-------" +
                "-------");

        assertF("-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=0A-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n");
    }

    @Test
    public void shouldStartWhenSeveralStarts_case3() {
        // given
        givenFl("╔═════┐" +
                "║1...2│" +
                "║.....│" +
                "║.....│" +
                "║.....│" +
                "║4...3│" +
                "└─────┘");

        // when
        game.tick();

        // then
        assertL("╔═════┐" +
                "║1...2│" +
                "║.....│" +
                "║.....│" +
                "║.....│" +
                "║4...3│" +
                "└─────┘");

        assertE("-------" +
                "-♥-----" +
                "-------" +
                "-------" +
                "-------" +
                "-------" +
                "-------");

        assertF("-=-=-=-=-=-=-=\n" +
                "-=0A-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n");
    }

    @Test
    public void shouldStartWhenSeveralStarts_case4() {
        // given
        givenFl("╔═════┐" +
                "║2...3│" +
                "║.....│" +
                "║.....│" +
                "║.....│" +
                "║1...4│" +
                "└─────┘");

        // when
        game.tick();

        // then
        assertL("╔═════┐" +
                "║2...3│" +
                "║.....│" +
                "║.....│" +
                "║.....│" +
                "║1...4│" +
                "└─────┘");

        assertE("-------" +
                "-------" +
                "-------" +
                "-------" +
                "-------" +
                "-♥-----" +
                "-------");

        assertF("-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n" +
                "-=0A-=-=-=-=-=\n" +
                "-=-=-=-=-=-=-=\n");
    }

    @Test
    public void shouldSkipEmptyMessages() {
        // given
        givenFl("      " +
                "╔════┐" +
                "║1..E│" +
                "└────┘" +
                "      " +
                "      ");

        assertL("      " +
                "╔════┐" +
                "║1..E│" +
                "└────┘" +
                "      " +
                "      ");

        assertE("------" +
                "------" +
                "-♥----" +
                "------" +
                "------" +
                "------");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=0A-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");

        // when
        hero.message("");
        game.tick();

        // then
        verifyNoMoreInteractions(listener);

        assertL("      " +
                "╔════┐" +
                "║1..E│" +
                "└────┘" +
                "      " +
                "      ");

        assertE("------" +
                "------" +
                "-♥----" +
                "------" +
                "------" +
                "------");

        assertF("-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=0A-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n" +
                "-=-=-=-=-=-=\n");
    }

    // если я перемещаю что-то на яму, то войска пропадают
    @Test
    public void shouldMoveOnHole_thenKillForces() {
        // given
        givenFl("╔═══┐" +
                "║OOO│" +
                "║O1O│" +
                "║OOO│" +
                "└───┘");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=0A-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");

        // when
        hero.move(
                new ForcesMoves(pt(2, 2), 1, QDirection.LEFT_DOWN),
                new ForcesMoves(pt(2, 2), 1, QDirection.LEFT_UP),
                new ForcesMoves(pt(2, 2), 1, QDirection.RIGHT_DOWN),
                new ForcesMoves(pt(2, 2), 1, QDirection.RIGHT_UP),
                new ForcesMoves(pt(2, 2), 1, QDirection.RIGHT),
                new ForcesMoves(pt(2, 2), 1, QDirection.LEFT),
                new ForcesMoves(pt(2, 2), 1, QDirection.UP),
                new ForcesMoves(pt(2, 2), 1, QDirection.DOWN)
        );
        game.tick();

        assertE("-----" +
                "-----" +
                "--♥--" +
                "-----" +
                "-----");

        assertF("-=-=-=-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=02-=-=\n" +
                "-=-=-=-=-=\n" +
                "-=-=-=-=-=\n");
    }

}
