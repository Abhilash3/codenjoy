package com.epam.dojo.expansion.model;

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


import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Tickable;
import com.codenjoy.dojo.utils.JsonUtils;
import com.epam.dojo.expansion.model.interfaces.ICell;
import com.epam.dojo.expansion.model.interfaces.IField;
import com.epam.dojo.expansion.model.interfaces.IItem;
import com.epam.dojo.expansion.model.interfaces.ILevel;
import com.epam.dojo.expansion.model.items.*;
import com.epam.dojo.expansion.services.Events;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * О! Это самое сердце игры - борда, на которой все происходит.
 * Если какой-то из жителей борды вдруг захочет узнать что-то у нее, то лучше ему дать интефейс {@see Field}
 * Борда реализует интерфейс {@see Tickable} чтобы быть уведомленной о каждом тике игры. Обрати внимание на {Expansion#tick()}
 */
public class Expansion implements Tickable, IField {

    public static final boolean SINGLE = false;
    public static final boolean MULTIPLE = true;

    private List<ILevel> levels;
    private ILevel level;

    private boolean isMultiple;

    private int ticks;
    private List<Player> players;
    private List<Player> losers;
    private boolean waitingOthers = false;

    public Expansion(List<ILevel> levels, Dice dice, boolean multiple) {
        this.levels = new LinkedList(levels);

        isMultiple = multiple;
        ticks = 0;

        players = new LinkedList();
        losers = new LinkedList();
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

        if (isWaiting()) return;

        for (Player player : players.toArray(new Player[0])) {
            player.tick();
        }

        for (Tickable item : level.getItems(Tickable.class)) {
            if (item instanceof  Hero) {
                continue;
            }

            item.tick();
        }

        for (Player player : players.toArray(new Player[0])) {
            Hero hero = player.getHero();

            hero.applyGold();

            if (isMultiple) {
                boolean win = checkStatus(player, hero);
                if (win) {
                    List<Player> renew = new LinkedList<>();
                    for (Player p : losers) {
                        remove(p);
                        renew.add(p);
                    }
                    remove(player);
                    renew.add(player);
                    losers.clear();
                    for (Player p : renew) {
                        newGame(p);
                    }
                    break;
                }
            }

            if (!hero.isAlive()) {
                // TODO продолжить тут
            }

            if (hero.isWin()) {
                player.event(Events.WIN(0));
                player.setNextLevel();
            }
        }
    }

    private boolean checkStatus(Player player, Hero hero) {
        if (losers.contains(player)) return false;

        List<HeroForces> allForces = level.getItems(HeroForces.class);
        boolean alone = true;
        boolean exists = false;
        for (HeroForces item : allForces) {
            alone &= item.itsMe(hero);
            exists |= item.itsMe(hero);
        }
        if (alone && players.size() != 1) {
            player.event(Events.WIN(1));
            return true;
        }
        if (!exists) {
            player.event(Events.LOOSE());
            losers.add(player);
            // players.remove(player); TODO продолжить тут
        }
        return false;
    }

    private void checkIsWinner(Hero hero) {
        List<HeroForces> allForces = level.getItems(HeroForces.class);
        boolean alone = true;
        for (HeroForces item : allForces) {
            alone &= item.itsMe(hero);
        }
        if (alone) {
            hero.setWin();
        }
    }

    public boolean isWaiting() {
        return waitingOthers && players.size() != 4;
    }

    public int size() {
        return level.getSize();
    }

    @Override
    public boolean isBarrier(int x, int y) {
        return level.isBarrier(x, y);
    }

    @Nullable
    @Override
    public Start getBaseOf(Hero hero) {
        List<Start> bases = level.getItems(Start.class);

        for (Start base : bases) {
            if (base.isOwnedBy(hero)) {
                return base;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Start getFreeBase() {
        List<Start> bases = level.getItems(Start.class);

        Collections.sort(bases, new Comparator<Start>() {
            @Override
            public int compare(Start o1, Start o2) {
                return Integer.compare(o1.index(), o2.index());
            }
        });

        Start free = null;
        for (Start place : bases) {
            if (place.isFree()) {
                free = place;
                break;
            }
        }
        return free;
    }

    @Override
    public ICell getEndPosition() {
        return level.getItems(Exit.class).get(0).getCell();
    }

    @Override
    public HeroForces tryIncreaseForces(Hero hero, int x, int y, int count) {
        if (count == 0) return HeroForces.EMPTY;

        ICell cell = level.getCell(x, y);

        List<HeroForces> forces = cell.getItems(HeroForces.class);
        if (forces.isEmpty()) {
            HeroForces income = new HeroForces(hero);
            capture(cell, income);
            income.tryIncrease(count);
            return income;
        } else if (forces.size() == 1) {
            HeroForces heroForces = forces.get(0);
            if (heroForces.itsMe(hero)) {
                heroForces.tryIncrease(count);
                return heroForces;
            } else {
                HeroForces income = new HeroForces(hero, count);
                return attack(heroForces, income);
            }
        } else {
            throw new IllegalStateException("There are more than 1 heroes on cell!");
        }
    }

    private void capture(ICell cell, HeroForces income) {
        cell.addItem(income);
        cell.comeIn(income);
    }

    private HeroForces attack(HeroForces defender, HeroForces attacker) {
        int defenciveCount = defender.getCount();
        int attackCount = attacker.getCount();
        int delta = defenciveCount - attackCount;
        if (delta < 0) {
            ICell cell = defender.removeFromCell();
            attacker.decrease(Math.abs(delta));
            capture(cell, attacker);
            return attacker;
        } else if (delta == 0) {
            ICell cell = defender.removeFromCell();
            attacker.decrease(attackCount);
            return HeroForces.EMPTY;
        } else { // if (delta > 0)
            defender.decrease(Math.abs(delta));
            attacker.decrease(attackCount);
            return defender;
        }
    }

    @Override
    public void removeForces(Hero hero, int x, int y) {
        ICell cell = level.getCell(x, y);
        HeroForces forces = cell.getItem(HeroForces.class);
        if (forces != null && forces.itsMe(hero)) {
            forces.removeFromCell();
        }
    }

    @Override
    public int decreaseForces(Hero hero, int x, int y, int count) {
        ICell cell = level.getCell(x, y);
        if (cell.getItems(HeroForces.class).size() > 1) {
            throw new IllegalStateException("There are more than 1 heroes on cell!");
        }
        HeroForces forces = cell.getItem(HeroForces.class);
        if (forces != null && forces.itsMe(hero)) {
            return forces.decrease(count);
        } else {
            return 0;
        }
    }

    @Override
    public int countForces(Hero hero, int x, int y) {
        ICell cell = level.getCell(x, y);

        List<HeroForces> forces = cell.getItems(HeroForces.class);
        if (forces.isEmpty()) {
            return 0;
        } else if (forces.size() == 1) {
            HeroForces heroForces = forces.get(0);
            if (heroForces.itsMe(hero)) {
                return heroForces.getCount();
            } else {
                return 0;
            }
        } else {
            throw new IllegalStateException("There are more than 1 heroes on cell!");
        }
    }

    @Override
    public ICell getCell(int x, int y) {
        return level.getCell(x, y);
    }

    @Override
    public IItem getIfPresent(Class<? extends BaseItem> clazz, int x, int y) {
        for (IItem item : getCell(x, y).getItems()) {
            if (item.getClass().equals(clazz)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean isAt(int x, int y, Class<? extends BaseItem>... classes) {
        for (Class clazz : classes) {
            if (getIfPresent(clazz, x, y) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void reset() {
        for (Gold gold : level.getItems(Gold.class)) {
            gold.reset();
        }
    }

    public List<Hero> getHeroes() {
        List<Hero> result = new LinkedList();
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

    public void remove(Player player) {
        players.remove(player);
        player.destroyHero();
    }

    @Override
    public void removeFromCell(Hero hero) {
        for (HeroForces forces : level.getItems(HeroForces.class)) {
            if (forces.itsMe(hero)) {
                forces.removeFromCell();
            }
        }
        for (Start start : level.getItems(Start.class)) { // TODO test me
            if (start.isOwnedBy(hero)) {
                start.setOwner(null);
            }
        }
    }

    public ILevel getCurrentLevel() {
        return level;
    }

    public List<Player> getPlayers() {
        return new LinkedList(players);
    }

    public List<ILevel> getLevels() {
        return new LinkedList<>(levels);
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setLevel(ILevel level) {
        this.level = level;
    }

    public boolean isNotBusy() {
        return ((isMultiple && players.size() < 4) || (!isMultiple && players.size() == 0));
    }

    public void waitingOthers() {
        if (!isMultiple) return;
        waitingOthers = true;
    }

    @Override
    public String toString() {
        return JsonUtils.toStringSorted(new JSONObject(){{
            put("id", "E@" + Integer.toHexString(this.hashCode()));
            put("players", players.stream().map(p -> "Player@" + Integer.toHexString(p.hashCode())).collect(toList()));
            put("isMultiple", isMultiple);
            put("losers", losers.stream().map(p -> "Player@" + Integer.toHexString(p.hashCode())).collect(toList()));
            put("waitingOthers", waitingOthers);
            put("ticks", ticks);
            put("level", "L@" + Integer.toHexString(level.hashCode()));
        }});
    }
}
