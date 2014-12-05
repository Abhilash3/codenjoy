package com.codenjoy.dojo.collapse.model;

import com.codenjoy.dojo.loderunner.model.LoderunnerTest;
import com.codenjoy.dojo.services.Dice;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Sanja
 */
public class LevelBuilderTest {

    private Dice dice;
    private LevelBuilder builder;
    private final int SIZE = 4;

    @Before
    public void setup() {
        dice = mock(Dice.class);
    }

    @Test
    public void testGetBoard() throws Exception {
        when(dice.next(8)).thenReturn(0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3);
        builder = new LevelBuilder(dice, SIZE);

        assertB("☼☼☼☼" +
                "☼12☼" +
                "☼34☼" +
                "☼☼☼☼");
    }

    private void assertB(String expected) {
        assertEquals(LoderunnerTest.injectN(expected), LoderunnerTest.injectN(builder.getBoard()));
    }
}
