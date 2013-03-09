package com.codenjoy.bomberman.model;

import java.util.List;

/**
 * User: oleksandr.baglai
 * Date: 3/7/13
 * Time: 11:58 PM
 */
public interface BoomEngine {

    List<Point> boom(List<Point> barriers, int boardSize, Point source, int radius);

}
