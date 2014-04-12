package com.codenjoy.dojo.a2048.model;

import com.codenjoy.dojo.services.LengthToXY;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class LevelImpl implements Level {

    private final Settings settings;
    private LengthToXY xy;
    private Parameter<Integer> size;
    private Parameter<Integer> newAdd;
    private Parameter<Integer> score;

    private String map;

    public LevelImpl() {
        settings = new SettingsImpl();
        size = settings.addEditBox("Size").type(Integer.class).def(5);
        newAdd = settings.addEditBox("New numbers").type(Integer.class).def(3);
        score = settings.addEditBox("Score base").type(Integer.class).def(3);
        map = StringUtils.leftPad("", getSize(), ' ');
        xy = new LengthToXY(getSize());
    }

    public LevelImpl(String board) {
        this();
        map = board;
        size.update((int)Math.sqrt(board.length()));
        xy = new LengthToXY(getSize());
    }

    @Override
    public int getSize() {
        return size.getValue();
    }

    @Override
    public List<Number> getNumbers() {
        List<Number> result = new LinkedList<Number>();

        for (Elements element : Elements.values()) {
            if (element == Elements.NONE) continue;

            List<Point> points = getPointsOf(element);
            for (Point pt : points) {
                result.add(new Number(element.number(), pt));
            }
        }

        return result;
    }

    @Override
    public int getNewAdd() {
        return newAdd.getValue();
    }

    @Override
    public int getScore() {
        return score.getValue();
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    private List<Point> getPointsOf(Elements element) {
        List<Point> result = new LinkedList<Point>();
        for (int index = 0; index < map.length(); index++) {
            if (map.charAt(index) == element.getChar()) {
                result.add(xy.getXY(index));
            }
        }
        return result;
    }
}
