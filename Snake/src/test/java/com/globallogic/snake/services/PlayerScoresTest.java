package com.globallogic.snake.services;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: oleksandr.baglai
 * Date: 10/1/12
 * Time: 3:45 AM
 */
public class PlayerScoresTest {

    @Test
    public void shouldCollectScores() {
        PlayerScores scores = new PlayerScores(-10);

        scores.snakeEatApple();  //+2
        scores.snakeEatApple();  //+3
        scores.snakeEatApple();  //+4
        scores.snakeEatApple();  //+5

        scores.snakeEatStone();  //-10

        scores.snakeIsDead();    //-50

        assertEquals(-10 + 2 + 3 + 4 + 5 - 10 - 50, scores.getScore());
    }

    @Test
    public void shouldShortLengthWhenEatStone() {
        PlayerScores scores = new PlayerScores(0);

        scores.snakeEatApple();  //+2
        scores.snakeEatApple();  //+3
        scores.snakeEatApple();  //+4
        scores.snakeEatApple();  //+5
        scores.snakeEatApple();  //+6
        scores.snakeEatApple();  //+7
        scores.snakeEatApple();  //+8
        scores.snakeEatApple();  //+9
        scores.snakeEatApple();  //+10
        scores.snakeEatApple();  //+11

        scores.snakeEatStone();  //-10

        scores.snakeEatApple();  //+2
        scores.snakeEatApple();  //+3

        assertEquals(2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10 + 11 - 10 + 2 + 3, scores.getScore());
    }

    @Test
    public void shouldStartsFrom2AfterDead() {
        PlayerScores scores = new PlayerScores(100);

        scores.snakeIsDead();    //-50

        scores.snakeEatApple();  //+2
        scores.snakeEatApple();  //+3

        assertEquals(100 - 50 + 2 + 3, scores.getScore());
    }
}
