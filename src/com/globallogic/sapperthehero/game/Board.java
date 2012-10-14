package com.globallogic.sapperthehero.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: oleksii.morozov
 * Date: 10/14/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class Board {
    private List<Cell> freeCells;
    private List<Cell> boardCells;
    private int boardSize;

    public Board(int boardSize, int minesCount) {
        this.boardSize = boardSize;
        freeCells = initializeBoardCells(boardSize);
        boardCells = initializeBoardCells(boardSize);
    }


    private List<Cell> initializeBoardCells(int boardSize) {
        List<Cell> cells = new ArrayList<Cell>();
        for (int xPosition = 0; xPosition < boardSize; xPosition++) {
            for (int yPosition = 0; yPosition < boardSize; yPosition++) {
                cells.add(new Cell(xPosition, yPosition));
            }
        }
        return cells;
    }

    public List<Cell> getFreeCells() {
        return freeCells;
    }

    public List<Cell> getBoardCells() {
        return boardCells;
    }

    public int getBoardSize() {
        return boardSize;
    }


}
