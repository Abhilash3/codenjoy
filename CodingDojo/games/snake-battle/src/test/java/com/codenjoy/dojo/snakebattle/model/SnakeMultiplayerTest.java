package com.codenjoy.dojo.snakebattle.model;

/*-
 * #©L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * ©©
 * Copyright (C) 2018 Codenjoy
 * ©©
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
 * #L©
 */


import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.snakebattle.model.board.SnakeBoard;
import com.codenjoy.dojo.snakebattle.model.hero.Hero;
import com.codenjoy.dojo.snakebattle.model.level.LevelImpl;
import com.codenjoy.dojo.snakebattle.services.Events;
import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * ®author Kors
 */
public class SnakeMultiplayerTest {
    private SnakeBoard game;
    private Dice dice;

    private Hero hero;
    private EventListener heroEvents;
    private Player heroPlayer;

    private Hero enemy;
    private EventListener enemyEvents;
    private Player enemyPlayer;

    private PrinterFactory printer = new PrinterFactoryImpl();

    @Before
    public void setup() {
        dice = mock(Dice.class);
    }

    private void givenFl(String board) {
        LevelImpl level = new LevelImpl(board);

        game = new SnakeBoard(level, dice);

        Hero hero = level.getHero();
        hero.setActive(true);
        heroEvents = mock(EventListener.class);
        heroPlayer = new Player(heroEvents);
        game.newGame(heroPlayer);
        heroPlayer.setHero(hero);
        hero.init(game);
        this.hero = game.getHeroes().get(0);

        Hero enemy = level.getEnemy();
        enemy.setActive(true);
        enemyEvents = mock(EventListener.class);
        enemyPlayer = new Player(enemyEvents);
        game.newGame(enemyPlayer);
        enemyPlayer.setHero(enemy);
        enemy.init(game);
        this.enemy = game.getHeroes().get(1);
    }

    // проверяем что соперник отображается на карте
    @Test
    public void enemyOnField() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ╘►  ☼" +
                "☼     ☼" +
                "☼ ×>  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ╘►  ☼" +
                "☼     ☼" +
                "☼ ×>  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");
    }

    // спящие змеи
    @Test
    public void testSleepingSnake() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ╘►  ☼" +
                "☼     ☼" +
                "☼ ×>  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        hero.setActive(false);
        enemy.setActive(false);

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ~&  ☼" +
                "☼     ☼" +
                "☼ *ø  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ~&  ☼" +
                "☼     ☼" +
                "☼ *ø  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");
    }

    // проверяем что обе змейки умирают, когда врезаются в равного соперника
    // и получаем оповещение о смерти
    @Test
    public void diedBothHeroes() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ╘►  ☼" +
                "☼     ☼" +
                "☼ ×>  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        hero.down();
        enemy.up();
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ╓  ☼" +
                "☼  ☻  ☼" +
                "☼  ¤  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        verify(heroEvents).event(Events.DIE);

        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");
    }

    // проверяем что меньшая змейка умирает, когда врезаются голова к голове
    // большая змейка уменьшается на размер маленькой
    // змейка перестаёт уменьшаться на следующий ход
    @Test
    public void diedSmallerHero() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼   ╘►☼" +
                "☼     ☼" +
                "☼ ×──>☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        hero.down();
        enemy.up();
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼    ╓☼" +
                "☼    ☻☼" +
                "☼  ×─┘☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        verify(heroEvents).event(Events.DIE);
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼    ˄☼" +
                "☼    ¤☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼    ˄☼" +
                "☼    ¤☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");
    }

    // проверяем что змейка умирает, когда врезается в тело другой змейки
    @Test
    public void diedByBody() {
        // когда в игрока врезается противник
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼╘►○  ☼" +
                "☼×>   ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();
        verify(heroEvents).event(Events.APPLE);
        enemy.up();
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ╘═► ☼" +
                "☼  ¤  ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        verify(heroEvents).event(Events.ALIVE);
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ╘═►☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        // такой же тест, но врезается игрок в противника
        // (последовательность героев в списке может оказывать значение на результат)
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼╘►   ☼" +
                "☼×>○  ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();
        hero.down();
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ╓  ☼" +
                "☼ ×☻> ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        verify(heroEvents).event(Events.DIE);
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼  ×─>☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");
    }

    // проверяем что змейка умирает, когда врезается в хвост другой змейки
    @Test
    public void diedByTail() {
        // когда в игрока врезается противник
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼╘►   ☼" +
                "☼×>○  ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();
        enemy.up();
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ╘► ☼" +
                "☼ ×┘  ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼   ╘►☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        // такой же тест, но врезается игрок в противника
        // (последовательность героев в списке может оказывать значение на результат)
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼╘►   ☼" +
                "☼×>   ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();
        hero.down();
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ╓  ☼" +
                "☼  ☻> ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        verify(heroEvents).event(Events.DIE);
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼   ×>☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");
    }

    // в полёте змейки не вредят друг-другу (летишь сам)
    @Test
    public void flyOverEnemy() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼╘►©  ☼" +
                "☼     ☼" +
                "☼×>   ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();
        hero.down();
        enemy.up();
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ╓  ☼" +
                "☼  ♠  ☼" +
                "☼  ¤  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ˄  ☼" +
                "☼  ╓  ☼" +
                "☼  ♠  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼  ˄  ☼" +
                "☼  ¤  ☼" +
                "☼     ☼" +
                "☼  ╓  ☼" +
                "☼  ♠  ☼" +
                "☼☼☼☼☼☼☼");
    }

    // в полёте змейки не вредят друг-другу (летит враг)
    @Test
    public void flyOverHero() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼╘►   ☼" +
                "☼     ☼" +
                "☼×>©  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();
        hero.down();
        enemy.up();
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ╓  ☼" +
                "☼  ▼  ☼" +
                "☼  ¤  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ♦  ☼" +
                "☼  ╓  ☼" +
                "☼  ▼  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼  ♦  ☼" +
                "☼  ¤  ☼" +
                "☼     ☼" +
                "☼  ╓  ☼" +
                "☼  ▼  ☼" +
                "☼☼☼☼☼☼☼");
    }

    // в случае ярости, змея может съесть другую
    @Test
    public void eatEnemy() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ╘►® ☼" +
                "☼     ☼" +
                "☼ ×>○○☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();
        game.tick();
        hero.down();
        enemy.up();
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼    ╓☼" +
                "☼    ♥☼" +
                "☼  ×─┘☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼    ╓☼" +
                "☼    ♥☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");
    }

    // в случае ярости, можно откусить кусок змейки соперника
    @Test
    public void eatEnemyTail() {
        // когда в игрока врезается противник
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼╘►○○ ☼" +
                "☼     ☼" +
                "☼×>®  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();
        verify(heroEvents).event(Events.APPLE);
        enemy.up();
        game.tick();
        verify(heroEvents, times(2)).event(Events.APPLE);
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ♣╘►☼" +
                "☼  ¤  ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        // такой же тест, но врезается игрок в противника
        // (последовательность героев в списке может оказывать значение на результат)
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼╘►®  ☼" +
                "☼     ☼" +
                "☼×>○  ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();
        hero.down();
        game.tick();
        game.tick();

        assertH("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼  ╓  ☼" +
                "☼  ♥×>☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");
    }

    private void assertH(String expected) {
        assertBoard(expected, heroPlayer);
    }

    private void assertBoard(String expected, Player player) {
        assertEquals(TestUtils.injectN(expected),
                printer.getPrinter(game.reader(), player).print());
    }

    private void assertE(String expected) {
        assertBoard(expected, enemyPlayer);
    }
}
