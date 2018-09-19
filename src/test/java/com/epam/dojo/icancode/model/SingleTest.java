//package com.epam.dojo.icancode.model;
//
///*-
// * #%L
// * iCanCode - it's a dojo-like platform from developers to developers.
// * %%
// * Copyright (C) 2016 EPAM
// * %%
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as
// * published by the Free Software Foundation, either version 3 of the
// * License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public
// * License along with this program.  If not, see
// * <http://www.gnu.org/licenses/gpl-3.0.html>.
// * #L%
// */
//
//
//import com.codenjoy.dojo.services.Dice;
//import com.codenjoy.dojo.services.EventListener;
//import com.codenjoy.dojo.utils.JsonUtils;
//import com.codenjoy.dojo.utils.TestUtils;
//import com.epam.dojo.icancode.model.interfaces.ILevel;
//import com.epam.dojo.icancode.services.Events;
//import com.epam.dojo.icancode.services.Levels;
//import org.json.JSONObject;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.stubbing.OngoingStubbing;
//
//import java.util.*;
//
//import static junit.framework.Assert.assertEquals;
//import static org.mockito.Matchers.anyInt;
//import static org.mockito.Mockito.*;
//
//public class SingleTest {
//
//    private Dice dice;
//    private EventListener listener1;
//    private EventListener listener2;
//    private Single single1;
//    private Single single2;
//
//    @Before
//    public void setup() {
//        dice = mock(Dice.class);
//    }
//
//    private void dice(int... ints) {
//        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
//        for (int i : ints) {
//            when = when.thenReturn(i);
//        }
//    }
//
//    private void givenFl(String... boards) {
//        Levels.VIEW_SIZE = Levels.VIEW_SIZE_TESTING;
//        Deque<String> strings = new LinkedList<>(Arrays.asList(boards));
//        String multiple = strings.removeLast();
//        ILevel levelSingle1 = createLevels(strings).get(0);
//        ILevel levelSingle2 = createLevels(strings).get(1);
//        ILevel levelMultiple = createLevels(Arrays.asList(multiple));
//
//        ICanCode gameSingle1 = new ICanCode(levelsSingle1, dice, ICanCode.SINGLE);
//        ICanCode gameSingle2 = new ICanCode(levelsSingle2, dice, ICanCode.SINGLE);
//        ICanCode gameMultiple = new ICanCode(levelMultiple, dice, ICanCode.MULTIPLE);
//        listener1 = mock(EventListener.class);
//        listener2 = mock(EventListener.class);
//
//        single1 = new Single(gameSingle1, gameMultiple, listener1, null, null);
//        single1.newGame();
//
//        single2 = new Single(gameSingle2, gameMultiple, listener2, null, null);
//        single2.newGame();
//    }
//
//    private Hero hero1() {
//        return (Hero)single1.getJoystick();
//    }
//
//    private Hero hero2() {
//        return (Hero)single2.getJoystick();
//    }
//
//    private List<ILevel> screateLevels(Collection<String> boards) {
//        List<ILevel> levels = new LinkedList<ILevel>();
//        for (String board : boards) {
//            ILevel level = new LevelImpl(board);
//            levels.add(level);
//        }
//        return levels;
//    }
//
//    private void assertL(Single single, String expected) {
//        assertEquals(TestUtils.injectN(expected),
//                TestUtils.injectN(single.getPrinter().print().getLayers().get(0)));
//    }
//
//    private void assertE(Single single, String expected) {
//        assertEquals(TestUtils.injectN(expected),
//                TestUtils.injectN(single.getPrinter().print().getLayers().get(1)));
//    }
//
//    @Test
//    public void shouldNextLevelWhenFinishCurrent() {
//        // given
//        givenFl("╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘",
//                "╔══┐" +
//                "║S.│" +
//                "║E.│" +
//                "└──┘",
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        // when
//        hero1().right();
//        single1.tick();
//
//        verify(listener1).event(Events.WIN(0));
//        reset(listener1);
//
//        assertL(single1,
//                "╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "--☺-" +
//                "----" +
//                "----");
//
//        // when
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║E.│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        // when
//        hero1().down();
//        single1.tick();
//
//        // then
//        verify(listener1).event(Events.WIN(0));
//        reset(listener1);
//
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║E.│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "-☺--" +
//                "----");
//
//        // when
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        // when
//        hero1().down();
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "-☺--" +
//                "----");
//
//        // when
//        hero1().right();
//        single1.tick();
//
//        // then
//        verify(listener1).event(Events.WIN(0));
//        reset(listener1);
//
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "--☺-" +
//                "----");
//
//        // when
//        single1.tick();
//
//        // then
//        verifyNoMoreInteractions(listener1);
//
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//    }
//
//    @Test
//    public void shouldSeveralPlayersCollectionAtLastLevel() {
//        // given
//        givenFl("╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘",
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        // when
//        hero1().right();
//        single1.tick();
//        single2.tick();
//
//        verify(listener1).event(Events.WIN(0));
//        reset(listener1);
//        verifyNoMoreInteractions(listener2);
//
//        assertL(single1,
//                "╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "--☺-" +
//                "----" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        // when
//        hero2().right();
//        single1.tick(); // goes multiple
//        single2.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "--☺-" +
//                "----" +
//                "----");
//
//        // when
//        hero1().down();
//        single1.tick();
//        single2.tick(); // goes multiple
//
//        // then
//        verifyNoMoreInteractions(listener1);
//        verify(listener2).event(Events.WIN(0));
//        reset(listener2);
//
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-X--" +
//                "-☺--" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "-☺--" +
//                "-X--" +
//                "----");
//
//        // when
//        hero1().right(); // finished
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        // then
//        verify(listener1).event(Events.WIN(0));
//        reset(listener1);
//        verifyNoMoreInteractions(listener2);
//
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "--X-" +
//                "--☺-" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "--☺-" +
//                "--X-" +
//                "----");
//
//        // when
//        hero2().down();
//        single1.tick(); // started
//        single2.tick(); // finished
//
//        // then
//        verifyNoMoreInteractions(listener1);
//        verify(listener2).event(Events.WIN(0));
//        reset(listener2);
//
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-☺--" +
//                "--X-" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "-X--" +
//                "--☺-" +
//                "----");
//
//        // when
//        single1.tick();
//        single2.tick(); // started
//
//        // then
//        verifyNoMoreInteractions(listener1);
//        verifyNoMoreInteractions(listener2);
//
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        // when
//        hero1().down();
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        // then
//        verifyNoMoreInteractions(listener1);
//        verifyNoMoreInteractions(listener2);
//
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "--X-" +
//                "-☺--" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "--☺-" +
//                "-X--" +
//                "----");
//
//        // when
//        hero1().right();
//        hero2().down();
//        single1.tick(); // finished
//        single2.tick(); // finished
//
//        // then
//        verify(listener1).event(Events.WIN(0));
//        reset(listener1);
//
//        verify(listener2).event(Events.WIN(0));
//        reset(listener2);
//
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "--☺-" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "----" +
//                "--☺-" +
//                "----");
//
//        // when
//        single1.tick(); // started
//        single2.tick(); // started
//
//        // then
//        verifyNoMoreInteractions(listener1);
//        verifyNoMoreInteractions(listener2);
//
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//    }
//
//    @Test
//    public void shouldDrawLaserDeath() {
//        // given
//        givenFl("╔═══┐" +
//                "║SE.│" +
//                "║...│" +
//                "║...│" +
//                "└───┘",
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║˄˄E│" +
//                "└───┘");
//
//        // go to next level
//        hero1().right();
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        single1.tick();
//        single2.tick();
//
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        reset(listener1);
//        reset(listener2);
//
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║˄˄E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-☺X--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║˄˄E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-X☺--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        // when
//        single1.tick();
//        single2.tick();
//
//        single1.tick();
//        single2.tick();
//
//        single1.tick();
//        single2.tick();
//
//        single1.tick();
//        single2.tick();
//
//        single1.tick(); // laser machine is ready
//        single2.tick();
//
//        single1.tick(); // fire
//        single2.tick();
//
//        // then
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║˄˄E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-☺X--" +
//                "-↑↑--" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║˄˄E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-X☺--" +
//                "-↑↑--" +
//                "-----" +
//                "-----");
//
//        // when
//        single1.tick();
//        single2.tick();
//
//        // then
//        verify(listener1).event(Events.LOOSE());
//        verify(listener2).event(Events.LOOSE());
//
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║˄˄E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-☻&--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║˄˄E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-&☻--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        // when
//        single1.tick();
//        single2.tick();
//
//        // then
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║˄˄E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-☺---" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║˄˄E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-☺---" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        // when
//        hero1().right(); // other hero goes right
//        single2.tick();
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║˄˄E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-X☺--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║˄˄E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-☺X--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        verifyNoMoreInteractions(listener1);
//        verifyNoMoreInteractions(listener2);
//    }
//
//    @Test
//    public void shouldDrawOverBox() {
//        // given
//        givenFl("╔═══┐" +
//                "║SE.│" +
//                "║...│" +
//                "║...│" +
//                "└───┘",
//                "╔═══┐" +
//                "║S..│" +
//                "║BB.│" +
//                "║..E│" +
//                "└───┘");
//
//        // go to next level
//        hero1().right();
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        single1.tick();
//        single2.tick();
//
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        reset(listener1);
//        reset(listener2);
//
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-☺X--" +
//                "-BB--" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-X☺--" +
//                "-BB--" +
//                "-----" +
//                "-----");
//        // when
//        hero1().jump();
//        hero1().down();
//        hero2().jump();
//        hero2().down();
//        single1.tick();
//        single2.tick();
//
//        // then
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-----" +
//                "-№%--" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-----" +
//                "-%№--" +
//                "-----" +
//                "-----");
//
//        // when
//        single1.tick();
//        single2.tick();
//
//        // then
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-----" +
//                "-BB--" +
//                "-☺X--" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-----" +
//                "-BB--" +
//                "-X☺--" +
//                "-----");
//    }
//
//    @Test
//    public void shouldDrawJump() {
//        // given
//        givenFl("╔═══┐" +
//                "║SE.│" +
//                "║...│" +
//                "║...│" +
//                "└───┘",
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        // go to next level
//        hero1().right();
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        single1.tick();
//        single2.tick();
//
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        reset(listener1);
//        reset(listener2);
//
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-☺X--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-X☺--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        // when
//        hero1().act();
//        single1.tick();
//        single2.tick();
//
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-*X--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-^☺--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        // when
//        hero2().act();
//        single1.tick();
//        single2.tick();
//
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-☺^--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-X*--" +
//                "-----" +
//                "-----" +
//                "-----");
//    }
//
//    @Test
//    public void shouldFallingInHoleJump() {
//        // given
//        givenFl("╔═══┐" +
//                "║SE.│" +
//                "║...│" +
//                "║...│" +
//                "└───┘",
//                "╔═══┐" +
//                "║S..│" +
//                "║OO.│" +
//                "║..E│" +
//                "└───┘");
//
//        // go to next level
//        hero1().right();
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        single1.tick();
//        single2.tick();
//
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        reset(listener1);
//        reset(listener2);
//
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║OO.│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-☺X--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║OO.│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-X☺--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        // when
//        hero1().down();
//        single1.tick();
//        single2.tick();
//
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║OO.│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "--X--" +
//                "-o---" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║OO.│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "--☺--" +
//                "-x---" +
//                "-----" +
//                "-----");
//
//        // when
//        hero2().down();
//        single1.tick();
//        single2.tick();
//
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║OO.│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-☺---" +
//                "--x--" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║OO.│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-X---" +
//                "--o--" +
//                "-----" +
//                "-----");
//
//        // when
//        single1.tick();
//        single2.tick();
//
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║OO.│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-☺---" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║OO.│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-☺---" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        // when
//        hero1().right();
//        single1.tick();
//        single2.tick();
//
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║OO.│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "-X☺--" +
//                "-----" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║OO.│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "-☺X--" +
//                "-----" +
//                "-----" +
//                "-----");
//    }
//
//    @Test
//    public void shouldAllLevelsAreDone() {
//        // given
//        givenFl("╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘",
//                "╔══┐" +
//                "║.S│" +
//                "║.E│" +
//                "└──┘",
//                "╔══┐" +
//                "║..│" +
//                "║ES│" +
//                "└──┘",
//                "╔══┐" + // multiple
//                "║E.│" +
//                "║S.│" +
//                "└──┘"
//        );
//
//        assertL(single1,
//                "╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        // when done 1 level - go to 2 (single)
//        hero1().right();
//        single1.tick();
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║.S│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "--☺-" +
//                "----" +
//                "----");
//
//        // when done 2 level - go to 3 (single)
//        hero1().down();
//        single1.tick();
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║..│" +
//                "║ES│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "--☺-" +
//                "----");
//
//        // when done 3 level - go to 4 (multiple)
//        hero1().left();
//        single1.tick();
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║E.│" +
//                "║S.│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "-☺--" +
//                "----");
//
//        // when done 4 level - start 4 again (multiple)
//        hero1().up();
//        single1.tick();
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║E.│" +
//                "║S.│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "-☺--" +
//                "----");
//
//        // when done 4 level - start 4 again multiple)
//        hero1().up();
//        single1.tick();
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║E.│" +
//                "║S.│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "-☺--" +
//                "----");
//    }
//
//    @Test
//    public void shouldSelectLevelWhenAllLevelsAreDone() {
//        // given
//        shouldAllLevelsAreDone();
//
//        // when try to change level 1  - success from multiple to single
//        hero1().loadLevel(0);
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        // when try to change level 2  - success from single to single
//        hero1().loadLevel(1);
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║.S│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "--☺-" +
//                "----" +
//                "----");
//
//        // when try to change level 3  - success from single to single
//        hero1().loadLevel(2);
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║..│" +
//                "║ES│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "--☺-" +
//                "----");
//
//        // when try to change level 4 - success from single to multiple
//        hero1().loadLevel(3);
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║E.│" +
//                "║S.│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "-☺--" +
//                "----");
//
//        // when try to change level 500 - fail
//        hero1().right();
//        single1.tick();
//        hero1().loadLevel(500);
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║E.│" +
//                "║S.│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "--☺-" +
//                "----");
//
//        // when try to change level 2 - success from multiple to single
//        hero1().loadLevel(1);
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║.S│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "--☺-" +
//                "----" +
//                "----");
//
//    }
//
//    @Test
//    public void shouldWinOnPassedLevelThanCanSelectAnother_caseGoFromMultiple() {
//        // given
//        shouldAllLevelsAreDone();
//
//        // when win on level then try to change to last - success
//        hero1().loadLevel(3);
//        single1.tick();
//        single1.tick();
//        hero1().right();
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║E.│" +
//                "║S.│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "--☺-" +
//                "----");
//
//        // when try to change level 3 (previous) - success
//        hero1().loadLevel(2);
//        single1.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║..│" +
//                "║ES│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "--☺-" +
//                "----");
//    }
//
//    @Test
//    public void shouldResetOnMultipleWillResetOnlyMultipleLevel() {
//        // given
//        shouldAllLevelsAreDone();
//
//        // when
//        hero1().reset();
//        single1.tick();
//        single1.tick();
//        hero1().right();
//        single1.tick();
//
//        // then
//        assertL(single1, // still multiple
//                "╔══┐" +
//                "║E.│" +
//                "║S.│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "--☺-" +
//                "----");
//
//        // when
//        hero1().reset();
//        single1.tick();
//
//        // then
//        assertL(single1, // still multiple
//                "╔══┐" +
//                "║E.│" +
//                "║S.│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "----" +
//                "-☺--" +
//                "----");
//    }
//
//    @Test
//    public void testGetBoardAsString() {
//        // given
//        givenFl("╔═══┐" +
//                "║SE.│" +
//                "║...│" +
//                "║...│" +
//                "└───┘",
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        // when then
//        assertBoardData("{'current':0,'lastPassed':-1,'multiple':false,'scores':true,'total':1}",
//                "{'x':0,'y':0}",
//                true,
//                "['╔═══┐" +
//                "║SE.│" +
//                "║...│" +
//                "║...│" +
//                "└───┘'," +
//                "'-----" +
//                "-☺---" +
//                "-----" +
//                "-----" +
//                "-----']", single1);
//
//        // when then
//        assertBoardData("{'current':0,'lastPassed':-1,'multiple':false,'scores':true,'total':1}",
//                "{'x':0,'y':0}",
//                true,
//                "['╔═══┐" +
//                "║SE.│" +
//                "║...│" +
//                "║...│" +
//                "└───┘'," +
//                "'-----" +
//                "-☺---" +
//                "-----" +
//                "-----" +
//                "-----']",
//                single2);
//
//        // go to next level
//        hero1().right();
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        single1.tick();
//        single2.tick();
//
//        // then select different way
//        hero1().right();
//        hero2().down();
//        single1.tick();
//        single2.tick();
//
//        // then
//        assertL(single1,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single1,
//                "-----" +
//                "--☺--" +
//                "-X---" +
//                "-----" +
//                "-----");
//
//        assertL(single2,
//                "╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘");
//
//        assertE(single2,
//                "-----" +
//                "--X--" +
//                "-☺---" +
//                "-----" +
//                "-----");
//
//        // when then
//        assertBoardData("{'current':0,'lastPassed':0,'multiple':true,'scores':true,'total':1}",
//                "{'x':0,'y':0}",
//                false,
//                "['╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘'," +
//                "'-----" +
//                "--☺--" +
//                "-X---" +
//                "-----" +
//                "-----']",
//                single1);
//
//        // when then
//        assertBoardData("{'current':0,'lastPassed':0,'multiple':true,'scores':true,'total':1}",
//                "{'x':0,'y':0}",
//                false,
//                "['╔═══┐" +
//                "║S..│" +
//                "║...│" +
//                "║..E│" +
//                "└───┘'," +
//                "'-----" +
//                "--X--" +
//                "-☺---" +
//                "-----" +
//                "-----']",
//                single2);
//    }
//
//    @Test
//    public void testGetBoardAsString_whenBigFrame() {
//        // given
//        String field =
//                "╔══════════════════┐" +
//                "║S.................│" +
//                "║..............B...│" +
//                "║....┌──╗..........│" +
//                "║....│  ║..........│" +
//                "║..┌─┘  └─╗........│" +
//                "║..│      ║........│" +
//                "║..│      ║........│" +
//                "║..╚═┐  ╔═╝........│" +
//                "║....│  ║..........│" +
//                "║....╚══╝..........│" +
//                "║..................│" +
//                "║..................│" +
//                "║..................│" +
//                "║..................│" +
//                "║.B................│" +
//                "║..................│" +
//                "║..................│" +
//                "║.................E│" +
//                "└──────────────────┘";
//        givenFl(field, field);
//
//        // when then
//        assertBoardData("{'current':0,'lastPassed':-1,'multiple':false,'scores':true,'total':1}",
//                "{'x':0,'y':4}",
//                true,
//                "['╔═══════════════" +
//                "║S.............." +
//                "║..............." +
//                "║....┌──╗......." +
//                "║....│  ║......." +
//                "║..┌─┘  └─╗....." +
//                "║..│      ║....." +
//                "║..│      ║....." +
//                "║..╚═┐  ╔═╝....." +
//                "║....│  ║......." +
//                "║....╚══╝......." +
//                "║..............." +
//                "║..............." +
//                "║..............." +
//                "║..............." +
//                "║...............'," +
//                "'----------------" +
//                "-☺--------------" +
//                "---------------B" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "--B-------------']", single1);
//
//        assertBoardData("{'current':0,'lastPassed':-1,'multiple':false,'scores':true,'total':1}",
//                "{'x':0,'y':4}",
//                true,
//                "['╔═══════════════" +
//                "║S.............." +
//                "║..............." +
//                "║....┌──╗......." +
//                "║....│  ║......." +
//                "║..┌─┘  └─╗....." +
//                "║..│      ║....." +
//                "║..│      ║....." +
//                "║..╚═┐  ╔═╝....." +
//                "║....│  ║......." +
//                "║....╚══╝......." +
//                "║..............." +
//                "║..............." +
//                "║..............." +
//                "║..............." +
//                "║...............'," +
//                "'----------------" +
//                "-☺--------------" +
//                "---------------B" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "--B-------------']", single2);
//
//        // when
//        for (int i = 0; i < 17; i++) {
//            hero1().right();
//            hero2().down();
//            single1.tick();
//            single2.tick();
//        }
//
//        // then
//        assertBoardData("{'current':0,'lastPassed':-1,'multiple':false,'scores':true,'total':1}",
//                "{'x':4,'y':4}",
//                true,
//                "['═══════════════┐" +
//                "...............│" +
//                "...............│" +
//                ".┌──╗..........│" +
//                ".│  ║..........│" +
//                "─┘  └─╗........│" +
//                "      ║........│" +
//                "      ║........│" +
//                "═┐  ╔═╝........│" +
//                ".│  ║..........│" +
//                ".╚══╝..........│" +
//                "...............│" +
//                "...............│" +
//                "...............│" +
//                "...............│" +
//                "...............│'," +
//                "'----------------" +
//                "--------------☺-" +
//                "-----------B----" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------']", single1);
//
//        assertBoardData("{'current':0,'lastPassed':-1,'multiple':false,'scores':true,'total':1}",
//                "{'x':0,'y':0}",
//                true,
//                "['║....│  ║......." +
//                "║..┌─┘  └─╗....." +
//                "║..│      ║....." +
//                "║..│      ║....." +
//                "║..╚═┐  ╔═╝....." +
//                "║....│  ║......." +
//                "║....╚══╝......." +
//                "║..............." +
//                "║..............." +
//                "║..............." +
//                "║..............." +
//                "║..............." +
//                "║..............." +
//                "║..............." +
//                "║..............." +
//                "└───────────────'," +
//                "'----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "----------------" +
//                "--B-------------" +
//                "----------------" +
//                "----------------" +
//                "-☺--------------" +
//                "----------------']", single2);
//    }
//
//    private void assertBoardData(String levelProgress, String heroes, boolean onlyMyName, String levels, Single single) {
//        JSONObject json = single.getBoardAsString();
//
//        assertEquals(levelProgress,
//                JsonUtils.clean(JsonUtils.toStringSorted(json.get("levelProgress").toString())));
//
//        assertEquals(heroes,
//                JsonUtils.clean(JsonUtils.toStringSorted(json.get("offset").toString())));
//
//        assertEquals(levels,
//                JsonUtils.clean(json.get("layers").toString()));
//
//        assertEquals(true,
//                json.getBoolean("showName"));
//
//        assertEquals(onlyMyName,
//                json.getBoolean("onlyMyName"));
//    }
//
//    @Test
//    public void shouldRemoveOnePlayerFromMultiple() {
//        // given
//        givenFl("╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘",
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        // when
//        hero1().right();
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        single1.tick();
//        single2.tick();
//
//        hero1().right();
//        hero2().down();
//        single1.tick();
//        single2.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "--☺-" +
//                "-X--" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "--X-" +
//                "-☺--" +
//                "----");
//
//        // when
//        single2.destroy();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "--☺-" +
//                "----" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "--X-" +
//                "----" +
//                "----");
//    }
//
//    @Test
//    public void shouldChangeLevelToSingleFromMultiple_thenOtherPlayerShouldNotHide() {
//        // given
//        givenFl("╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘",
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        // when
//        hero1().right();
//        hero2().right();
//        single1.tick();
//        single2.tick();
//
//        single1.tick();
//        single2.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        // when
//        hero2().loadLevel(0);
//        single1.tick();
//        single2.tick();
//
//        // then
//        assertL(single1,
//                "╔══┐" +
//                "║S.│" +
//                "║.E│" +
//                "└──┘");
//
//        assertE(single1,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//
//        assertL(single2,
//                "╔══┐" +
//                "║SE│" +
//                "║..│" +
//                "└──┘");
//
//        assertE(single2,
//                "----" +
//                "-☺--" +
//                "----" +
//                "----");
//    }
//
//}
