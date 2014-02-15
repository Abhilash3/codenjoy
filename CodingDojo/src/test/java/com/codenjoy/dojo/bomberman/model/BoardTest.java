package com.codenjoy.dojo.bomberman.model;

import com.codenjoy.dojo.bomberman.services.BombermanEvents;
import com.codenjoy.dojo.services.*;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.services.settings.SimpleParameter.v;
import static junit.framework.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * User: oleksandr.baglai
 * Date: 3/7/13
 * Time: 9:07 AM
 */
public class BoardTest {

    public int SIZE = 5;
    private SingleBoard board;
    private Joystick bomberman;
    private Level level;
    private WallsImpl walls;
    private GameSettings settings;
    private EventListener listener;
    private Dice meatChppperDice;
    private Dice bombermanDice;
    private Player player;
    private List bombermans;
    private Board field;

    @Before
    public void setUp() throws Exception {
        meatChppperDice = mock(Dice.class);
        bombermanDice = mock(Dice.class);

        level = mock(Level.class);
        canDropBombs(1);
        bombsPower(1);
        walls = mock(WallsImpl.class);
        when(walls.iterator()).thenReturn(new LinkedList<Wall>().iterator());
        settings = mock(GameSettings.class);
        listener = mock(EventListener.class);

        when(settings.getWalls(any(Board.class))).thenReturn(walls);
        when(settings.getLevel()).thenReturn(level);
        initBomberman();
        givenBoard(SIZE);
    }

    private void initBomberman() {
        dice(bombermanDice, 0, 0);
        MyBomberman bomberman = new MyBomberman(level, bombermanDice);
        when(settings.getBomberman(level)).thenReturn(bomberman);
        this.bomberman = bomberman;
    }

    private void givenBoard(int size) {
        when(settings.getBoardSize()).thenReturn(v(size));
        field = new Board(settings);
        board = new SingleBoard(field, listener);
        dice(bombermanDice, 0, 0);
        board.newGame();
        bomberman = board.getJoystick();
        player = board.getPlayer();
    }

    @Test
    public void shouldBoard_whenStartGame() {
        when(settings.getBoardSize()).thenReturn(v(10));

        Board board = new Board(settings);

        assertEquals(10, board.size());
    }

    @Test
    public void shouldBoard_whenStartGame2() {
        assertEquals(SIZE, field.size());
    }

    @Test
    public void shouldBombermanOnBoardAtInitPos_whenGameStart() {
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
    }

    @Test
    public void shouldBombermanOnBoardOneRightStep_whenCallRightCommand() {
        bomberman.right();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");
    }

    @Test
    public void shouldBombermanOnBoardTwoRightSteps_whenCallRightCommandTwice() {
        bomberman.right();
        board.tick();

        bomberman.right();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☺  \n");
    }

    @Test
    public void shouldBombermanOnBoardOneUpStep_whenCallDownCommand() {
        bomberman.up();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "     \n");
    }

    @Test
    public void shouldBombermanWalkUp() {
        bomberman.up();
        board.tick();

        bomberman.up();
        board.tick();

        bomberman.down();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "     \n");
    }

    @Test
    public void shouldBombermanStop_whenGoToWallDown() {
        bomberman.down();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
    }

    private void assertBombermanAt(int x, int y) {
        assertEquals(x, player.getBomberman().getX());
        assertEquals(y, player.getBomberman().getY());
    }

    @Test
    public void shouldBombermanWalkLeft() {
        bomberman.right();
        board.tick();

        bomberman.right();
        board.tick();

        bomberman.left();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");
    }

    @Test
    public void shouldBombermanStop_whenGoToWallLeft() {
        bomberman.left();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");
    }

    @Test
    public void shouldBombermanStop_whenGoToWallRight() {
        gotoMaxRight();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "    ☺\n");
    }

    @Test
    public void shouldBombermanStop_whenGoToWallUp() {
        gotoMaxUp();

        asrtBrd("☺    \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n");
    }

    private void gotoMaxUp() {
        for (int y = 0; y <= SIZE + 1; y++) {
            bomberman.up();
            board.tick();
        }
    }

    @Test
    public void shouldBombermanMovedOncePerTact() {
        bomberman.down();
        bomberman.up();
        bomberman.left();
        bomberman.right();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n");

        bomberman.right();
        bomberman.left();
        bomberman.down();
        bomberman.up();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺   \n" +
                "     \n");
    }

    @Test
    public void shouldBombDropped_whenBombermanDropBomb() {
        bomberman.act();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");
    }

    @Test
    public void shouldBombDropped_whenBombermanDropBombAtAnotherPlace() {
        bomberman.up();
        board.tick();

        bomberman.right();
        board.tick();

        bomberman.act();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n" +
                "     \n");
    }

    @Test
    public void shouldBombsDropped_whenBombermanDropThreeBomb() {
        canDropBombs(3);

        bomberman.up();
        board.tick();
        bomberman.act();
        board.tick();

        bomberman.right();
        board.tick();
        bomberman.act();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "2☻   \n" +
                "     \n");
    }

    private void canDropBombs(int countBombs) {
        reset(level);
        when(level.bombsCount()).thenReturn(countBombs);
    }

    // проверить, что бомбермен не может бомб дропать больше, чем у него в level прописано
    @Test
    public void shouldOnlyTwoBombs_whenLevelApproveIt() {
        canDropBombs(2);

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        bomberman.up();
        bomberman.act();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☻    \n" +
                "     \n");

        bomberman.up();
        bomberman.act();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "☻    \n" +
                "3    \n" +
                "     \n");

        bomberman.up();
        bomberman.act();
        board.tick();

        asrtBrd("     \n" +
                "☺    \n" +
                "3    \n" +
                "2    \n" +
                "     \n");
    }

    // бомберен не может дропать два бомбы на одно место
    @Test
    public void shouldOnlyOneBombPerPlace() {
        canDropBombs(2);

        bomberman.act();
        board.tick();

        bomberman.act();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☻    \n");

        assertEquals(1, field.getBombs().size());

        bomberman.right();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "2☺   \n");

        bomberman.right();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1 ☺  \n");

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "҉ ☺  \n");

        board.tick();   // бомб больше нет, иначе тут был бы взрыв второй

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☺  \n");
    }

    @Test
    public void shouldBoom_whenDroppedBombHas5Ticks() {
        bomberman.act();
        board.tick();
        bomberman.right();
        board.tick();
        bomberman.right();
        board.tick();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1 ☺  \n");

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");
    }

    // проверить, что я могу поставить еще одну бомбу, когда другая рванула
    @Test
    public void shouldCanDropNewBomb_whenOtherBoom() {
        shouldBoom_whenDroppedBombHas5Ticks();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");

        bomberman.act();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "  ☻  \n");
    }

    // если бомбермен стоит на бомбе то он умирает после ее взрыва
    @Test
    public void shouldKillBoomberman_whenBombExploded() {
        bomberman.act();
        bomberman.right();
        board.tick();
        board.tick();
        board.tick();

        board.tick();

        assertBombermanAlive();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1☺   \n");

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉Ѡ   \n");
        assertBombermanDie();

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n");
        assertBombermanDie();
    }

    private void assertBombermanDie() {
        assertTrue("Expected model over", board.isGameOver());
    }

    // после смерти ходить больше нельзя
    @Test
    public void shouldException_whenTryToMoveIfDead_goLeft() {
        killBomber();

        bomberman.left();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    private void killBomber() {
        bomberman.up();
        board.tick();
        bomberman.right();
        bomberman.act();
        board.tick();
        board.tick();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                " ҉   \n" +
                "҉Ѡ҉  \n" +
                " ҉   \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goUp() {
        killBomber();

        bomberman.up();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goDown() {
        killBomber();

        bomberman.down();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_goRight() {
        killBomber();

        bomberman.right();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    @Test
    public void shouldException_whenTryToMoveIfDead_dropBomb() {
        killBomber();

        bomberman.act();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n" +
                "     \n");
    }

    // если бомбермен стоит под действием ударной волны, он умирает
    @Test
    public void shouldKillBoomberman_whenBombExploded_blastWaveAffect_fromLeft() {
        bomberman.act();
        board.tick();
        bomberman.right();
        board.tick();
        board.tick();

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "1☺   \n");
        assertBombermanAlive();

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉Ѡ   \n");
        assertBombermanDie();

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " Ѡ   \n");
        assertBombermanDie();
    }

    @Test
    public void shouldKillBoomberman_whenBombExploded_blastWaveAffect_fromRight() {
        bomberman.right();
        board.tick();
        bomberman.act();
        board.tick();
        bomberman.left();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺1   \n");
        assertBombermanAlive();

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "Ѡ҉҉  \n");
        assertBombermanDie();

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n");
        assertBombermanDie();
    }

    @Test
    public void shouldKillBoomberman_whenBombExploded_blastWaveAffect_fromUp() {
        bomberman.up();
        bomberman.act();
        board.tick();
        bomberman.down();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "1    \n" +
                "☺    \n");
        assertBombermanAlive();

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉   \n" +
                "Ѡ    \n");
        assertBombermanDie();

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n");
        assertBombermanDie();
    }

    private void assertBombermanAlive() {
        assertFalse(board.isGameOver());
    }

    @Test
    public void shouldKillBoomberman_whenBombExploded_blastWaveAffect_fromDown() {
        bomberman.down();
        board.tick();
        bomberman.act();
        board.tick();
        bomberman.up();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                "1    \n");
        assertBombermanAlive();

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n" +
                "҉҉   \n");
        assertBombermanDie();

        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "Ѡ    \n" +
                "     \n");
        assertBombermanDie();
    }

    @Test
    public void shouldNoKillBoomberman_whenBombExploded_blastWaveAffect_fromDownRight() {
        bomberman.down();
        board.tick();
        bomberman.right();
        board.tick();
        bomberman.act();
        board.tick();
        bomberman.up();
        board.tick();
        bomberman.left();
        board.tick();

        board.tick();

        assertBombermanAlive();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺    \n" +
                " 1   \n");

        board.tick();

        assertBombermanAlive();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "☺҉   \n" +
                "҉҉҉  \n");
    }

    @Test
    public void shouldSameBoomberman_whenNetFromBoard() {
        assertSame(bomberman, board.getJoystick());
    }

    @Test
    public void shouldBlastAfter_whenBombExposed() {
        bomberman.act();
        board.tick();
        bomberman.right();
        board.tick();
        bomberman.right();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "҉    \n" +
                "҉҉☺  \n");
    }

    @Test
    public void shouldBlastAfter_whenBombExposed_inOtherCorner() {
        gotoMaxUp();
        gotoMaxRight();

        bomberman.act();
        board.tick();
        bomberman.left();
        board.tick();
        bomberman.left();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("  ☺҉҉\n" +
                "    ҉\n" +
                "     \n" +
                "     \n" +
                "     \n");
    }

    @Test
    public void shouldBlastAfter_whenBombExposed_bombermanDie() {
        gotoBoardCenter();
        bomberman.act();
        board.tick();
        bomberman.down();
        board.tick();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("     \n" +
                "  ҉  \n" +
                " ҉҉҉ \n" +
                "  Ѡ  \n" +
                "     \n");

        assertBombermanDie();

        board.tick();
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "  Ѡ  \n" +
                "     \n");

        assertBombermanDie();
    }

    private void gotoBoardCenter() {
        for (int y = 0; y < SIZE / 2; y++) {
            bomberman.up();
            board.tick();
            bomberman.right();
            board.tick();
        }
    }

    private void asrtBrd(String expected) {
        assertEquals(expected, new Printer(field.size(), new BombermanPrinter(field, player)).toString());
    }

    // появляются стенки, которые конфигурятся извне
    @Test
    public void shouldBombermanNotAtWall() {
        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "☺    \n");

        givenBoardWithWalls();

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

    }

    // бомбермен не может пойти вперед на стенку
    @Test
    public void shouldBombermanStop_whenUpWall() {
        givenBoardWithWalls();

        bomberman.down();
        board.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldBombermanStop_whenLeftWall() {
        givenBoardWithWalls();

        bomberman.left();
        board.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldBombermanStop_whenRightWall() {
        givenBoardWithWalls();

        gotoMaxRight();

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼  ☺☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldBombermanStop_whenDownWall() {
        givenBoardWithWalls();

        gotoMaxUp();

        asrtBrd("☼☼☼☼☼\n" +
                "☼☺  ☼\n" +
                "☼ ☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
    }

    private void gotoMaxRight() {
        for (int x = 0; x <= SIZE + 1; x++) {
            bomberman.right();
            board.tick();
        }
    }

    private void givenBoardWithWalls() {
        givenBoardWithWalls(SIZE);
    }

    private void givenBoardWithWalls(int size) {
        withWalls(new OriginalWalls(v(size)));
        givenBoard(size);
    }

    private void givenBoardWithDestroyWalls() {
        givenBoardWithDestroyWalls(SIZE);
    }

    private void givenBoardWithDestroyWalls(int size) {
        withWalls(new DestroyWalls(new OriginalWalls(v(size))));
        givenBoard(size);
    }

    private void withWalls(Walls walls) {
        when(settings.getWalls(any(Board.class))).thenReturn(walls);
    }

    private void givenBoardWithOriginalWalls() {
        givenBoardWithOriginalWalls(SIZE);
    }

    private void givenBoardWithOriginalWalls(int size) {
        withWalls(new OriginalWalls(v(size)));
        givenBoard(size);
    }

    // бомбермен не может вернуться на место бомбы, она его не пускает как стена
    @Test
    public void shouldBombermanStop_whenGotoBomb() {
        bomberman.act();
        board.tick();
        bomberman.right();
        board.tick();

        bomberman.left();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "2☺   \n");
    }

    // проверить, что бомбермен может одноверменно перемещаться по полю и дропать бомбы за один такт, только как именно?
    @Test
    public void shouldBombermanWalkAndDropBombsTogetherInOneTact_bombFirstly() {
        bomberman.act();
        bomberman.right();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "4☺   \n");
    }

    @Test
    public void shouldBombermanWalkAndDropBombsTogetherInOneTact_moveFirstly() {
        bomberman.right();
        bomberman.act();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n");

        bomberman.right();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " 3☺  \n");
    }

    @Test
    public void shouldBombermanWalkAndDropBombsTogetherInOneTact_bombThanMove() {
        bomberman.act();
        board.tick();
        bomberman.right();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "3☺   \n");
    }

    @Test
    public void shouldBombermanWalkAndDropBombsTogetherInOneTact_moveThanBomb() {
        bomberman.right();
        board.tick();
        bomberman.act();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " ☻   \n");

        bomberman.right();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                " 3☺  \n");
    }

    @Test
    public void shouldWallProtectsBomberman() {
        givenBoardWithOriginalWalls();

        bomberman.act();
        goOut();

        asrtBrd("☼☼☼☼☼\n" +
                "☼  ☺☼\n" +
                "☼ ☼ ☼\n" +
                "☼1  ☼\n" +
                "☼☼☼☼☼\n");

        board.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼  ☺☼\n" +
                "☼҉☼ ☼\n" +
                "☼҉҉ ☼\n" +
                "☼☼☼☼☼\n");

        assertBombermanAlive();
    }

    @Test
    public void shouldWallProtectsBomberman2() {
        assertBombPower(5,
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉      ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉ ☺    ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉҉҉҉҉҉ ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");

        assertBombermanAlive();
    }

    private void bombsPower(int power) {
        when(level.bombsPower()).thenReturn(power);
    }

    // проверить, что разрыв бомбы длинной указанной в level
    @Test
    public void shouldChangeBombPower_to2() {
        assertBombPower(2,
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼҉ ☺    ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉҉҉    ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldChangeBombPower_to3() {
        assertBombPower(3,
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼       ☼\n" +
                "☼ ☼ ☼ ☼ ☼\n" +
                "☼       ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉ ☺    ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉҉҉҉   ☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldChangeBombPower_to6() {
        assertBombPower(6,
                "☼☼☼☼☼☼☼☼☼\n" +
                "☼҉      ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉      ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉ ☺    ☼\n" +
                "☼҉☼ ☼ ☼ ☼\n" +
                "☼҉҉҉҉҉҉҉☼\n" +
                "☼☼☼☼☼☼☼☼☼\n");
    }

    private void assertBombPower(int power, String expected) {
        givenBoardWithOriginalWalls(9);
        bombsPower(power);

        bomberman.act();
        goOut();
        board.tick();

        asrtBrd(expected);
    }

    private void goOut() {
        bomberman.right();
        board.tick();
        bomberman.right();
        board.tick();
        bomberman.up();
        board.tick();
        bomberman.up();
        board.tick();
    }

    // я немогу модифицировать список бомб на доске, меняя getBombs
    // но список бомб, что у меня на руках обязательно синхронизирован с теми, что на поле
    @Test
    public void shouldNoChangeOriginalBombsWhenUseBoardApiButTimersSynchronized() {
        canDropBombs(2);
        bomberman.act();
        bomberman.right();
        board.tick();
        bomberman.act();
        bomberman.right();
        board.tick();

        List<Bomb> bombs1 = field.getBombs();
        List<Bomb> bombs2 = field.getBombs();
        List<Bomb> bombs3 = field.getBombs();
        assertNotSame(bombs1, bombs2);
        assertNotSame(bombs2, bombs3);
        assertNotSame(bombs3, bombs1);

        Bomb bomb11 = bombs1.get(0);
        Bomb bomb12 = bombs2.get(0);
        Bomb bomb13 = bombs3.get(0);
        assertSame(bomb11, bomb12);
        assertSame(bomb12, bomb13);
        assertSame(bomb13, bomb11);

        Bomb bomb21 = bombs1.get(1);
        Bomb bomb22 = bombs2.get(1);
        Bomb bomb23 = bombs3.get(1);
        assertSame(bomb21, bomb22);
        assertSame(bomb22, bomb23);
        assertSame(bomb23, bomb21);

        board.tick();
        board.tick();

        assertFalse(bomb11.isExploded());
        assertFalse(bomb12.isExploded());
        assertFalse(bomb13.isExploded());

        board.tick();

        assertTrue(bomb11.isExploded());
        assertTrue(bomb12.isExploded());
        assertTrue(bomb13.isExploded());

        assertFalse(bomb21.isExploded());
        assertFalse(bomb22.isExploded());
        assertFalse(bomb23.isExploded());

        board.tick();

        assertTrue(bomb21.isExploded());
        assertTrue(bomb22.isExploded());
        assertTrue(bomb23.isExploded());
    }

    @Test
    public void shouldReturnShouldNotSynchronizedBombsList_whenUseBoardApi() {
        bomberman.act();
        bomberman.right();
        board.tick();

        List<Bomb> bombs1 = field.getBombs();
        assertEquals(1, bombs1.size());

        board.tick();
        board.tick();
        board.tick();
        board.tick();

        List<Bomb> bombs2 = field.getBombs();
        assertEquals(0, bombs2.size());

        assertEquals(1, bombs1.size());
    }

    @Test
    public void shouldNoChangeBlast_whenUseBoardApi() {
        bomberman.act();
        bomberman.right();
        board.tick();
        bomberman.right();
        board.tick();
        board.tick();
        board.tick();
        board.tick();

        List<Point> blasts1 = field.getBlasts();
        List<Point> blasts2 = field.getBlasts();
        List<Point> blasts3 = field.getBlasts();
        assertNotSame(blasts1, blasts2);
        assertNotSame(blasts2, blasts3);
        assertNotSame(blasts3, blasts1);

        Point blast11 = blasts1.get(0);
        Point blast12 = blasts2.get(0);
        Point blast13 = blasts3.get(0);
        assertNotSame(blast11, blast12);
        assertNotSame(blast12, blast13);
        assertNotSame(blast13, blast11);

        Point blast21 = blasts1.get(1);
        Point blast22 = blasts2.get(1);
        Point blast23 = blasts3.get(1);
        assertNotSame(blast21, blast22);
        assertNotSame(blast22, blast23);
        assertNotSame(blast23, blast21);
    }

    @Test
    public void shouldNoChangeWall_whenUseBoardApi() {
        givenBoardWithWalls();

        Walls walls1 = field.getWalls();
        Walls walls2 = field.getWalls();
        Walls walls3 = field.getWalls();
        assertNotSame(walls1, walls2);
        assertNotSame(walls2, walls3);
        assertNotSame(walls3, walls1);

        Iterator<Wall> iterator1 = walls1.iterator();
        Iterator<Wall> iterator2 = walls2.iterator();
        Iterator<Wall> iterator3 = walls3.iterator();

        PointImpl wall11 = iterator1.next();
        PointImpl wall12 = iterator2.next();
        PointImpl wall13 = iterator3.next();
        assertNotSame(wall11, wall12);
        assertNotSame(wall12, wall13);
        assertNotSame(wall13, wall11);

        PointImpl wall21 = iterator1.next();
        PointImpl wall22 = iterator2.next();
        PointImpl wall23 = iterator3.next();
        assertNotSame(wall21, wall22);
        assertNotSame(wall22, wall23);
        assertNotSame(wall23, wall21);
    }

    // в настройках уровня так же есть и разрущающиеся стены
    @Test
    public void shouldRandomSetDestroyWalls_whenStart() {
        givenBoardWithDestroyWalls();

        asrtBrd("#####\n" +
                "#   #\n" +
                "# # #\n" +
                "#☺  #\n" +
                "#####\n");
    }

    // они взрываются от ударной волны
    @Test
    public void shouldDestroyWallsDestroyed_whenBombExploded() {
        givenBoardWithDestroyWalls();

        bomberman.act();
        goOut();

        asrtBrd("#####\n" +
                "#  ☺#\n" +
                "# # #\n" +
                "#1  #\n" +
                "#####\n");

        board.tick();

        asrtBrd("#####\n" +
                "#  ☺#\n" +
                "#҉# #\n" +
                "H҉҉ #\n" +
                "#H###\n");
    }

    private void dice(Dice dice, int... values) {
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int value : values) {
            when = when.thenReturn(value);
        }
    }

    // появляются чертики, их несоклько за игру
    // каждый такт чертики куда-то рендомно муваются
    // если бомбермен и чертик попали в одну клетку - бомбермен умирает
    @Test
    public void shouldRandomMoveMonster() {
        givenBoardWithMeatChopper(11);
        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼        &☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(meatChppperDice, 1, Direction.DOWN.getValue());
        board.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼&☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(meatChppperDice, 1);
        board.tick();
        board.tick();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼        &☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(meatChppperDice, 0, Direction.LEFT.getValue());
        board.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼       & ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        board.tick();
        board.tick();
        board.tick();
        board.tick();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼&        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(meatChppperDice, 1, Direction.RIGHT.getValue());
        board.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼ &       ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(meatChppperDice, 0, Direction.LEFT.getValue());
        board.tick();
        board.tick();

        dice(meatChppperDice, Direction.LEFT.getValue());
        board.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼&        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(meatChppperDice, Direction.DOWN.getValue());
        board.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼&☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        board.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼Ѡ        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        Assert.assertTrue(board.isGameOver());
        verify(listener).event(BombermanEvents.KILL_BOMBERMAN);
    }

    private void givenBoardWithMeatChopper(int size) {
        dice(meatChppperDice, size - 2, size - 2);

        IBoard temp = mock(IBoard.class);
        when(temp.size()).thenReturn(size);
        MeatChoppers walls = new MeatChoppers(new OriginalWalls(v(size)), temp, v(1), meatChppperDice);
        bombermans = mock(List.class);
        when(bombermans.contains(anyObject())).thenReturn(false);
        when(temp.getBombermans()).thenReturn(bombermans);
        withWalls(walls);
        walls.regenerate();
        givenBoard(size);

        dice(meatChppperDice, 1, Direction.UP.getValue());  // Чертик будет упираться в стенку и стоять на месте
    }

    // чертик умирает, если попадает под взывающуюся бомбу
    @Test
    public void shouldDieMonster_whenBombExploded() {
        SIZE = 11;
        givenBoardWithMeatChopper(SIZE);

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼        &☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(meatChppperDice, 1, Direction.DOWN.getValue());
        board.tick();
        board.tick();
        board.tick();
        board.tick();
        board.tick();
        board.tick();
        board.tick();
        board.tick();

        dice(meatChppperDice, 1, Direction.LEFT.getValue());
        board.tick();
        board.tick();
        bomberman.act();
        bomberman.up();
        board.tick();
        bomberman.up();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼1 &      ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        board.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼҉☼ ☼ ☼ ☼ ☼\n" +
                "☼҉x       ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");

        dice(meatChppperDice, SIZE - 2, SIZE - 2, Direction.DOWN.getValue());
        board.tick();

        asrtBrd("☼☼☼☼☼☼☼☼☼☼☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼&☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼☺        ☼\n" +
                "☼ ☼ ☼ ☼ ☼ ☼\n" +
                "☼         ☼\n" +
                "☼☼☼☼☼☼☼☼☼☼☼\n");
    }

    @Test
    public void shouldFireEventWhenKillBomberman() {
        shouldKillBoomberman_whenBombExploded();

        verify(listener).event(BombermanEvents.KILL_BOMBERMAN);
    }

    @Test
    public void shouldNoEventsWhenBombermanNotMove() {
        board.tick();
        board.tick();
        board.tick();
        board.tick();

        verifyNoMoreInteractions(listener);
    }

    @Test
    public void shouldFireEventWhenKillWall() {
        givenBoardWithDestroyWallsAt(0, 0);

        bomberman.act();
        bomberman.right();
        board.tick();
        bomberman.right();
        board.tick();
        bomberman.right();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "H҉҉ ☺\n");

        verify(listener).event(BombermanEvents.KILL_DESTROY_WALL);
    }

    static class DestroyWallAt extends WallsDecorator {

        public DestroyWallAt(int x, int y, Walls walls) {
            super(walls);
            walls.add(new DestroyWall(x, y));
        }

        @Override
        public Wall destroy(int x, int y) {   // неразрушаемая стенка
            return walls.get(x, y);
        }

    }

    private void givenBoardWithDestroyWallsAt(int x, int y) {
        withWalls(new DestroyWallAt(x, y, new WallsImpl()));
        givenBoard(SIZE);
    }

    @Test
    public void shouldFireEventWhenKillMeatChopper() {
        givenBoardWithMeatChopperAt(0, 0);

        bomberman.act();
        bomberman.right();
        board.tick();
        bomberman.right();
        board.tick();
        bomberman.right();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ҉   \n" +
                "x҉҉ ☺\n");

        verify(listener).event(BombermanEvents.KILL_MEAT_CHOPPER);
    }


    static class MeatChopperAt extends WallsDecorator {

        public MeatChopperAt(int x, int y, Walls walls) {
            super(walls);
            walls.add(new MeatChopper(x, y));
        }

        @Override
        public Wall destroy(int x, int y) {   // неубиваемый монстрик
            return walls.get(x, y);
        }

    }

    private void givenBoardWithMeatChopperAt(int x, int y) {
        withWalls(new MeatChopperAt(x, y, new WallsImpl()));
        givenBoard(SIZE);
    }

    @Test
    public void shouldCalculateMeatChoppersAndWallKills() {
        withWalls(new MeatChopperAt(0, 0, new DestroyWallAt(0, 1, new MeatChopperAt(0, 2, new DestroyWallAt(0, 3, new WallsImpl())))));
        givenBoard(SIZE);
        canDropBombs(4);
        bombsPower(1);

        asrtBrd("     \n" +
                "#    \n" +
                "&    \n" +
                "#    \n" +
                "&☺   \n");

        bomberman.act();
        bomberman.up();
        board.tick();

        bomberman.act();
        bomberman.up();
        board.tick();

        bomberman.act();
        bomberman.up();
        board.tick();

        bomberman.act();
        bomberman.up();
        board.tick();
        asrtBrd(" ☺   \n" +
                "#4   \n" +
                "&3   \n" +
                "#2   \n" +
                "&1   \n");
        assertScores(0, 0);

        bomberman.right();
        board.tick();
        asrtBrd("  ☺  \n" +
                "#3   \n" +
                "&2   \n" +
                "#1   \n" +
                "x҉҉  \n");
        assertScores(1, 1);

        board.tick();
        asrtBrd("  ☺  \n" +
                "#2   \n" +
                "&1   \n" +
                "H҉҉  \n" +
                "&҉   \n");
        assertScores(2, 2);

        board.tick();
        asrtBrd("  ☺  \n" +
                "#1   \n" +
                "x҉҉  \n" +
                "#҉   \n" +
                "&    \n");
        assertScores(3, 3);

        board.tick();
        asrtBrd(" ҉☺  \n" +
                "H҉҉  \n" +
                "&҉   \n" +
                "#    \n" +
                "&    \n");
        assertScores(4, 4);

        bomberman.left();
        board.tick();
        bomberman.down();
        bomberman.act();
        board.tick();
        asrtBrd("     \n" +
                "#☻   \n" +
                "&    \n" +
                "#    \n" +
                "&    \n");
        board.tick();
        board.tick();
        board.tick();
        board.tick();
        asrtBrd(" ҉   \n" +
                "HѠ҉  \n" +
                "&҉   \n" +
                "#    \n" +
                "&    \n");
        assertScores(0, 5);

        assertBombermanDie();

        initBomberman();
        board.tick();
        board.newGame();
        asrtBrd("     \n" +
                "#    \n" +
                "&    \n" +
                "#    \n" +
                "&☺   \n");

        bomberman.act();
        bomberman.right();
        board.tick();
        bomberman.right();
        board.tick();
        board.tick();
        board.tick();
        board.tick();
        asrtBrd("     \n" +
                "#    \n" +
                "&    \n" +
                "#҉   \n" +
                "x҉҉☺ \n");
        assertScores(1, 5);
    }

    private void assertScores(int expectedCurrent, int expectedMax) {
        assertEquals(expectedCurrent, board.getCurrentScore());
        assertEquals(expectedMax, board.getMaxScore());
    }

    // если я двинулся за пределы стены и тут же поставил бомбу, то бомба упадет на моем текущем месте
    @Test
    public void shouldMoveOnBoardAndDropBombTogether() {
        givenBoardWithOriginalWalls();
        bomberman.up();
        board.tick();
        bomberman.up();
        board.tick();

        bomberman.left();
        bomberman.act();
        board.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼☻  ☼\n" +
                "☼ ☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
    }

    // чертик  может ходить по бомбам
    @Test
    public void shouldMonsterCanMoveOnBomb() {
        givenBoardWithMeatChopper(SIZE);

        asrtBrd("☼☼☼☼☼\n" +
                "☼  &☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        bomberman.up();
        board.tick();
        bomberman.up();
        board.tick();
        bomberman.right();
        bomberman.act();
        board.tick();
        bomberman.left();
        board.tick();
        bomberman.down();
        board.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ 2&☼\n" +
                "☼☺☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");

        dice(meatChppperDice, 1, Direction.LEFT.getValue());
        board.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼ & ☼\n" +
                "☼☺☼ ☼\n" +
                "☼   ☼\n" +
                "☼☼☼☼☼\n");
    }

    @Test
    public void shouldStopBlastWhenBombermanOrDestroyWalls() {
        bombsPower(5);
        withWalls(new DestroyWallAt(3, 0, new WallsImpl()));
        givenBoard(7);

        bomberman.act();
        bomberman.up();
        board.tick();
        bomberman.up();
        board.tick();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("       \n" +
                "       \n" +
                "       \n" +
                "       \n" +
                "Ѡ      \n" +
                "҉      \n" +
                "҉҉҉H   \n");
    }

    @Test
    public void shouldStopBlastWhenMeatChopper() {
        bombsPower(5);
        withWalls(new MeatChopperAt(4, 0, new WallsImpl()));
        givenBoard(7);

        bomberman.act();
        bomberman.up();
        board.tick();
        bomberman.up();
        board.tick();
        bomberman.up();
        board.tick();
        bomberman.right();
        board.tick();
        board.tick();

        asrtBrd("       \n" +
                "҉      \n" +
                "҉      \n" +
                "҉☺     \n" +
                "҉      \n" +
                "҉      \n" +
                "҉҉҉҉x  \n");
    }

    @Test
    public void shouldMeatChopperAppearAfterKill() {
        bombsPower(3);
        dice(meatChppperDice, 3, 0, Direction.DOWN.getValue());
        withWalls(new MeatChoppers(new WallsImpl(), field, v(1), meatChppperDice));
        givenBoard(SIZE);

        bomberman.act();
        bomberman.up();
        board.tick();
        bomberman.right();
        board.tick();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("     \n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉҉҉x \n");

        dice(meatChppperDice, 2, 2, Direction.DOWN.getValue());
        board.tick();

        asrtBrd("     \n" +
                "     \n" +
                "     \n" +
                " ☺&  \n" +
                "     \n");
    }

    @Test
    public void shouldMeatChopperNotAppearWhenDestroyWall() {
        bombsPower(3);
        dice(meatChppperDice, 4, 4, Direction.RIGHT.getValue());
        withWalls(new MeatChoppers(new DestroyWallAt(3, 0, new WallsImpl()), field, v(1), meatChppperDice));
        givenBoard(SIZE);

        bomberman.act();
        bomberman.up();
        board.tick();
        bomberman.right();
        board.tick();
        board.tick();
        board.tick();
        board.tick();

        asrtBrd("    &\n" +
                "҉    \n" +
                "҉    \n" +
                "҉☺   \n" +
                "҉҉҉H \n");

        dice(meatChppperDice, Direction.DOWN.getValue());
        board.tick();

        asrtBrd("     \n" +
                "    &\n" +
                "     \n" +
                " ☺   \n" +
                "   # \n");
    }

    // Чертик не может появится на бомбере!
    @Test
    public void shouldMeatChopperNotApperOnBommber() {
        shouldMonsterCanMoveOnBomb();
        bomberman.down();
        board.tick();

        asrtBrd("☼☼☼☼☼\n" +
                "☼x҉҉☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

        dice(meatChppperDice, 1, 2, Direction.DOWN.getValue());
        when(bombermans.contains(anyObject())).thenReturn(true);

        try {
            board.tick();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("java.lang.IllegalStateException: Dead loop at MeatChoppers.regenerate!", e.toString());
        }

        asrtBrd("☼☼☼☼☼\n" +
                "☼   ☼\n" +
                "☼ ☼ ☼\n" +
                "☼☺  ☼\n" +
                "☼☼☼☼☼\n");

    }


    // под разрущающейся стенкой может быть приз - это специальная стенка
    // появляется приз - увеличение длительности ударной волны - его может бомбермен взять и тогда ударная волна будет больше
    // появляется приз - хождение сквозь разрушающиеся стенки - взяв его, бомбермен может ходить через тенки
    // чертики тоже могут ставить бомбы

}
