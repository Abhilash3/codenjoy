package com.codenjoy.dojo.tetris.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.client.AbstractTextBoard;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.printer.CharElements;
import com.codenjoy.dojo.tetris.model.Elements;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.services.PointImpl.pt;

/**
 * Класс, обрабатывающий строковое представление доски в виде JSON.
 * Содержит ряд унаследованных методов {@see AbstractTextBoard},
 * но ты можешь добавить сюда любые свои методы на их основе.
 */
public class Board extends AbstractTextBoard {

    public Point getCurrentFigurePoint() {
        JSONObject point = getJson().getJSONObject("currentFigurePoint");
        int x = point.getInt("x");
        int y = point.getInt("y");
        return pt(x, y);
    }

    public Elements getCurrentFigureType() {
        String figureType = getJson().getString("currentFigureType");
        return getElement(figureType);
    }

    public List<Elements> getFutureFigures() {
        List<Elements> result = new LinkedList<>();
        for (Object figure : getJson().getJSONArray("futureFigures")) {
            result.add(getElement((String)figure));
        }
        return result;
    }

    public boolean isFree(int x, int y) {
        return getGlass().isAt(x, y, Elements.NONE);
    }

    private JSONObject getJson() {
        return new JSONObject(data);
    }

    public AbstractBoard<Elements> getGlass() {
        String glassString = getJson().getJSONArray("layers").getString(0);
        return (AbstractBoard) new AbstractBoard(){
            @Override
            public CharElements valueOf(char ch) {
                return Elements.valueOf(ch);
            }

            @Override
            protected int inversionY(int y) {
                return size() - 1 - y;
            }
        }.forString(glassString);
    }

    private Elements getElement(String figureType) {
        char ch = figureType.charAt(0);
        return Elements.valueOf(ch);
    }


}