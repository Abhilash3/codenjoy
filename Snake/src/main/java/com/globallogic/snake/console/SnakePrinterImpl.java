package com.globallogic.snake.console;

import com.globallogic.snake.model.Direction;
import com.globallogic.snake.model.Walls;
import com.globallogic.snake.model.artifacts.Apple;
import com.globallogic.snake.model.artifacts.Point;
import com.globallogic.snake.model.artifacts.Stone;
import com.globallogic.snake.model.Board;
import com.globallogic.snake.model.Snake;

import static com.globallogic.snake.model.Direction.*;

public class SnakePrinterImpl implements SnakePrinter {

    public final static char APPLE = '☺';
    public final static char STONE = '☻';
    public final static char BODY = '○';
    public static final char HEAD_LEFT = '◄';
    public static final char HEAD_RIGHT = '►';
    public static final char HEAD_UP = '▲';
    public static final char HEAD_DOWN = '▼';
    public final static char WALL = '☼';
    public static final char SPACE = ' ';

    int size;
	private char[][] monitor;

	public String print(Board board) {
		this.size = board.getSize();

		clean();
		printWalls(board.getWalls());
		printApple(board.getApple());
		printStone(board.getStone());
		printSnake(board.getSnake());

		return asString();
	}

	void clean() {
		monitor = new char[size][size];

		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				monitor[x][y] = SPACE;
			}
		}
	}

	void printWalls(Walls walls) {
        if (walls != null) {
            for (Point element : walls) {
                monitor[element.getX()][element.getY()] = WALL;
            }
        }
	}

	void printSnake(Snake snake) {
		for (Point element : snake) {
			monitor[element.getX()][element.getY()] = BODY;
		}

		printHead(snake);
	}

	private void printHead(Snake snake) {
		Point head = snake.getHead();
        monitor[head.getX()][head.getY()] = getHead(snake.getDirection());
	}

    private char getHead(Direction direction) {
        switch (direction){
            case UP: return HEAD_UP;
            case DOWN: return HEAD_DOWN;
            case LEFT: return HEAD_LEFT;
            case RIGHT: return HEAD_RIGHT;
            default: return BODY;
        }
    }

    void printStone(Stone stone) {
        if (stone != null){
            monitor[stone.getX()][stone.getY()] = STONE;
        }
	}

	void printApple(Apple apple) {
        if (apple != null){
		    monitor[apple.getX()][apple.getY()] = APPLE;
        }
	}

	String asString() {
		String result = "";
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				result += monitor[x][size - 1 - y];
			}
			result += "\n";
		}
		return result;
	}

}
