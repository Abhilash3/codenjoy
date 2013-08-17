package com;

import com.utils.Board;
import com.utils.Dice;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * User: sanja
 * Date: 17.08.13
 * Time: 16:44
 */
public class ApofigDirectionSolverTest {

    private ApofigDirectionSolver solver;
    private Dice dice;

    @Before
    public void setup() {
        dice = mock(Dice.class);
        solver = new ApofigDirectionSolver(dice);
    }

    @Test
    public void test() {
        assertD("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼" +     // кубик сказал вправо, а там свободно - иду
                "☼☺        # # ☼" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼##           ☼" +
                "☼ ☼ ☼#☼ ☼ ☼ ☼ ☼" +
                "☼   #    # #  ☼" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼             ☼" +
                "☼#☼ ☼ ☼#☼ ☼ ☼#☼" +
                "☼  #  #       ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ ##      #   ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ #   #  &    ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼",
                "RIGHT", Direction.RIGHT);
    }

    @Test
    public void test2() {
        assertD("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼" +   // кубик сказал вниз, а там свободно - иду
                "☼☺        # # ☼" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼##           ☼" +
                "☼ ☼ ☼#☼ ☼ ☼ ☼ ☼" +
                "☼   #    # #  ☼" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼             ☼" +
                "☼#☼ ☼ ☼#☼ ☼ ☼#☼" +
                "☼  #  #       ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ ##      #   ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ #   #  &    ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼",
                "DOWN", Direction.DOWN);
    }

    @Test
    public void test3() {
        assertD("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼" +   // кубик сказал вверх, но я могу поставить бомбу! поставлю ее
                "☼         # # ☼" +
                "☼☺☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼##           ☼" +
                "☼ ☼ ☼#☼ ☼ ☼ ☼ ☼" +
                "☼   #    # #  ☼" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼             ☼" +
                "☼#☼ ☼ ☼#☼ ☼ ☼#☼" +
                "☼  #  #       ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ ##      #   ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ #   #  &    ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼",
                "ACT,UP", Direction.UP);
    }

    @Test
    public void test4() {
        assertD("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼" +   // не могу пойти вниз, а кубик того требует - взрываюсь!
                "☼#        # # ☼" +
                "☼☺☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼##           ☼" +
                "☼ ☼ ☼#☼ ☼ ☼ ☼ ☼" +
                "☼   #    # #  ☼" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼             ☼" +
                "☼#☼ ☼ ☼#☼ ☼ ☼#☼" +
                "☼  #  #       ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ ##      #   ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ #   #  &    ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼",
                "ACT", repeat(11, Direction.DOWN));
    }

    private Direction[] repeat(int count, Direction direction) {
        Direction[] result = new Direction[count];
        Arrays.fill(result, direction);
        return result;
    }

    @Test
    public void test5() {
        assertD("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼" +   // бомбу уже поставил, кубик говорит иди вниз, спрошу ка я еще раз у него пока не скажет идти вверх
                "☼         # # ☼" +
                "☼☻☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼##           ☼" +
                "☼ ☼ ☼#☼ ☼ ☼ ☼ ☼" +
                "☼   #    # #  ☼" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼             ☼" +
                "☼#☼ ☼ ☼#☼ ☼ ☼#☼" +
                "☼  #  #       ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ ##      #   ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ #   #  &    ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼",
                "UP", Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP);
    }

    @Test
    public void test6() {
        assertD("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼" +   // мне на взрыв от бомбы идти нельзя, останусь на месте
                "☼ ☺#      # # ☼" +
                "☼1☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼##           ☼" +
                "☼ ☼ ☼#☼ ☼ ☼ ☼ ☼" +
                "☼   #    # #  ☼" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼             ☼" +
                "☼#☼ ☼ ☼#☼ ☼ ☼#☼" +
                "☼  #  #       ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ ##      #   ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ #   #  &    ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼",
                "STOP", Direction.LEFT);
    }

    @Test
    public void test7() {
        assertD("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼" +   // мне на взрыв от бомбы идти нельзя (даже если еще время есть), останусь на месте
                "☼ ☺#      # # ☼" +
                "☼4☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼##           ☼" +
                "☼ ☼ ☼#☼ ☼ ☼ ☼ ☼" +
                "☼   #    # #  ☼" +
                "☼ ☼ ☼ ☼#☼ ☼ ☼ ☼" +
                "☼             ☼" +
                "☼#☼ ☼ ☼#☼ ☼ ☼#☼" +
                "☼  #  #       ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ ##      #   ☼" +
                "☼ ☼ ☼ ☼ ☼ ☼ ☼#☼" +
                "☼ #   #  &    ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼",
                "STOP", Direction.LEFT);
    }


    private void assertD(String board, String expected, Direction... directions) {
        List<Integer> dices = new LinkedList<Integer>();
        for (Direction d : directions) {
            dices.add(d.value);
        }
        Integer first = dices.remove(0);
        when(dice.next(anyInt())).thenReturn(first, dices.toArray(new Integer[0]));

        String actual = solver.get(new Board(board));

        verify(dice, times(directions.length)).next(anyInt());
        assertEquals(expected, actual);
    }

}
