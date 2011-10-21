package com.globallogic.snake;



public class Board {

	private Snake snake;
	private Stone stone;
	private StoneGenerator stoneGenerator = new RandomStoneGenerator();
	private int size;  

	public Board(int size) {
		this.size = size;
		if (size%2 == 0) {
			throw new IllegalArgumentException();
		}
		
		int position = (size - 1)/2; 		
		snake = new Snake(position, position);
		
		stone = stoneGenerator.generateStone(snake, size);
	}
	
	public Board(StoneGenerator stoneGenerator, int size) {
		this(size);
		this.stoneGenerator = stoneGenerator;
		stone = stoneGenerator.generateStone(snake, size);
	}

	public Snake getSnake() {
		return snake; 
	}

	public Stone getStone() {  		
		return stone;				
	}

	public void tact() {
		snake.checkAlive();
		if (Direction.RIGHT.equals(snake.getDirection())) {
			snake.moveRight();
		} else if (Direction.UP.equals(snake.getDirection())) {
			snake.moveUp();
		} else if (Direction.DOWN.equals(snake.getDirection()))  {					
			snake.moveDown();
		} else {			
			snake.moveLeft();
		} 
		if (stone.getX() == snake.getX() && stone.getY() == snake.getY()) {
			snake.killMe();
		} 
		if (snake.getX() < 0 || snake.getY() >= size || snake.getY() < 0 || snake.getX() >= size) {
			snake.killMe();
		}
	}

	public boolean isGameOver() {
		return !snake.isAlive();
	}

	public Apple getApple() {
		return new Apple();
	}

}
