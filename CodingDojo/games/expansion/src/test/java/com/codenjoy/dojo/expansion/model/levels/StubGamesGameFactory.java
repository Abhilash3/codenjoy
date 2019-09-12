package com.codenjoy.dojo.expansion.model.levels;

/*-
 * #%L
 * expansion - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 - 2017 EPAM
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


import com.codenjoy.dojo.expansion.model.GameFactory;
import com.codenjoy.dojo.expansion.model.IField;

/**
 * Created by Oleksandr_Baglai on 2017-09-01.
 */
public class StubGamesGameFactory implements GameFactory {

    private IField multiple;
    private IField single;

    public StubGamesGameFactory(IField single, IField multiple) {
        this.single = single;
        this.multiple = multiple;
    }

    @Override
    public IField newMultiple() {
        return multiple;
    }

    @Override
    public IField existMultiple() {
        return multiple;
    }

    @Override
    public IField single() {
        return single;
    }
}
