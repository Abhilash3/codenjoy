package com.codenjoy.dojo.services;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Created by Sanja on 15.02.14.
 */
public class StatisticsTest {

    private Statistics statistics;
    private Player player;
    private PlayerSpy spy;

    @Before
    public void setup() {
        statistics = new Statistics();

        player = mock(Player.class);
        spy = statistics.newPlayer(player);
    }

    @Test
    public void shouldActiveIfOneTickAfterRegister() {
        // given
        statistics.tick();

        // when
        List<Player> activePlayers = statistics.getActivePlayers();

        // then
        assertEquals(1, activePlayers.size());
        assertSame(activePlayers.get(0), player);
    }

    @Test
    public void shouldActiveIfTwoTickAfterRegister() {
        // given
        statistics.tick();
        statistics.tick();

        // when
        List<Player> activePlayers = statistics.getActivePlayers();

        // then
        assertEquals(1, activePlayers.size());
        assertSame(activePlayers.get(0), player);
    }

    @Test
    public void shouldNotActiveIfThreeTickAfterRegister() {
        // given
        statistics.tick();
        statistics.tick();
        statistics.tick();

        // when
        List<Player> activePlayers = statistics.getActivePlayers();

        // then
        assertEquals(0, activePlayers.size());
    }

    @Test
    public void shouldAgainActiveIfActCall() {
        // given
        statistics.tick();
        statistics.tick();
        statistics.tick();
        statistics.tick();
        statistics.tick();

        spy.act();

        // when
        List<Player> activePlayers = statistics.getActivePlayers();

        // then
        assertEquals(1, activePlayers.size());
        assertSame(activePlayers.get(0), player);
    }

    @Test
    public void shouldAnotherUserActiveWhenFirstIsNot() {
        // given
        Player player2 = mock(Player.class);
        PlayerSpy spy2 = statistics.newPlayer(player2);

        statistics.tick();
        statistics.tick();
        statistics.tick();
        statistics.tick();
        statistics.tick();

        spy2.act();

        // when
        List<Player> activePlayers = statistics.getActivePlayers();

        // then
        assertEquals(1, activePlayers.size());
        assertSame(activePlayers.get(0), player2);
    }
}
