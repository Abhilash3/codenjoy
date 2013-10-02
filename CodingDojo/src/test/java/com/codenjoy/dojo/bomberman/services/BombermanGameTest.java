package com.codenjoy.dojo.bomberman.services;

import com.codenjoy.dojo.bomberman.model.Board;
import com.codenjoy.dojo.services.*;
import org.fest.reflect.core.Reflection;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

/**
 * User: oleksandr.baglai
 * Date: 3/11/13
 * Time: 5:23 PM
 */
public class BombermanGameTest {

    @Test
    public void shouldWork() {
        int size = 11;

        EventListener listener = mock(EventListener.class);
        GameType bombermanGame = new BombermanGame();
        Game game = bombermanGame.newGame(listener);
        bombermanGame.getSettings().getParameter("Board size").type(Integer.class).update(size);
        game.tick();

        PlayerScores scores = bombermanGame.getPlayerScores(10);
        assertEquals(10, scores.getScore());
        scores.event(BombermanEvents.KILL_MEAT_CHOPPER.name());
        assertEquals(110, scores.getScore());

        assertEquals(size, bombermanGame.getBoardSize().getValue().intValue());

        Joystick joystick = game.getJoystick();

        int countWall = (size - 1) * 4 + (size / 2 - 1) * (size / 2 - 1);
        int countDestroyWalls = size * size / 10;
        int meatChoppersCount = DefaultGameSettings.MEAT_CHOPPERS_COUNT;

        String actual = game.getBoardAsString();
        assertCharCount(actual, "☼", countWall);
        assertCharCount(actual, " ", size * size - countWall - countDestroyWalls - meatChoppersCount - 1);
        assertCharCount(actual, "#", countDestroyWalls);
        assertCharCount(actual, "&", meatChoppersCount);
        assertCharCount(actual, "☺", 1);

        assertEquals(0, game.getMaxScore());
        assertEquals(0, game.getCurrentScore());
        assertFalse(game.isGameOver());

        joystick.act();
        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();

        assertTrue(game.isGameOver());
    }

    @Test
    public void shouldOneBoardForAllGames() {
        EventListener listener = mock(EventListener.class);
        GameType bombermanGame = new BombermanGame();
        Game game1 = bombermanGame.newGame(listener);
        Game game2 = bombermanGame.newGame(listener);
        assertSame(getBoard(game1), getBoard(game2));
    }

    private Board getBoard(Game game) {
        return Reflection.field("board").ofType(Board.class).in(game).get();
    }

    private void assertCharCount(String actual, String ch, int count) {
        assertEquals(count, actual.length() - actual.replace(ch, "").length());
    }

}
