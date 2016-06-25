package com.epam.dojo.icancode.model;

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.utils.TestUtils;
import com.epam.dojo.icancode.services.Events;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;

import java.util.*;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * User: sanja
 * Date: 17.12.13
 * Time: 4:47
 */
public class SingleTest {

    private Dice dice;
    private EventListener listener1;
    private EventListener listener2;
    private Single single1;
    private Single single2;

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
        Deque<String> strings = new LinkedList<>(Arrays.asList(boards));
        String multiple = strings.removeLast();
        List<ILevel> levelsSingle1 = createLevels(strings);
        List<ILevel> levelsSingle2 = createLevels(strings);
        List<ILevel> levelMultiple = createLevels(Arrays.asList(multiple));

        ICanCode gameSingle1 = new ICanCode(levelsSingle1, dice, ICanCode.SINGLE);
        ICanCode gameSingle2 = new ICanCode(levelsSingle2, dice, ICanCode.SINGLE);
        ICanCode gameMultiple = new ICanCode(levelMultiple, dice, ICanCode.MULTIPLE);
        listener1 = mock(EventListener.class);
        listener2 = mock(EventListener.class);

        single1 = new Single(gameSingle1, gameMultiple, listener1, null);
        single1.newGame();

        single2 = new Single(gameSingle2, gameMultiple, listener2, null);
        single2.newGame();
    }

    private Joystick hero1() {
        return single1.getJoystick();
    }

    private Joystick hero2() {
        return single2.getJoystick();
    }

    private List<ILevel> createLevels(Collection<String> boards) {
        List<ILevel> levels = new LinkedList<ILevel>();
        for (String board : boards) {
            ILevel level = new LevelImpl(board);
            levels.add(level);
        }
        return levels;
    }

    private void assertL(Single single, String expected) {
        assertEquals(TestUtils.injectN(expected),
                single.getPrinter().getBoardAsString(1, single.getPlayer())[0]);
    }

    private void assertE(Single single, String expected) {
        assertEquals(TestUtils.injectN(expected),
                single.getPrinter().getBoardAsString(2, single.getPlayer())[1]);
    }

    @Test
    public void shouldNextLevelWhenFinishCurrent() {
        // given
        givenFl("╔══┐" +
                "║SE│" +
                "║..│" +
                "└──┘",
                "╔══┐" +
                "║S.│" +
                "║E.│" +
                "└──┘",
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        // when
        hero1().right();
        single1.tick();

        verify(listener1).event(Events.WIN(0));
        reset(listener1);

        assertL(single1,
                "╔══┐" +
                "║SE│" +
                "║..│" +
                "└──┘");

        assertE(single1,
                "----" +
                "--☺-" +
                "----" +
                "----");

        // when
        single1.tick();

        // then
        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║E.│" +
                "└──┘");

        assertE(single1,
                "----" +
                "-☺--" +
                "----" +
                "----");

        // when
        hero1().down();
        single1.tick();

        // then
        verify(listener1).event(Events.WIN(0));
        reset(listener1);

        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║E.│" +
                "└──┘");

        assertE(single1,
                "----" +
                "----" +
                "-☺--" +
                "----");

        // when
        single1.tick();

        // then
        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "-☺--" +
                "----" +
                "----");

        // when
        hero1().down();
        single1.tick();

        // then
        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "----" +
                "-☺--" +
                "----");

        // when
        hero1().right();
        single1.tick();

        // then
        verify(listener1).event(Events.WIN(0));
        reset(listener1);

        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "----" +
                "--☺-" +
                "----");

        // when
        single1.tick();

        // then
        verifyNoMoreInteractions(listener1);

        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "-☺--" +
                "----" +
                "----");
    }

    @Test
    public void shouldSeveralPlayersCollectionAtLastLevel() {
        // given
        givenFl("╔══┐" +
                "║SE│" +
                "║..│" +
                "└──┘",
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        // when
        hero1().right();
        single1.tick();
        single2.tick();

        verify(listener1).event(Events.WIN(0));
        reset(listener1);
        verifyNoMoreInteractions(listener2);

        assertL(single1,
                "╔══┐" +
                "║SE│" +
                "║..│" +
                "└──┘");

        assertE(single1,
                "----" +
                "--☺-" +
                "----" +
                "----");

        assertL(single2,
                "╔══┐" +
                "║SE│" +
                "║..│" +
                "└──┘");

        assertE(single2,
                "----" +
                "-☺--" +
                "----" +
                "----");

        // when
        hero2().right();
        single1.tick(); // goes multiple
        single2.tick();

        // then
        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "-☺--" +
                "----" +
                "----");

        assertL(single2,
                "╔══┐" +
                "║SE│" +
                "║..│" +
                "└──┘");

        assertE(single2,
                "----" +
                "--☺-" +
                "----" +
                "----");

        // when
        hero1().down();
        single1.tick();
        single2.tick(); // goes multiple

        // then
        verifyNoMoreInteractions(listener1);
        verify(listener2).event(Events.WIN(0));
        reset(listener2);

        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "-X--" +
                "-☺--" +
                "----");

        assertL(single2,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single2,
                "----" +
                "-☺--" +
                "-X--" +
                "----");

        // when
        hero1().right(); // finished
        hero2().right();
        single1.tick();
        single2.tick();

        // then
        verify(listener1).event(Events.WIN(0));
        reset(listener1);
        verifyNoMoreInteractions(listener2);

        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "--X-" +
                "--☺-" +
                "----");

        assertL(single2,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single2,
                "----" +
                "--☺-" +
                "--X-" +
                "----");

        // when
        hero2().down();
        single1.tick(); // started
        single2.tick(); // finished

        // then
        verifyNoMoreInteractions(listener1);
        verify(listener2).event(Events.WIN(0));
        reset(listener2);

        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "-☺--" +
                "--X-" +
                "----");

        assertL(single2,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single2,
                "----" +
                "-X--" +
                "--☺-" +
                "----");

        // when
        single1.tick();
        single2.tick(); // started

        // then
        verifyNoMoreInteractions(listener1);
        verifyNoMoreInteractions(listener2);

        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "-☺--" +
                "----" +
                "----");

        assertL(single2,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single2,
                "----" +
                "-☺--" +
                "----" +
                "----");

        // when
        hero1().down();
        hero2().right();
        single1.tick();
        single2.tick();

        // then
        verifyNoMoreInteractions(listener1);
        verifyNoMoreInteractions(listener2);

        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "--X-" +
                "-☺--" +
                "----");

        assertL(single2,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single2,
                "----" +
                "--☺-" +
                "-X--" +
                "----");

        // when
        hero1().right();
        hero2().down();
        single1.tick(); // finished
        single2.tick(); // finished

        // then
        verify(listener1).event(Events.WIN(0));
        reset(listener1);

        verify(listener2).event(Events.WIN(0));
        reset(listener2);

        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "----" +
                "--☺-" +
                "----");

        assertL(single2,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single2,
                "----" +
                "----" +
                "--☺-" +
                "----");

        // when
        single1.tick(); // started
        single2.tick(); // started

        // then
        verifyNoMoreInteractions(listener1);
        verifyNoMoreInteractions(listener2);

        assertL(single1,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single1,
                "----" +
                "-☺--" +
                "----" +
                "----");

        assertL(single2,
                "╔══┐" +
                "║S.│" +
                "║.E│" +
                "└──┘");

        assertE(single2,
                "----" +
                "-☺--" +
                "----" +
                "----");
    }

}
