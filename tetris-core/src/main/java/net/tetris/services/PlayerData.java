package net.tetris.services;

import com.codenjoy.dojo.tetris.model.Plot;

import java.util.List;

public class PlayerData {
    PlayerData(List<Plot> plots, int score, int linesRemoved,
               String nextLevelIngoingCriteria, int level, String info) {
        this.plots = plots;
        this.score = score;
        this.linesRemoved = linesRemoved;
        this.nextLevelIngoingCriteria = nextLevelIngoingCriteria;
        this.level = level;
        this.info = info;
    }

    private List<Plot> plots;
    private int score;
    private int linesRemoved;
    private String nextLevelIngoingCriteria;
    private int level;
    private String info;

    public List<Plot> getPlots() {
        return plots;
    }

    public int getScore() {
        return score;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public String getNextLevelIngoingCriteria() {
        return nextLevelIngoingCriteria;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return String.format(
                "PlayerData[Plots:%s, " +
                "Score:%s, " +
                "LinesRemoved:%s, " +
                "NextLevelIngoingCriteria:'%s', " +
                "CurrentLevel:%s, " +
                "Info:'%s']",
                plots.toString(),
                score,
                linesRemoved,
                nextLevelIngoingCriteria,
                level,
                getInfo());
    }

    public String getInfo() {
        return (info == null)?"":info;
    }
}
