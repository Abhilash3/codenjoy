package com.codenjoy.dojo.moebius.services;

import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;

public class Scores implements PlayerScores {

    private final Parameter<Integer> winScore;
    private final Parameter<Integer> loosePenalty;

    private volatile int score;

    public Scores(int startScore, Settings settings) {
        this.score = startScore;

        winScore = settings.addEditBox("Win score").type(Integer.class).def(1);
        loosePenalty = settings.addEditBox("Loose penalty").type(Integer.class).def(0);
    }

    @Override
    public int clear() {
        return score = 0;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void event(Object o) {
        Events events = (Events) o;
        switch (events.getType()) {
            case WIN:
                score += winScore.getValue()*events.getLines();
                break;
            case GAME_OVER:
                score -= loosePenalty.getValue();
                break;
        }
        score = Math.max(0, score);
    }
}
