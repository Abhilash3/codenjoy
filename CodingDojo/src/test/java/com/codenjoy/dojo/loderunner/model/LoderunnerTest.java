package com.codenjoy.dojo.loderunner.model;

import com.codenjoy.dojo.services.Dice;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: sanja
 * Date: 17.12.13
 * Time: 4:47
 */
public class LoderunnerTest {

    private Loderunner game;
    private Hero hero;
    private Dice dice;

    @Before
    public void setup() {
        dice = mock(Dice.class);
    }

    private void dice(int...ints) {
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int i : ints) {
            when = when.thenReturn(i);
        }
    }

    // есть карта со мной
    @Test
    public void shouldFieldAtStart() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼###☼" +
                "☼☼☼☼☼");
    }

    // я могу сверлить дырки
    @Test
    public void shouldDrillLeft() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        hero.act();
        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼.Я ☼" +
                "☼*##☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼ ##☼" +
                "☼☼☼☼☼");
    }

    @Test
    public void shouldDrillRight() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        hero.act();
        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ R.☼" +
                "☼##*☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ R ☼" +
                "☼## ☼" +
                "☼☼☼☼☼");
    }

    // я могу ходить влево и вправо
    @Test
    public void shouldMoveLeft() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼◄  ☼" +
                "☼###☼" +
                "☼☼☼☼☼");
    }

    @Test
    public void shouldMoveRight() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼  ►☼" +
                "☼###☼" +
                "☼☼☼☼☼");
    }

    // если небыло команды я никуда не иду
    @Test
    public void shouldStopWhenNoMoreRightCommand() {
        shouldMoveRight();

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼  ►☼" +
                "☼###☼" +
                "☼☼☼☼☼");
    }

    // я останавливаюсь возле границы
    @Test
    public void shouldStopWhenWallRight() {
        shouldMoveRight();

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼  ►☼" +
                "☼###☼" +
                "☼☼☼☼☼");
    }

    @Test
    public void shouldStopWhenWallLeft() {
        shouldMoveLeft();

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼◄  ☼" +
                "☼###☼" +
                "☼☼☼☼☼");
    }

    // яма заростает со временем
    @Test
    public void shouldDrillFilled() {
        shouldDrillLeft();

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼ ##☼" +
                "☼☼☼☼☼");

        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼4##☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼3##☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼2##☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼1##☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼###☼" +
                "☼☼☼☼☼");
    }

    // В просверленную яму я легко могу упасть
    @Test
    public void shouldFallInPitLeft() {
        shouldDrillLeft();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼ ##☼" +
                "☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼]  ☼" +
                "☼ ##☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼◄##☼" +
                "☼☼☼☼☼");
    }

    @Test
    public void shouldFallInPitRight() {
        shouldDrillRight();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ R ☼" +
                "☼## ☼" +
                "☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼  [☼" +
                "☼## ☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼##►☼" +
                "☼☼☼☼☼");
    }

    // я если упал то не могу передвигаться влево и вправо поскольку мне мешают стены
    @Test
    public void shouldCantGoLeftIfWall() {
        shouldFallInPitRight();

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼##◄☼" +
                "☼☼☼☼☼");
    }

    @Test
    public void shouldCantGoRightIfWall() {
        shouldFallInPitLeft();

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼►##☼" +
                "☼☼☼☼☼");
    }

    // я если упал, то могу перемещаться влево и вправо, если мне не мешают стены
    @Test
    public void shouldСanGoInPitIfNoWall() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        hero.act();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼.Я ☼" +
                "☼*##☼" +
                "☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼  ►☼" +
                "☼ ##☼" +
                "☼☼☼☼☼");

        hero.left();
        hero.act();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ .Я☼" +
                "☼ *#☼" +
                "☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ] ☼" +
                "☼  #☼" +
                "☼☼☼☼☼");

        hero.left();  // при падении я не могу передвигаться
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ◄#☼" +
                "☼☼☼☼☼");

        hero.left(); // а вот в яме куда угодно, пока есть место
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼◄ #☼" +
                "☼☼☼☼☼");

        hero.right(); // пока стенки не заростут
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ►#☼" +
                "☼☼☼☼☼");

        hero.right();
        game.tick();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼4►#☼" +
                "☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼◄ #☼" +
                "☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼2►#☼" +
                "☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼◄3#☼" +
                "☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼#►#☼" +
                "☼☼☼☼☼");

    }

    // если стенка замуровывается вместе со мной, то я умираю и появляюсь в рендомном месте
    @Test
    public void shouldIDieIPitFillWithMe() {
        shouldDrillLeft();

        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼2##☼" +
                "☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼]  ☼" +
                "☼1##☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼Ѡ##☼" +
                "☼☼☼☼☼");

        dice(2, 3);
        game.tick();         // ну а после смерти он появляется в рендомном месте

        assertE("☼☼☼☼☼" +
                "☼ ] ☼" +
                "☼   ☼" +
                "☼###☼" +
                "☼☼☼☼☼");

    }

    @Test
    public void shouldIDieIPitFillWithMe2() {
        shouldDrillLeft();

        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼3##☼" +
                "☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼]  ☼" +
                "☼2##☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼◄##☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼Ѡ##☼" +
                "☼☼☼☼☼");

        dice(2, 3);
        game.tick();         // ну а после смерти он появляется в рендомном месте

        assertE("☼☼☼☼☼" +
                "☼ ] ☼" +
                "☼   ☼" +
                "☼###☼" +
                "☼☼☼☼☼");

    }

    // я не могу сверлить уже отсверленную дырку, я даже не делаю вид что пытался
    @Test
    public void shouldCantDrillPit() {
        shouldDrillLeft();

        hero.right();
        game.tick();
        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼ ##☼" +
                "☼☼☼☼☼");

        hero.left();
        hero.act();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼ ##☼" +
                "☼☼☼☼☼");
    }

    // выполнения команд left + act не зависят от порядка - если они сделаны в одном тике, то будет дырка слева без перемещения
    @Test
    public void shouldDrillLeft_otherCommandSequence() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼###☼" +
                "☼☼☼☼☼");

//        hero.act();
        hero.left();
        hero.act();

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼.Я ☼" +
                "☼*##☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼ ##☼" +
                "☼☼☼☼☼");
    }

    // если я повернут в какую-то сторону и просто нажимаю сверлить то будет с той стороны дырка
    @Test
    public void shouldDrillLeft_onyActCommand() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        hero.act();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼.Я ☼" +
                "☼*##☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ Я ☼" +
                "☼ ##☼" +
                "☼☼☼☼☼");
    }

    @Test
    public void shouldDrillRight_onyActCommand() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ► ☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        hero.act();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ R.☼" +
                "☼##*☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ R ☼" +
                "☼## ☼" +
                "☼☼☼☼☼");
    }

    // на карте появляется золото, если я его беру то получаю +
    @Test
    public void shouldGoldOnMap_iCanGetIt() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ►$☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        dice(2, 3);
        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼ $ ☼" +
                "☼  ►☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼ $ ☼" +
                "☼ ◄ ☼" +
                "☼###☼" +
                "☼☼☼☼☼");
    }

    // проверить, что если новому обекту не где появится то программа не зависает - там бесконечный цикл потенциальный есть
    @Test(timeout = 1000)
    public void shouldNoDeadLoopWhenNewObjectCreation() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ►$☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        dice(4, 4);
        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼  ►☼" +
                "☼###☼" +
                "$☼☼☼☼");
    }

    // на карте появляются лестницы, я могу зайти на нее и выйти обратно
    @Test
    public void shouldICanGoOnLadder() {
        givenFl("☼☼☼☼☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼ ►H☼" +
                "☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼  Y☼" +
                "☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼ ◄H☼" +
                "☼☼☼☼☼");
    }

    // я могу карабкаться по лестнице вверх
    @Test
    public void shouldICanGoOnLadderUp() {
        givenFl("☼☼☼☼☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼ ►H☼" +
                "☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼  Y☼" +
                "☼☼☼☼☼");

        hero.up();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  H☼" +
                "☼  Y☼" +
                "☼  H☼" +
                "☼☼☼☼☼");

        hero.up();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  Y☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼☼☼☼☼");
    }

    // я не могу вылезти с лестницей за границы
    @Test
    public void shouldICantGoOnBarrierFromLadder() {
        shouldICanGoOnLadderUp();

        assertE("☼☼☼☼☼" +
                "☼  Y☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼☼☼☼☼");

        hero.up();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  Y☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  Y☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼☼☼☼☼");
    }

    // я могу спустится вниз, но не дальше границы экрана
    @Test
    public void shouldICanGoOnLadderDown() {
        shouldICanGoOnLadderUp();

        assertE("☼☼☼☼☼" +
                "☼  Y☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼☼☼☼☼");

        hero.down();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  H☼" +
                "☼  Y☼" +
                "☼  H☼" +
                "☼☼☼☼☼");

        hero.down();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼  Y☼" +
                "☼☼☼☼☼");

        hero.down();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼  Y☼" +
                "☼☼☼☼☼");
    }

    // я мошгу в любой момент спрыгнуть с лестницы и я буду падать до тех пор пока не наткнусь на препятствие
    @Test
    public void shouldICanFly() {
        shouldICanGoOnLadderUp();

        assertE("☼☼☼☼☼" +
                "☼  Y☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼ ]H☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  H☼" +
                "☼ ]H☼" +
                "☼  H☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  H☼" +
                "☼  H☼" +
                "☼ ◄H☼" +
                "☼☼☼☼☼");
    }

    // под стеной я не могу сверлить
    @Test
    public void shouldICantDrillUnderLadder() {
        givenFl("☼☼☼☼☼" +
                "☼  H☼" +
                "☼ ►H☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        hero.act();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼  H☼" +
                "☼ ►H☼" +
                "☼###☼" +
                "☼☼☼☼☼");
    }

    // под золотом я не могу сверлить
    @Test
    public void shouldICantDrillUnderGold() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ►$☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        hero.act();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ►$☼" +
                "☼###☼" +
                "☼☼☼☼☼");
    }

    // я могу поднятся по лестнице и зайти на площадку
    @Test
    public void shouldICanGoFromLadderToArea() {
        givenFl("☼☼☼☼☼" +
                "☼H  ☼" +
                "☼H# ☼" +
                "☼H◄ ☼" +
                "☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼H  ☼" +
                "☼H# ☼" +
                "☼Y  ☼" +
                "☼☼☼☼☼");

        hero.up();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼H  ☼" +
                "☼Y# ☼" +
                "☼H  ☼" +
                "☼☼☼☼☼");

        hero.up();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼Y  ☼" +
                "☼H# ☼" +
                "☼H  ☼" +
                "☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼H► ☼" +
                "☼H# ☼" +
                "☼H  ☼" +
                "☼☼☼☼☼");

        // и упасть
        hero.right();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼H [☼" +
                "☼H# ☼" +
                "☼H  ☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼H  ☼" +
                "☼H#[☼" +
                "☼H  ☼" +
                "☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼" +
                "☼H  ☼" +
                "☼H# ☼" +
                "☼H ►☼" +
                "☼☼☼☼☼");

    }

    // я не могу сверлить бетон
    @Test
    public void shouldICantDrillWall() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ► ☼" +
                "☼☼☼☼☼" +
                "☼☼☼☼☼");

        hero.act();
        game.tick();

        assertE("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ► ☼" +
                "☼☼☼☼☼" +
                "☼☼☼☼☼");
    }

    // пока я падаю я не могу двигаться влево и справо, даже если там есть площадки
    @Test
    public void shouldICantMoveWhenFall() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼  ►  ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼## ##☼" +
                "☼☼☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ]  ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼## ##☼" +
                "☼☼☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼  [  ☼" +
                "☼     ☼" +
                "☼## ##☼" +
                "☼☼☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼  ]  ☼" +
                "☼## ##☼" +
                "☼☼☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼##◄##☼" +
                "☼☼☼☼☼☼☼");
    }

    // появляются на карте трубы, если я с площадки захожу на трубу то я ползу по ней
    @Test
    public void shouldIPipe() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼►~~~ ☼" +
                "☼#    ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ }~~ ☼" +
                "☼#    ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ~}~ ☼" +
                "☼#    ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ~~} ☼" +
                "☼#    ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");
    }

    // с трубы я могу спрыгунть и тогда я буду падать до препятствия
    @Test
    public void shouldIFallFromPipe() {
        shouldIPipe();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ~~} ☼" +
                "☼#    ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        hero.right();
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ~~~[☼" +
                "☼#    ☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ~~~ ☼" +
                "☼#   [☼" +
                "☼     ☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ~~~ ☼" +
                "☼#    ☼" +
                "☼    [☼" +
                "☼     ☼" +
                "☼☼☼☼☼☼☼");

        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ~~~ ☼" +
                "☼#    ☼" +
                "☼     ☼" +
                "☼    ►☼" +
                "☼☼☼☼☼☼☼");
    }

    // если по дороге я встречаюсь с золотом, то я его захвачу
    @Test
    public void shouldIGetGoldWhenFallenFromPipe() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ $~~◄☼" +
                "☼ $  #☼" +
                "☼ $   ☼" +
                "☼ $   ☼" +
                "☼☼☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ $~{ ☼" +
                "☼ $  #☼" +
                "☼ $   ☼" +
                "☼ $   ☼" +
                "☼☼☼☼☼☼☼");

        hero.left();
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ${~ ☼" +
                "☼ $  #☼" +
                "☼ $   ☼" +
                "☼ $   ☼" +
                "☼☼☼☼☼☼☼");

        dice(1, 5);
        hero.left();
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼$    ☼" +
                "☼ ]~~ ☼" +
                "☼ $  #☼" +
                "☼ $   ☼" +
                "☼ $   ☼" +
                "☼☼☼☼☼☼☼");

        dice(2, 5);
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼$$   ☼" +
                "☼  ~~ ☼" +
                "☼ ]  #☼" +
                "☼ $   ☼" +
                "☼ $   ☼" +
                "☼☼☼☼☼☼☼");

        dice(3, 5);
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼$$$  ☼" +
                "☼  ~~ ☼" +
                "☼    #☼" +
                "☼ ]   ☼" +
                "☼ $   ☼" +
                "☼☼☼☼☼☼☼");

        dice(4, 5);
        game.tick();

        assertE("☼☼☼☼☼☼☼" +
                "☼$$$$ ☼" +
                "☼  ~~ ☼" +
                "☼    #☼" +
                "☼     ☼" +
                "☼ ◄   ☼" +
                "☼☼☼☼☼☼☼");
    }

    // появляются монстры - они за мной гонятся
    // монстр может похитить 1 золото
    // если монстр проваливается в ямку, которую я засверлил, и у него было золото - оно остается на поверхности
    // я могу ходить по монстру, который в ямке
    // монстр сидит в ямке некоторое количество тиков, потом он вылазит
    // если монстр не успел вылезти из ямки и она заросла то монстр умирает
    // когда монстр умирает, то на карте появляется новый
    // если я просверлил дырку и падаю в нее, а под ней ничего нет - то я падаю пока не найду препятствие
    // если в процессе падения я вдург наткнулся на трубу то я повисаю на ней
    // я не могу просверлить дырку непосредственно под монстром
    // появляется другой игрок, игра становится мультипользовательской
    // карта намного больше, чем квардартик вьюшка, и я подходя к границе просто передвигаю вьюшку
    // повляется многопользовательский режим игры в формате "стенка на стенку"
    //


    private void givenFl(String board) {
        game = new Loderunner(new LevelImpl(board), dice);
        hero = game.getHero();
    }

    private void assertE(String expected) {
        int size = (int) Math.sqrt(expected.length());
        String result = inject(expected, size, "\n");

        assertEquals(result, game.getBoardAsString());
    }

    @Test
    public void testInject() {
        assertEquals("1234^*5678^*90AB^*CDEF^*HIJK^*LMNO^*PQRS^*TUVW^*XYZ", inject("1234567890ABCDEFHIJKLMNOPQRSTUVWXYZ", 4, "^*"));
        assertEquals("1234^*5678^*90AB^*CDEF^*HIJK^*LMNO^*PQRS^*TUVW^*", inject("1234567890ABCDEFHIJKLMNOPQRSTUVW", 4, "^*"));
        assertEquals("1234^*5678^*90AB^*CDEF^*HIJK^*LMNO^*PQRS^*TUV", inject("1234567890ABCDEFHIJKLMNOPQRSTUV", 4, "^*"));
    }

    private String inject(String string, int position, String substring) {
        StringBuilder result = new StringBuilder();
        for (int index = 1; index < string.length() / position + 1; index++) {
            result.append(string.substring((index - 1)*position, index*position)).append(substring);
        }
        result.append(string.substring((string.length() / position) * position, string.length()));
        return result.toString();
    }

}
