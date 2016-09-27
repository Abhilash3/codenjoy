package com.epam.dojo.icancode.model;

/*-
 * #%L
 * iCanCode - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 EPAM
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


import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PrinterFactory;
import com.epam.dojo.icancode.services.Printer;
import com.epam.dojo.icancode.services.PrinterData;
import org.apache.commons.lang.StringUtils;

/**
 * А вот тут немного хак :) Дело в том, что фреймворк изначально не поддерживал игры типа "все на однмо поле", а потому
 * пришлось сделать этот декоратор. Борда (@see Sample) - одна на всех, а игры (@see Single) у каждого своя. Кода тут не много.
 */
public class Single implements Game {

    private ProgressBar progressBar;
    private Player player;

    public Single(ICanCode single, ICanCode multiple, EventListener listener, PrinterFactory factory, String save) {
        progressBar = new ProgressBar(single, multiple);
        player = new Player(listener, progressBar);
        progressBar.setPlayer(player);
        if (!StringUtils.isEmpty(save)) {
            progressBar.loadProgress(save);
        }
    }

    @Override
    public Joystick getJoystick() {
        return player.getHero();
    }

    @Override
    public int getMaxScore() {
        return player.getMaxScore();
    }

    @Override
    public int getCurrentScore() {
        return player.getScore();
    }

    @Override
    public boolean isGameOver() {
        return !player.hero.isAlive();
    }

    @Override
    public void newGame() {
        progressBar.newGame(player);
    }

    @Override
    public String getBoardAsString() { // TODO test me
        PrinterData data = getPrinter().getBoardAsString(2, player);

        String layers = String.format("[\"%s\",\"%s\"]", data.layers[0], data.layers[1]);

        StringBuilder heroes = new StringBuilder("[");
        Point point;
        for (int i = 0; i < data.heroes.size(); ++i) {
            point = data.heroes.get(i).getPosition();
            if (i != 0) {
                heroes.append(",");
            }
            heroes.append("{\"x\":" + point.getX() + ", \"y\":" + point.getY() + "}");
        }
        heroes.append("]");

        return "{\"layers\":" + layers +
                ", \"heroes\":" + heroes +
                ", \"levelProgress\":" + progressBar.printProgress() + "}";
    }

    @Override
    public void destroy() {
        progressBar.remove(player);
    }

    @Override
    public void clearScore() {
        player.clearScore();
    }

    @Override
    public Point getHero() {
        return player.getHero().getPosition();
    }

    @Override
    public String getSave() {
        return progressBar.printProgress();
    }

    @Override
    public void tick() {
        progressBar.tick();
    }

    public Player getPlayer() {
        return player;
    }

    public Printer getPrinter() {
        return progressBar.getPrinter();
    }
}
