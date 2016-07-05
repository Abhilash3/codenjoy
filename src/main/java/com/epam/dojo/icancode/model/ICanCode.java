package com.epam.dojo.icancode.model;

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Tickable;
import com.epam.dojo.icancode.model.interfaces.ICell;
import com.epam.dojo.icancode.model.interfaces.IField;
import com.epam.dojo.icancode.model.interfaces.IItem;
import com.epam.dojo.icancode.model.interfaces.ILevel;
import com.epam.dojo.icancode.model.items.*;
import com.epam.dojo.icancode.services.Events;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * О! Это самое сердце игры - борда, на которой все происходит.
 * Если какой-то из жителей борды вдруг захочет узнать что-то у нее, то лучше ему дать интефейс {@see Field}
 * Борда реализует интерфейс {@see Tickable} чтобы быть уведомленной о каждом тике игры. Обрати внимание на {ICanCode#tick()}
 */
public class ICanCode implements Tickable, IField {

    public static final boolean SINGLE = false;
    public static final boolean MULTIPLE = true;

    private List<ILevel> levels;
    private boolean isMultiple;
    private ILevel level;
    private int currentNumOfLevel;
    private Dice dice;

    private int ticks;
    private List<Player> players;
    private boolean finished;

    public ICanCode(List<ILevel> levels, Dice dice, boolean isMultiple) {
        this.dice = dice;
        this.levels = levels;
        this.isMultiple = isMultiple;
        this.ticks = 0;
        this.finished = false;
        getNextLevel();

        players = new LinkedList<Player>();
    }

    private void getNextLevel() {
        level = levels.remove(0);
        level.setField(this);
        ++currentNumOfLevel;
    }

    /**
     * @see Tickable#tick()
     */
    @Override
    public void tick() {
        if (isMultiple) {
            ticks++;
            if (ticks % players.size() != 0) {
                return;
            }
            ticks = 0;
        }

        for (Player player : players) {
            checkLevel(player);
            player.getHero().tick();
        }

        for (IItem item : level.getItems(Tickable.class)) {
            if (item instanceof  Hero) {
                continue;
            }

            ((Tickable) item).tick();
        }

        for (Player player : players) {
            Hero hero = player.getHero();

            if (hero.isWin()) {
                player.event(Events.WIN(hero.getGoldCount()));
                player.setNextLevel();
            }

            if (!hero.isAlive()) {
                player.event(Events.LOOSE());
            }
        }
    }

    public int size() {
        return level.getSize();
    }

    @Override
    public boolean isBarrier(int x, int y) {
        return level.isBarrier(x, y);
    }

    @Override
    public ICell getStartPosition() {
        //TODO added check of existed barrier

        return level.getItems(Start.class).get(0).getCell();
    }

    @Override
    public void move(IItem item, int x, int y) {
        ICell cell = level.getCell(x, y);
        cell.addItem(item);
        cell.comeIn(item);
    }

    @Override
    public ICell getCell(int x, int y) {
        return level.getCell(x, y);
    }

    @Override
    public void reset() {
        // TODO think about it
        List<BaseItem> golds = level.getItems(Gold.class);

        if (isMultiple) {
            setRandomGolds(golds);
        }

        for (BaseItem gold : golds) {
            ((Gold) gold).reset();
        }
    }

    private void setRandomGolds(List<BaseItem> golds)
    {
        List<BaseItem> floors = level.getItems(Floor.class);

        for (int i = floors.size() - 1; i > -1; --i) {
            if (floors.get(i).getCell().getItems().size() > 1) {
                floors.remove(i);
            }
        }

        Gold gold;
        for (BaseItem item : golds) {
            gold = (Gold) item;

            if (gold.hidden && floors.size() > 0) {
                Random rand = new Random();
                int randomNum = rand.nextInt(floors.size());

                Floor floor = (Floor) floors.get(randomNum);
                floors.remove(randomNum);

                ICell fromCell = gold.getCell();
                floor.getCell().addItem(gold);
                fromCell.addItem(floor);
            }
        }
    }

    public List<Hero> getHeroes() {
        List<Hero> result = new ArrayList<Hero>(players.size());
        for (Player player : players) {
            result.add(player.getHero());
        }
        return result;
    }

    public void newGame(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
        player.newHero(this);
    }

    private void checkLevel(Player player) {
        if (!player.getHero().isAlive()) {
            player.newHero(this);
        }
        if (player.isNextLevel()) {
            if (!levels.isEmpty()) {
                getNextLevel();
            } else {
                if (!isMultiple) {
                    finished = true;
                }
            }
            player.newHero(this);
        }
    }

    public void remove(Player player) {
        players.remove(player);
    }

    public ILevel getCurrentLevel() {
        return level;
    }

    public boolean finished() {
        return finished;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String printProgress() {
        return "{\"current\":" + currentNumOfLevel + ", \"total\":" + (levels.size() + currentNumOfLevel) + "}";
    }
}
