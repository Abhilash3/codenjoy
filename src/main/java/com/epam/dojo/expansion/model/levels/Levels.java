package com.epam.dojo.expansion.model.levels;

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


import com.codenjoy.dojo.services.DLoggerFactory;
import com.codenjoy.dojo.services.LengthToXY;
import com.codenjoy.dojo.utils.TestUtils;
import com.epam.dojo.expansion.model.Elements;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Created by oleksandr.baglai on 18.06.2016.
 */
public class Levels {

    private static Logger logger = DLoggerFactory.getLogger(Levels.class);

    private static Map<String, String> levels = new LinkedHashMap<>();

    public static final String DEMO = make("DEMO");
    public static final String BIG_MULTI1 = make("BIG_MULTI1");
    public static final String BIG_MULTI2 = make("BIG_MULTI2");
    public static final String MULTI1 = make("MULTI1");
    public static final String MULTI2 = make("MULTI2");
    public static final String MULTI3 = make("MULTI3");
    public static final String MULTI4 = make("MULTI4");
    public static final String SINGLE1 = make("SINGLE1");
    public static final String SINGLE2 = make("SINGLE2");
    public static final String SINGLE3 = make("SINGLE3");
    public static final String SINGLE4 = make("SINGLE4");
    public static final String SINGLE5 = make("SINGLE5");
    public static final String SINGLE6 = make("SINGLE6");
    public static final String SINGLE7 = make("SINGLE7");

    private static String make(String name) {
        String level = loadFromFile(name);
        level = level.replace('-', ' ');
        String result = decorate(level);
        put(name, result);
        return name;
    }

    public static void put(String name, String result) {
        levels.put(name, result);
    }

    public static String get(String name) {
        return levels.get(name);
    }

    public static String loadFromFile(String name) {
        StringBuffer buffer = loadLines(
                "expansion/levels/" + name + ".lev",
                StringBuffer::new,
                (container, line) -> container.append(line)
        );
        return buffer.toString();
    }

    public static <T> T loadLines(String filePath,
                                Supplier<T> supplier,
                                BiFunction<T, String, T> applier)
    {
        T result = supplier.get();
        ClassLoader classLoader = Levels.class.getClassLoader();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        classLoader.getResourceAsStream(filePath))))
        {
            String line;
            while ((line = br.readLine()) != null) {
                applier.apply(result, line);
            }
        } catch (Exception e) {
            logger.error("Error durring loading file {}", filePath, e);
        }
        return result;
    }

    public static LevelsFactory collectSingle(int boardSize) {
        return () -> collect(boardSize,
                SINGLE1, SINGLE2, SINGLE3, SINGLE4,
                SINGLE5, SINGLE6, SINGLE7);
    }

    public static LevelsFactory collectMultiple(int boardSize, String... levels) {
        return () -> collect(boardSize, levels);
    }

    public static LevelsFactory none() {
        return () -> Arrays.asList();
    }

    public static LevelsFactory collectYours(final int viewSize, final String... boards) {
        return () -> {
            List<Level> result = new LinkedList<>();
            List<String> list = Arrays.asList(boards);
            for (String board : list) {
                String name = "level_" + Integer.toHexString(board.hashCode());
                Level level = new LevelImpl(name, board, viewSize);
                result.add(level);
            }
            return result;
        };
    }

    private static List<Level> collect(int viewSize, String... names) {
        List<Level> result = new LinkedList<>();
        for (String name : names) {
            String level = get(name);
            if (level == null) {
                level = get(make(name));
            }
            String resize = resize(level, viewSize);
            result.add(new LevelImpl(name, resize, viewSize));
        }
        return result;
    }

    static String resize(String level, int toSize) {
        double sqrt = Math.sqrt(level.length());
        int currentSize = (int) sqrt;
        if (sqrt - currentSize != 0) {
            throw new IllegalArgumentException("Level is not square: " + level);
        }
        if (currentSize >= toSize) {
            return level;
        }

        int before = (toSize - currentSize)/2;
        int after = (toSize - currentSize - before);
        String result = "";
        for (int i = 0; i < currentSize; i++) {
            String part = level.substring(i*currentSize, (i + 1)*currentSize);
            part = StringUtils.leftPad(part, before + part.length());
            part = StringUtils.rightPad(part, after + part.length());
            result += part;
        }
        result = StringUtils.leftPad(result, before*toSize + result.length());
        result = StringUtils.rightPad(result, after*toSize + result.length());

        return result;
    }

    public static String decorate(String level) {
        LengthToXY.Map map = new LengthToXY.Map(level);
        LengthToXY.Map out = new LengthToXY.Map(map.getSize());
        for (int x = 0; x < map.getSize(); ++x) {
            for (int y = 0; y < map.getSize(); ++y) {
                char at = map.getAt(x, y);
                if (at != '#') {
                    out.setAt(x, y, at);
                    continue;
                }

                if (chk("###" +
                        "#  " +
                        "#  ", x, y, map) ||
                    chk("## " +
                        "#  " +
                        "#  ", x, y, map) ||
                    chk("###" +
                        "#  " +
                        "   ", x, y, map) ||
                    chk("## " +
                        "#  " +
                        "   ", x, y, map))
                {
                    out.setAt(x, y, Elements.ANGLE_IN_LEFT.ch());
                } else
                if (chk("###" +
                        "  #" +
                        "  #", x, y, map) ||
                    chk(" ##" +
                        "  #" +
                        "  #", x, y, map) ||
                    chk("###" +
                        "  #" +
                        "   ", x, y, map) ||
                    chk(" ##" +
                        "  #" +
                        "   ", x, y, map))
                {
                    out.setAt(x, y, Elements.ANGLE_IN_RIGHT.ch());
                } else
                if (chk("#  " +
                        "#  " +
                        "###", x, y, map) ||
                    chk("   " +
                        "#  " +
                        "###", x, y, map) ||
                    chk("#  " +
                        "#  " +
                        "## ", x, y, map) ||
                    chk("   " +
                        "#  " +
                        "## ", x, y, map))
                {
                    out.setAt(x, y, Elements.ANGLE_BACK_LEFT.ch());
                } else
                if (chk("  #" +
                        "  #" +
                        "###", x, y, map) ||
                    chk("   " +
                        "  #" +
                        "###", x, y, map) ||
                    chk("  #" +
                        "  #" +
                        " ##", x, y, map) ||
                    chk("   " +
                        "  #" +
                        " ##", x, y, map))
                {
                    out.setAt(x, y, Elements.ANGLE_BACK_RIGHT.ch());
                } else
                if (chk("   " +
                        "   " +
                        "###", x, y, map) ||
                    chk("   " +
                        "   " +
                        " ##", x, y, map) ||
                    chk("   " +
                        "   " +
                        "## ", x, y, map) ||
                    chk("   " +
                        "   " +
                        " # ", x, y, map))
                {
                    out.setAt(x, y, Elements.WALL_BACK.ch());
                } else
                if (chk("#  " +
                        "#  " +
                        "#  ", x, y, map) ||
                    chk("   " +
                        "#  " +
                        "#  ", x, y, map) ||
                    chk("#  " +
                        "#  " +
                        "   ", x, y, map) ||
                    chk("   " +
                        "#  " +
                        "   ", x, y, map))
                {
                    out.setAt(x, y, Elements.WALL_LEFT.ch());
                } else
                if (chk("  #" +
                        "  #" +
                        "  #", x, y, map) ||
                    chk("  #" +
                        "  #" +
                        "   ", x, y, map) ||
                    chk("   " +
                        "  #" +
                        "  #", x, y, map) ||
                    chk("   " +
                        "  #" +
                        "   ", x, y, map))
                {
                    out.setAt(x, y, Elements.WALL_RIGHT.ch());
                } else
                if (chk("###" +
                        "   " +
                        "   ", x, y, map) ||
                    chk(" ##" +
                        "   " +
                        "   ", x, y, map) ||
                    chk("## " +
                        "   " +
                        "   ", x, y, map) ||
                    chk(" # " +
                        "   " +
                        "   ", x, y, map))
                {
                    out.setAt(x, y, Elements.WALL_FRONT.ch());
                } else
                if (chk("   " +
                        "   " +
                        "  #", x, y, map))
                {
                    out.setAt(x, y, Elements.WALL_BACK_ANGLE_LEFT.ch());
                } else
                if (chk("   " +
                        "   " +
                        "#  ", x, y, map))
                {
                    out.setAt(x, y, Elements.WALL_BACK_ANGLE_RIGHT.ch());
                } else
                if (chk("#  " +
                        "   " +
                        "   ", x, y, map))
                {
                    out.setAt(x, y, Elements.ANGLE_OUT_RIGHT.ch());
                } else
                if (chk("  #" +
                        "   " +
                        "   ", x, y, map))
                {
                    out.setAt(x, y, Elements.ANGLE_OUT_LEFT.ch());
                }
                if (chk("   " +
                        "   " +
                        "   ", x, y, map))
                {
                    out.setAt(x, y, Elements.BREAK.ch());
                }

            }
        }

        return out.getMap();
    }

    private static boolean chk(String mask, int x, int y, LengthToXY.Map map) {
        LengthToXY.Map mm = new LengthToXY.Map(mask);
        LengthToXY.Map out = new LengthToXY.Map(mm.getSize());
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = -1; yy <= 1; yy++) {
                char ch = ' ';
                if (map.isOutOf(x + xx, y + yy) || map.getAt(x + xx, y + yy) == ' ') {
                    ch = '#';
                }
                out.setAt(xx + 1, yy + 1, ch);
            }
        }
        String actual = TestUtils.injectN(out.getMap());
        String expected = TestUtils.injectN(mask);
        return actual.equals(expected);
    }
}