package com.codenjoy.dojo.hex;

import com.codenjoy.dojo.client.LocalGameRunner;
import com.codenjoy.dojo.hex.client.Board;
import com.codenjoy.dojo.hex.client.ai.ApofigSolver;
import com.codenjoy.dojo.hex.services.GameRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.multiplayer.GameField;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SmokeTest {
    private int index;

    @Test
    public void test() {
        // given
        List<String> messages = new LinkedList<>();

        LocalGameRunner.timeout = 0;
        LocalGameRunner.out = (e) -> messages.add(e);
        LocalGameRunner.countIterations = 15;

        Dice dice = mock(Dice.class);
        when(dice.next(anyInt())).thenReturn(
                2, 2, // hero pos
                0, // where to go, direction
                0, // index of hero to move
                1, // jump 0 or clone 1
                0, 1, 1,
                1, 1, 1,
                2, 1, 1,
                3, 1, 1,
                0, 2, 1,
                1, 2, 1,
                2, 2, 0,
                3, 2, 1,
                0, 3, 0,
                1, 3, 0,
                2, 3, 0,
                3, 3, 1,
                0, 4, 1,
                1, 4, 1,
                2, 4, 0,
                3, 4, 1,
                0, 5, 0,
                1, 5, 0,
                2, 5, 1,
                3, 5, 0,
                0, 6, 0,
                1, 6, 0,
                2, 6, 1,
                3, 6, 0,
                0, 7, 1,
                1, 7, 1,
                2, 7, 1,
                3, 7, 0,
                0, 8, 1,
                1, 8, 0,
                2, 8, 1,
                3, 8, 0);

        GameRunner gameType = new GameRunner() {
            @Override
            protected Dice getDice() {
                return dice;
            }

            @Override
            protected String getMap() {
                return  "☼☼☼☼☼☼" +
                        "☼    ☼" +
                        "☼    ☼" +
                        "☼    ☼" +
                        "☼    ☼" +
                        "☼☼☼☼☼☼";
            }
        };

        // when
        LocalGameRunner.run(gameType,
                new ApofigSolver(dice),
                new Board());

        // then
        assertEquals("Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺  ☼\n" +
                        "☼    ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(2,3),LEFT\n" +
                        "Fire Event: WIN(1)\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼    ☼\n" +
                        "☼☺☺  ☼\n" +
                        "☼    ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(2,3),RIGHT\n" +
                        "Fire Event: WIN(1)\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼    ☼\n" +
                        "☼☺☺☺ ☼\n" +
                        "☼    ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(2,3),UP\n" +
                        "Fire Event: WIN(1)\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺  ☼\n" +
                        "☼☺☺☺ ☼\n" +
                        "☼    ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(2,2),DOWN\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺  ☼\n" +
                        "☼☺☺☺ ☼\n" +
                        "☼    ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(3,3),DOWN\n" +
                        "Fire Event: WIN(1)\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺  ☼\n" +
                        "☼☺☺☺ ☼\n" +
                        "☼  ☺ ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(3,4),LEFT\n" +
                        "Fire Event: WIN(1)\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺  ☼\n" +
                        "☼☺☺☺ ☼\n" +
                        "☼ ☺☺ ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(3,3),RIGHT\n" +
                        "Fire Event: WIN(1)\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺  ☼\n" +
                        "☼☺☺☺☺☼\n" +
                        "☼ ☺☺ ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(3,3),DOWN\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺  ☼\n" +
                        "☼☺☺☺☺☼\n" +
                        "☼ ☺☺ ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(3,4,1),LEFT\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺  ☼\n" +
                        "☼☺☺☺☺☼\n" +
                        "☼☺☺  ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(3,3),UP\n" +
                        "Fire Event: WIN(1)\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺☺ ☼\n" +
                        "☼☺☺☺☺☼\n" +
                        "☼☺☺  ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(3,3),UP\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺☺ ☼\n" +
                        "☼☺☺☺☺☼\n" +
                        "☼☺☺  ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(3,3,1),DOWN\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺☺ ☼\n" +
                        "☼☺☺☺☺☼\n" +
                        "☼☺☺  ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(4,3),UP\n" +
                        "Fire Event: WIN(1)\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺☺☺☼\n" +
                        "☼☺☺☺☺☼\n" +
                        "☼☺☺  ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(4,3),UP\n" +
                        "------------------------------------------------------------------------------------\n" +
                        "Board:\n" +
                        "☼☼☼☼☼☼\n" +
                        "☼    ☼\n" +
                        "☼ ☺☺☺☼\n" +
                        "☼☺☺☺☺☼\n" +
                        "☼☺☺  ☼\n" +
                        "☼☼☼☼☼☼\n" +
                        "\n" +
                        "Answer: ACT(4,3,1),DOWN\n" +
                        "------------------------------------------------------------------------------------",
                String.join("\n", messages));

    }
}
