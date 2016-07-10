package net.tetris.online.service;

import com.codenjoy.dojo.tetris.model.Figure;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * User: serhiy.zelenin
 * Date: 10/27/12
 * Time: 9:37 PM
 */
public class GameLogFileTest {

    private ServiceConfigFixture fixture;
    private GameLogFile logFile;
    private FileChannel fileLock;

    @Before
    public void setUp() {
        fixture = new ServiceConfigFixture();
        fixture.setup();
        logFile = new GameLogFile(fixture.getConfiguration(), "testUser", "123");
    }

    @After
    public void tearDown() throws IOException {
        unlockAndCloseGameLogFile();
        fixture.tearDown();
    }

    private void unlockAndCloseGameLogFile() throws IOException {
        if (fileLock != null) {
            fileLock.close();
        }
        logFile.close();
    }

    @Test
    public void shouldBeFalseHasNextWhenEmptyFile() throws IOException {
        createEmptyLogFile();

        assertFalse(logFile.readNextStep());
    }

    @Test
    public void shouldBeFalseHasNextWhenFileNotExists() throws IOException {
        assertFalse(logFile.readNextStep());
    }

    @Test
    public void shouldBeTrueHasNextWhenOneLine() throws IOException {
        logFile.log("request", "response");

        assertTrue(logFile.readNextStep());
    }

    @Test
    public void shouldReturnFigureWhenOneLine() throws IOException {
        logFile.log("/tetrisServlet?figure=S&x=4&y=17&glass=+++", "");

        logFile.readNextStep();

        assertEquals(Figure.Type.S, logFile.getCurrentFigure());
    }

    @Test
    public void shouldReturnNextFiguresWhenOneLine() throws IOException {
        logFile.log("/tetrisServlet?figure=S&x=4&y=17&glass=+++&next=ITZO", "");

        logFile.readNextStep();

        assertEquals(Figure.Type.I, logFile.getFutureFigures().get(0));
        assertEquals(Figure.Type.T, logFile.getFutureFigures().get(1));
        assertEquals(Figure.Type.Z, logFile.getFutureFigures().get(2));
        assertEquals(Figure.Type.O, logFile.getFutureFigures().get(3));
    }

    @Test
    public void shouldReturnFigureWhenSeveralLines() throws IOException {
        logFile.log("/tetrisServlet?figure=S&x=4&y=17&glass=+++", "");
        logFile.log("/tetrisServlet?figure=T&x=4&y=16&glass=+++", "");

        logFile.readNextStep();
        logFile.readNextStep();

        assertEquals(Figure.Type.T, logFile.getCurrentFigure());
    }

    @Test
    public void shouldReturnNullWhenUnknownFigure() throws IOException {
        logFile.log("/tetrisServlet?figure=AAA&x=4&y=17&glass=+++", "");

        logFile.readNextStep();

        assertNull(logFile.getCurrentFigure());
    }

    @Test
    public void shouldReturnNullWhenUnknownPattern() throws IOException {
        logFile.log("bla-bla", "");

        logFile.readNextStep();

        assertNull(logFile.getCurrentFigure());
    }

    @Test
    public void shouldReturnResponse() throws IOException {
        logFile.log("", "left=1, right=2, rotate=3, drop");

        logFile.readNextStep();

        assertEquals("left=1, right=2, rotate=3, drop", logFile.getCurrentResponse());
    }

    @Test
    public void shouldReturnNullWhenExceptionWhileRead() throws IOException {
        logFile.log("some request", "some response");
        lockFile();

        assertFalse(logFile.readNextStep());
    }

    private void lockFile() throws IOException {
        fileLock = new RandomAccessFile(logFile.getPath(), "rw").getChannel();
        fileLock.lock();
    }

    private void createEmptyLogFile() throws IOException {
        new File(logFile.getPath()).getParentFile().mkdirs();
        new File(logFile.getPath()).createNewFile();
    }
}
