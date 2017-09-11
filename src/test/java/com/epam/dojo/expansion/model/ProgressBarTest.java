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
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.utils.JsonUtils;
import com.epam.dojo.expansion.model.levels.Cell;
import com.epam.dojo.expansion.model.levels.Level;
import com.epam.dojo.expansion.model.levels.items.Hero;
import com.epam.dojo.expansion.model.levels.items.HeroForces;
import com.epam.dojo.expansion.model.levels.items.Start;
import com.epam.dojo.expansion.model.levels.CellImpl;
import com.epam.dojo.expansion.model.levels.StubGamesGameFactory;
import com.epam.dojo.expansion.services.SettingsWrapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by oleksandr.baglai on 21.07.2016.
 */
public class ProgressBarTest {

    private Expansion single;
    private Expansion multiple;
    private ProgressBar progressBar;
    private Player player;
    private Dice dice;

    static class DummyExpansion extends Expansion {
        public DummyExpansion(List<Level> levels, Dice dice, boolean multiple) {
            super(levels, dice, multiple);
        }

        @Override
        public HeroForces startMoveForces(Hero hero, int x, int y, int count) {
            // do nothing
            return HeroForces.EMPTY;
        }

        @Override
        public void removeFromCell(Hero hero) {
            // do nothing
        }
    }

    @Before
    public void setup() {
        // given
        dice = mock(Dice.class);
        when(dice.next(anyInt())).thenReturn(0);

        SettingsWrapper.setup()
                .leaveForceCount(1)
                .regionsScores(0)
                .roundTicks(10000);

        Level level1 = getLevel();
        Level level2 = getLevel();
        Level level3 = getLevel();
        Level level4 = getLevel();
        Level level5 = getLevel();

        single = new DummyExpansion(Arrays.asList(level1, level2, level3, level4), dice, false);
        multiple = new DummyExpansion(Arrays.asList(level5), dice, true);
        PlayerLobby lobby = new PlayerLobby();
        progressBar = new ProgressBar(new StubGamesGameFactory(single, multiple), lobby);

        player = new Player(mock(EventListener.class), progressBar);
        progressBar.start(null);
    }

    private Level getLevel() {
        Level result = mock(Level.class);

        Start start = new Start(Elements.BASE1);
        CellImpl cell = new CellImpl(0, 0);
        start.setCell(cell);
        when(result.getItems(Start.class)).thenReturn(Arrays.asList(start));
        when(result.getCellsWith(any(Predicate.class))).thenReturn(Arrays.asList(start));
        when(result.getCells()).thenReturn(new Cell[]{cell});

        return result;
    }

    private void assertState(String expected) {
        assertEquals(JsonUtils.prettyPrint(new JSONObject(expected)), JsonUtils.prettyPrint(progressBar.printProgress()));
    }

    @Test
    public void stateAtStart() {
        // when then
        assertState("{'total':4,'current':0,'lastPassed':-1,'scores':true,'multiple':false}");
    }

    @Test
    public void stateWhenFinishLevel1() {
        // when
        player.getHero().setWin();
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':-1,'scores':true,'multiple':false}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':1,'lastPassed':0,'scores':true,'multiple':false}");
    }

    @Test
    public void stateWhenFinishLevel2() {
        // given
        stateWhenFinishLevel1();

        // when
        player.getHero().setWin();
        progressBar.tick();

        // then
        assertState("{'total':4,'current':1,'lastPassed':0,'scores':true,'multiple':false}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");
    }

    @Test
    public void stateWhenFinishLevel3() {
        // given
        stateWhenFinishLevel2();

        // when
        player.getHero().setWin();
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':3,'lastPassed':2,'scores':true,'multiple':false}");
    }

    @Test
    public void stateWhenFinishLevel4() {
        // given
        stateWhenFinishLevel3();

        // when
        player.getHero().setWin();
        progressBar.tick();

        // then
        assertState("{'total':4,'current':3,'lastPassed':2,'scores':true,'multiple':false}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");
    }

    @Test
    public void stateWhenFinishMultipleLevel5() {
        // given
        stateWhenFinishLevel4();

        // when
        player.getHero().setWin();
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");
    }

    @Test
    public void stateWhenFinishMultipleLevel5Again() {
        // given
        stateWhenFinishMultipleLevel5();

        // when
        player.getHero().setWin();
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");
    }

    @Test
    public void stateWhenGoFromMultipleToSingle() {
        // given
        stateWhenFinishMultipleLevel5Again();

        // when
        player.getHero().loadLevel(2);
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':3,'scores':false,'multiple':false}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':3,'scores':false,'multiple':false}");
    }

    @Test
    public void stateWhenNextLevelAfterGoFromMultipleToSingle() {
        // given
        stateWhenGoFromMultipleToSingle();

        // when
        player.getHero().setWin();
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':3,'scores':false,'multiple':false}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':3,'lastPassed':3,'scores':false,'multiple':false}");
    }

    @Test
    public void stateWhenGoFromSingleToMultiple() {
        // given
        stateWhenGoFromMultipleToSingle();

        // when
        player.getHero().loadLevel(4);
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");
    }

    @Test
    public void shouldNoSkipSingleLevel() {
        // given
        stateWhenFinishLevel2();

        // when
        player.getHero().loadLevel(3);
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");
    }

    @Test
    public void shouldNoSelectBadLevelFromSingle() {
        // given
        stateWhenFinishLevel2();

        // when
        player.getHero().loadLevel(30);
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");
    }

    @Test
    public void shouldNoSelectBadLevelFromMultiple() {
        // given
        stateWhenFinishLevel4();

        // when
        player.getHero().loadLevel(30);
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");
    }

    @Test
    public void canGoToSameSingleLevel() {
        // given
        stateWhenFinishLevel2();

        // when
        player.getHero().loadLevel(2);
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");
    }

    @Test
    public void shouldNoSkipMultipleLevel() {
        // given
        stateWhenFinishLevel2();

        // when
        player.getHero().loadLevel(4);
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");
    }

    @Test
    public void shouldResetCurrentSingleLevel() {
        // given
        stateWhenFinishLevel2();

        // when
        player.getHero().loadLevel(-1);
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");
    }

    @Test
    public void shouldResetCurrentMultipleLevel() {
        // given
        stateWhenFinishLevel4();

        // when
        player.getHero().loadLevel(-1);
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");
    }

    @Test
    public void remakeHeroWhenDieOnSingle() {
        // given
        stateWhenFinishLevel1();

        // when
        player.getHero().die();

        // then
        assertFalse(player.getHero().isAlive());

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':1,'lastPassed':0,'scores':true,'multiple':false}");
        assertTrue(player.getHero().isAlive());

        // when
        progressBar.tick();

        // then
        assertState("{'total':4,'current':1,'lastPassed':0,'scores':true,'multiple':false}");
        assertTrue(player.getHero().isAlive());
    }

    @Test
    public void shouldSelectSingleIfPassed() {
        // given
        stateWhenFinishLevel2();
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");

        // when
        progressBar.start("{'total':4,'current':1,'lastPassed':1,'scores':true,'multiple':false}");

        // then
        assertState("{'total':4,'current':1,'lastPassed':1,'scores':false,'multiple':false}");
    }

    @Test
    public void shouldSelectSingleIfNotPassed() {
        // given
        stateWhenFinishLevel2();
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");

        // when
        progressBar.start("{'total':4,'current':3,'lastPassed':1,'scores':true,'multiple':false}");

        // then
        assertState("{'total':4,'current':3,'lastPassed':1,'scores':true,'multiple':false}");
    }

    @Test
    public void shouldSelectMultipleIfNotPassed() {
        // given
        stateWhenFinishLevel2();
        assertState("{'total':4,'current':2,'lastPassed':1,'scores':true,'multiple':false}");

        // when
        progressBar.start("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");

        // then
        assertState("{'total':4,'current':0,'lastPassed':3,'scores':true,'multiple':true}");
    }
}
