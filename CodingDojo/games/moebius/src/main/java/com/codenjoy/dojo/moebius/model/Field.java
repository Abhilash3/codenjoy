package com.codenjoy.dojo.moebius.model;

import com.codenjoy.dojo.services.Point;

public interface Field {

    Point getFreeRandom();

    boolean isFree(Point pt);

    boolean isLine(Point pt);

    void setLine(Point pt, Elements element);

    void removeLine(Point pt);

    boolean isGameOver();
}
