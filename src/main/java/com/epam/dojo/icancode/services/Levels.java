package com.epam.dojo.icancode.services;

import com.codenjoy.dojo.services.LengthToXY;
import com.codenjoy.dojo.utils.TestUtils;
import com.epam.dojo.icancode.model.Elements;
import com.epam.dojo.icancode.model.ILevel;
import com.epam.dojo.icancode.model.LevelImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by oleksandr.baglai on 18.06.2016.
 */
public class
Levels {

    public static final String DEMO_LEVEL =
            "                " +
                    " ############## " +
                    " #S...O.....˅.# " +
                    " #˃.....$O....# " +
                    " #....####....# " +
                    " #....#  #....# " +
                    " #.O###  ###.O# " +
                    " #.$#      #..# " +
                    " #..#      #$.# " +
                    " #O.###  ###O.# " +
                    " #....#  #....# " +
                    " #....####....# " +
                    " #....O$.....˂# " +
                    " #.˄.....O...E# " +
                    " ############## " +
                    "                ";

    public static final String MULTI_LEVEL =
            "                                      " +
            "   ######                             " +
            "   #...˅#                             " +
            "   #BB.O#      ########### ########   " +
            "   #B...#      #...B.BBBB# #˃.O..O#   " +
            "   #..O.#  #####˃.......O# #....BB#   " +
            "   #˃...####......O......# #O...O˂#   " +
            "   #.........###.......OO# #O....B#   " +
            "   #B...###### #.O.......# #B.#####   " +
            "   #B..O#      #.........###B.#       " +
            "   ##.###      #..BOO.........#       " +
            "    #.#        #B.B....B.B..BB#       " +
            "    #.#        ##.B.#######B.B###     " +
            "    #.#         #BB.#     #O..BB#     " +
            "    #.#         #...#     #....˂#     " +
            "   ##.###       #.B.#  ####.....#     " +
            "   #..B.#  ######.BB#  #....BO..#     " +
            "   #....#  #B.......####.BB.B...#     " +
            "   #O...####O...O........########     " +
            "   #..O.........S..O######            " +
            "   #˄...####.OB.....#                 " +
            "   #BB..#  #BBB....˄#                 " +
            "   ######  #˃..O...O#                 " +
            "           #####.####                 " +
            "               #.#                    " +
            "            ####.##########           " +
            "            #.........B.BB#           " +
            "            #.#####.BB..BB#           " +
            "            #.#   #....O..#           " +
            "     ########.##  ####....#           " +
            "     #.....˂...##### ###.##           " +
            "     #B.O....O.....#   #.#            " +
            "     #..O......###.#####.####         " +
            "     #..O..O.BB# #.BB˃...O..#         " +
            "     ########### #..........#         " +
            "                 ####.BO..OB#         " +
            "                    #########         " +
            "                                      ";


    public static final String LEVEL1 =
            "                " +
            "                " +
            "                " +
            "                " +
            "                " +
            "                " +
            "     ######     " +
            "     #S..E#     " +
            "     ######     " +
            "                " +
            "                " +
            "                " +
            "                " +
            "                " +
            "                " +
            "                ";

    public static final String LEVEL2 =
            "                " +
            "                " +
            "                " +
            "                " +
            "    ########    " +
            "    #S.....#    " +
            "    #..###.#    " +
            "    #..# #.#    " +
            "    #.$###.#    " +
            "    #......#    " +
            "    #..$..E#    " +
            "    ########    " +
            "                " +
            "                " +
            "                " +
            "                ";

    public static final String LEVEL3 =
            "                " +
            "                " +
            "                " +
            "                " +
            "    ########    " +
            "    #S.O..$#    " +
            "    #......#    " +
            "    ####...#    " +
            "       #.O.#    " +
            "    ####...#    " +
            "    #...O.E#    " +
            "    ########    " +
            "                " +
            "                " +
            "                " +
            "                ";

    public static final String LEVEL4 =
            "                " +
            "                " +
            "    #######     " +
            "    #S.O..#     " +
            "    ####..#     " +
            "       #..#     " +
            "    ####..###   " +
            "    #$..OO..#   " +
            "    #.###...#   " +
            "    #.# #...#   " +
            "    #.###..E#   " +
            "    #.......#   " +
            "    #########   " +
            "                " +
            "                " +
            "                ";

    public static final String LEVEL5 =
            "                " +
            "                " +
            "    ########    " +
            "    #S...B.#    " +
            "    ###B...#    " +
            "      #B...#    " +
            "    ###$B..#### " +
            "    #$....B..B# " +
            "    #.#####...# " +
            "    #.#   #...# " +
            "    #.#####.B.# " +
            "    #.E.....B$# " +
            "    ########### " +
            "                " +
            "                " +
            "                ";

    public static final String LEVEL6 =
            "                " +
            "  #####         " +
            "  #S.$#         " +
            "  #..B#######   " +
            "  #B..B˃...$#   " +
            "  ###....BBB#   " +
            "    #.B.....#   " +
            "    #...˄B..### " +
            "    #.###˃....# " +
            "    #.# #B.B.$# " +
            "    #.# #...### " +
            "    #.# #.$##   " +
            "    #E# ####    " +
            "    ###         " +
            "                " +
            "                ";

    public static List<ILevel> collectSingle() {
        return collect(LEVEL1, LEVEL2, LEVEL3, LEVEL4, LEVEL5, LEVEL6);
    }

    public static List<ILevel> collectMultiple() {
        return collect(MULTI_LEVEL);
    }

    private static List<ILevel> collect(String... levels) {
        List<ILevel> result = new LinkedList<ILevel>();
        for (String level : levels) {
            result.add(new LevelImpl(decorate(level)));
        }
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
                    out.setAt(x, y, Elements.BOX.ch());
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
                char ch = (map.isOutOf(x + xx, y + yy)) ? '#' :
                        (map.getAt(x + xx, y + yy) == ' ') ? '#' : ' ';
                out.setAt(xx + 1, yy + 1, ch);
            }
        }
        String actual = TestUtils.injectN(out.getMap());
        String expected = TestUtils.injectN(mask);
//        System.out.print(actual);
//        System.out.println("-----------");
        return actual.equals(expected);
    }

    public static int size() {
        return 16; // TODO think about it
    }
}
