package com.globallogic.sapperthehero.controller;

import com.globallogic.sapperthehero.game.Board;
import com.globallogic.sapperthehero.game.Cell;

public class BoardPresenter {
    private static final char SAPPER_CHAR = '@';
    private static final char MINE_CHAR = '*';
    private static final char FREE_CELL_CHAR = '.';
    private static final char BOUND_BOARD_CHAR = '#';
    private static final String MESSAGE_MINES_ON_BOARD = "мин на поле: ";
    private static final String MESSAGE_MINES_NEAR_ME = "мин рядом со мной: ";
    private static final String MESSAGE_MY_DETECTOR_CHARGE = "заряов у детектора: ";
    private int boardSize;
    private boolean cheats;
    private Board board;

    public BoardPresenter(boolean cheats, Board board) {
        this.cheats = cheats;
        this.board = board;
        boardSize = board.getBoardSize();
    }

    public String print() {
        StringBuffer result = new StringBuffer();
        for (int y = -1; y <= boardSize; y++) {
            for (int x = -1; x <= boardSize; x++) {
                if (isBoardBound(y, x)) {
                    result.append(printCellObject(BOUND_BOARD_CHAR));
                } else if (isSapper(y, x)) {
                    result.append(printCellObject(SAPPER_CHAR));
                } else if (cheats && isMine(y, x)) {
                    result.append(printCellObject(MINE_CHAR));
                } else {
                    result.append(printCellObject(FREE_CELL_CHAR));
                }
            }
            result.append("\n");
        }
        result.append(printMessages());
        return result.toString();
    }

    private String printMessages() {
        return MESSAGE_MINES_ON_BOARD + board.getMinesCount() + "\n"
                + MESSAGE_MINES_NEAR_ME + board.getMinesNearSapper() + "\n"
                + MESSAGE_MY_DETECTOR_CHARGE + board.getSapper().getMineDetectorCharge();
    }

    private boolean isBoardBound(int y, int x) {
        return y == -1 || x == -1 || y == boardSize || x == boardSize;
    }

    private boolean isSapper(int y, int x) {
        return new Cell(x, y).equals(board.getSapper());
    }

    private boolean isMine(int y, int x) {
        return board.getMines().contains(new Cell(x, y));
    }

    private String printCellObject(char cellObject) {
        return cellObject + " ";
    }

}
