package com.codenjoy.dojo.chess.model;

import com.codenjoy.dojo.chess.model.figures.Figure;
import com.codenjoy.dojo.services.GamePrinter;
import com.codenjoy.dojo.services.Point;

import java.util.List;

public class ChessPrinter implements GamePrinter {

    private final Chess game;
    private Player player;

    private List<Figure> figures;

    public ChessPrinter(Chess game, Player player) {
        this.player = player;
        this.game = game;
    }

    @Override
    public boolean init() {
        figures = game.getFigures();
        return true;
    }

    @Override
    public char get(Point pt) {
        if (figures.contains(pt)) {
            Figure figure = figures.get(figures.indexOf(pt));

            return Elements.valueOf(figure).ch;
        }

        return Elements.NONE.ch;
    }

    @Override
    public void printAll(Filler filler) {
        // TODO использовать этот метод
    }
}
