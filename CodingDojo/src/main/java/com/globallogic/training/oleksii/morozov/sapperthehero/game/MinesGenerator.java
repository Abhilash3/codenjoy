package com.globallogic.training.oleksii.morozov.sapperthehero.game;

import com.globallogic.training.oleksii.morozov.sapperthehero.game.Board;
import com.globallogic.training.oleksii.morozov.sapperthehero.game.objects.Mine;

import java.util.List;

/**
 * User: oleksii.morozov
 * Date: 10/18/12
 * Time: 6:52 PM
 */
public interface MinesGenerator {
    List<Mine> get(int count, Board board);
}
