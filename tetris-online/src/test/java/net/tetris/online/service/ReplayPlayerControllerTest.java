package net.tetris.online.service;

import com.codenjoy.dojo.tetris.model.Figure;
import net.tetris.services.MockAdvancedTetrisJoystik;
import net.tetris.services.Player;
import com.codenjoy.dojo.tetris.model.Plot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;

/**
 * User: serhiy.zelenin
 * Date: 10/28/12
 * Time: 9:39 AM
 */
public class ReplayPlayerControllerTest {
    private MockAdvancedTetrisJoystik joystick;
    private ServiceConfiguration configuration;
    private ServiceConfigFixture fixture;
    private GameLogFile logFile;
    private ReplayPlayerController controller;

    @Before
    public void setUp() {
        joystick = new MockAdvancedTetrisJoystik();
        fixture = new ServiceConfigFixture();
        fixture.setup();
        configuration = fixture.getConfiguration();
        logFile = new GameLogFile(fixture.getConfiguration(), "testUser", "1234");
        controller = new ReplayPlayerController(logFile);
    }

    @After
    public void tearDown() throws IOException {
        logFile.close();
        fixture.tearDown();
    }

    @Test
    public void shouldMoveJoystick() throws IOException, InterruptedException {
        logFile.log("some request", "left=1,right=2,rotate=3,drop");
        logFile.readNextStep();

        controller.requestControl(new Player("testUser", "REPLAY", null, null, null), Figure.Type.I, 123, 123, joystick, Collections.<Plot>emptyList(),
                Arrays.asList(Figure.Type.I));

        assertEquals("left=1,right=2,rotate=3,drop", joystick.toString());
    }

    @Test
    public void shouldMoveJoystick2() throws IOException, InterruptedException {
        logFile.log("some request", "left=1,right=2,rotate=3,drop");
        logFile.log("some request", "Left=3, Right=2, Rotate=1, Drop");
        logFile.readNextStep();
        logFile.readNextStep();

        controller.requestControl(new Player("testUser", "REPLAY", null, null, null), Figure.Type.I, 123, 123, joystick, Collections.<Plot>emptyList(), Arrays.asList(Figure.Type.I, Figure.Type.J, Figure.Type.L, Figure.Type.O));

        assertEquals("left=3,right=2,rotate=1,drop", joystick.toString());
    }

}
