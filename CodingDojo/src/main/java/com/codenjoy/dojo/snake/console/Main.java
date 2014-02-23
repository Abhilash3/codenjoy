package com.codenjoy.dojo.snake.console;

import com.codenjoy.dojo.services.Console;
import com.codenjoy.dojo.services.ConsoleImpl;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.Runner;
import com.codenjoy.dojo.snake.model.SnakePrinter;
import com.codenjoy.dojo.snake.model.artifacts.BasicWalls;
import com.codenjoy.dojo.snake.model.artifacts.RandomArtifactGenerator;
import com.codenjoy.dojo.snake.model.Board;
import com.codenjoy.dojo.snake.model.BoardImpl;

public class Main {

	private static final int BOARD_SIZE = 5;

	public static void main(String[] args) {
		Game board = new BoardImpl(new RandomArtifactGenerator(),
                new BasicWalls(BOARD_SIZE), BOARD_SIZE);
		Console console = new ConsoleImpl();
		
		new Runner(board, console).playGame();
	} 

}
