package com.codenjoy.dojo.snake.model;

import com.codenjoy.dojo.services.Printer;
import com.codenjoy.dojo.snake.model.artifacts.Apple;
import com.codenjoy.dojo.snake.model.artifacts.BasicWalls;
import com.codenjoy.dojo.snake.model.artifacts.Stone;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SnakePrinterTest {

	private static final int BOARD_SIZE = 7;
	private Printer printer;
    private Snake snake;
    private Board board;

    @Before
	public void init() {
        board = mock(Board.class);
        when(board.getSize()).thenReturn(BOARD_SIZE);
        when(board.getApple()).thenReturn(new Apple(-1, -1));
        when(board.getStone()).thenReturn(new Stone(-1, -1));
        when(board.getWalls()).thenReturn(new Walls());
        when(board.getSnake()).thenReturn(new Snake(-1, -1));

        printer = new Printer(BOARD_SIZE, new SnakePrinter(board));
	}
	
	@Test
	public void checkCleanBoard() {
		assertEquals("       \n       \n       \n       \n       \n       \n       \n", printer.toString());
	}
	
	@Test
	public void checkPrintWall() {
        Walls walls = new Walls();
        walls.add(2, 2);
        walls.add(3, 3);
        walls.add(4, 4);
        when(board.getWalls()).thenReturn(walls);

		assertEquals(
				"       \n" +
                "       \n" +
                "    ☼  \n" +
                "   ☼   \n" +
                "  ☼    \n" +
                "       \n" +
                "       \n", printer.toString());
	}

    @Test
    public void checkPrintBasicWalls() {   // тут тестируем больше BasicWalls чем printer
        when(board.getWalls()).thenReturn(new BasicWalls(BOARD_SIZE));

        assertEquals(
                "☼☼☼☼☼☼☼\n" +
                "☼     ☼\n" +
                "☼     ☼\n" +
                "☼     ☼\n" +
                "☼     ☼\n" +
                "☼     ☼\n" +
                "☼☼☼☼☼☼☼\n", printer.toString());
    }
	
	@Test
	public void checkPrintApple() {
        when(board.getApple()).thenReturn(new Apple(2, 2));

        assertEquals(
				"       \n" +
				"       \n" +
				"       \n" +
				"       \n" +
				"  ☺    \n" +
				"       \n" +
				"       \n", printer.toString());
	}
	
	@Test
	public void checkPrintStone() {
        when(board.getStone()).thenReturn(new Stone(4, 4));

        assertEquals(
				"       \n" +
				"       \n" +
				"    ☻  \n" +
				"       \n" +
				"       \n" +
				"       \n" +
				"       \n", printer.toString());
	}
	
	@Test
	public void checkPrintSnake() {
		shouldSnake();
        moveUp();
        moveRight();
        moveUp();
        moveRight();

        assertSnake( 
                "       \n" +
                "    ╔► \n" +
                "   ╔╝  \n" +
                "   ╙   \n" +
                "       \n" +
                "       \n" +
                "       \n");
	}

    private void assertSnake(String expected) {
        when(board.getSnake()).thenReturn(snake);
        assertEquals(expected, printer.toString());
    }

    private void moveUp() {
        move(0, 1);
        snake.up();
    }

    private void move(int dx, int dy) {
        snake.move(snake.getX() + dx, snake.getY() + dy);
        snake.grow();
    }

    private void moveDown() {
        move(0, -1);
        snake.down();
    }

    private void moveLeft() {
        move(-1, 0);
        snake.left();
    }

    private void shouldSnake() {
        snake = new Snake(3, 3);
        snake.right();
    }

    private void moveRight() {
        move(1, 0);
        snake.right();
    }

    @Test
    public void checkPrintSnakeTailRight() {
        shouldSnake();
        assertSnake(
                "       \n" +
                "       \n" +
                "       \n" +
                "  ╘►   \n" +
                "       \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailLeft() {
        shouldSnake();
        moveLeft();
        assertSnake(
                "       \n" +
                "       \n" +
                "       \n" +
                "  ◄╕   \n" +
                "       \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailDown() {
        shouldSnake();
        moveDown();
        assertSnake(
                "       \n" +
                "       \n" +
                "       \n" +
                "   ╓   \n" +
                "   ▼   \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailUp() {
        shouldSnake();
        moveUp();
        assertSnake(
                "       \n" +
                "       \n" +
                "   ▲   \n" +
                "   ╙   \n" +
                "       \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailVerticalUp() {
        shouldSnake();
        moveUp();
        moveUp();
        assertSnake(
                "       \n" +
                "   ▲   \n" +
                "   ║   \n" +
                "   ╙   \n" +
                "       \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailVerticalDown() {
        shouldSnake();
        moveDown();
        moveDown();
        assertSnake(
                "       \n" +
                "       \n" +
                "       \n" +
                "   ╓   \n" +
                "   ║   \n" +
                "   ▼   \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailHorizontalLeft() {
        shouldSnake();
        moveLeft();
        moveLeft();
        assertSnake(
                "       \n" +
                "       \n" +
                "       \n" +
                " ◄═╕   \n" +
                "       \n" +
                "       \n" +
                "       \n");
    }


    @Test
    public void checkPrintSnakeTailHorizontalRight() {
        shouldSnake();
        moveRight();
        moveRight();
        assertSnake(
                "       \n" +
                "       \n" +
                "       \n" +
                "   ╘═► \n" +
                "       \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailRotateLeftUp() {
        shouldSnake();
        moveLeft();
        moveUp();
        assertSnake(
                "       \n" +
                "       \n" +
                "  ▲    \n" +
                "  ╚╕   \n" +
                "       \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailRotateLeftDown() {
        shouldSnake();
        moveLeft();
        moveDown();
        assertSnake(
                "       \n" +
                "       \n" +
                "       \n" +
                "  ╔╕   \n" +
                "  ▼    \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailRotateUpLeft() {
        shouldSnake();
        moveUp();
        moveLeft();
        assertSnake(
                "       \n" +
                "       \n" +
                "  ◄╗   \n" +
                "   ╙   \n" +
                "       \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailRotateUpRight() {
        shouldSnake();
        moveUp();
        moveRight();
        assertSnake(
                "       \n" +
                "       \n" +
                "   ╔►  \n" +
                "   ╙   \n" +
                "       \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailRotateDownLeft() {
        shouldSnake();
        moveDown();
        moveLeft();
        assertSnake(
                "       \n" +
                "       \n" +
                "       \n" +
                "   ╓   \n" +
                "  ◄╝   \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailRotateDownRight() {
        shouldSnake();
        moveDown();
        moveRight();
        assertSnake(
                "       \n" +
                "       \n" +
                "       \n" +
                "   ╓   \n" +
                "   ╚►  \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailRotateRightDown() {
        shouldSnake();
        moveRight();
        moveDown();
        assertSnake(
                "       \n" +
                "       \n" +
                "       \n" +
                "   ╘╗  \n" +
                "    ▼  \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnakeTailHorizontalRightUp() {
        shouldSnake();
        moveRight();
        moveUp();
        assertSnake(
                "       \n" +
                "       \n" +
                "    ▲  \n" +
                "   ╘╝  \n" +
                "       \n" +
                "       \n" +
                "       \n");
    }

    @Test
    public void checkPrintSnake2() {
        shouldSnake();
        moveDown();
        moveLeft();
        moveDown();
        moveLeft();
        assertSnake(
                "       \n" +
                "       \n" +
                "       \n" +
                "   ╓   \n" +
                "  ╔╝   \n" +
                " ◄╝    \n" +
                "       \n");
    }

	
}
