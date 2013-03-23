package com.codenjoy.dojo.snake.console;

import com.codenjoy.dojo.services.Console;
import com.codenjoy.dojo.services.Printer;
import com.codenjoy.dojo.snake.model.Board;
import com.codenjoy.dojo.snake.model.Snake;


public class SnakeRunner {
	
	private Board board;
	private Console console;
		
	public SnakeRunner(Board board, Console console) {
		this.board = board;
		this.console = console;
	}

	public void playGame() {
		Snake snake = board.getSnake();
		
		do {		
			printBoard();
			
			String line = console.read();
			if (line.length() != 0) {
				int ch = line.charAt(0);			
				
				if (ch == 's' || ch == 'ы') {
					snake.down();
				} else if (ch == 'a' || ch == 'ф') {
					snake.left();
				} else if (ch == 'd' || ch == 'в') {
					snake.right();
				} else if (ch == 'w' || ch == 'ц') {
					snake.up();
				} 				
			}
			board.tact();
		} while (!board.isGameOver());
		
		printBoard();
		console.print("Game over!");
	}
	
	private void printBoard() {
		console.print(board.toString());
	}

}
