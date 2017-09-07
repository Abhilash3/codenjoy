package com.epam.dojo.expansion.model.interfaces;

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


import com.codenjoy.dojo.services.Point;
import com.epam.dojo.expansion.model.items.HeroForces;

import java.util.List;

/**
 * Created by Mikhail_Udalyi on 08.06.2016.
 */
public interface ICell extends Point {

    void captureBy(HeroForces income);

    void addItem(IItem item);

    boolean isPassable();

    <T extends IItem> T getItem(Class<T> type);

    <T extends IItem> T getItem(int layer);

    <T extends IItem> List<T> getItems(Class<T> clazz);

    <T extends IItem> List<T> getItems();

    void removeItem(IItem item);
}
