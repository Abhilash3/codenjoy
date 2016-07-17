package net.tetris.services.levels;

import com.codenjoy.dojo.tetris.model.GlassEvent;
import net.tetris.dom.Levels;
import net.tetris.services.PlayerFigures;
import net.tetris.services.ProbabilityFigureTypesLevel;
import net.tetris.services.randomizer.ProbabilityRandomizer;
import net.tetris.services.randomizer.Randomizer;

import static com.codenjoy.dojo.tetris.model.Figure.Type.*;
import static com.codenjoy.dojo.tetris.model.GlassEvent.Type.WITHOUT_OVERFLOWN_LINES_REMOVED;


/**
 * User: oleksandr.baglai
 * Date: 9/23/12
 * Time: 3:18 PM
 */
public class ProbabilityWithoutOverflownLevels extends Levels {

    public static final int LINES_REMOVED_FOR_NEXT_LEVEL = 20;
    private static final GlassEvent.Type criteria = WITHOUT_OVERFLOWN_LINES_REMOVED;

    private int removedLinesWithoutOverflown;

    public ProbabilityWithoutOverflownLevels(PlayerFigures queue) {
        super(new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, 0),
                        lastFigureProbability(100),
                        O),
                // + I
                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(15),
                        O, I),

                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(50),
                        O, I),

                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(100),
                        O, I),

                // + J
                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(15),
                        O, I, J),


                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(50),
                        O, I, J),

                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(100),
                        O, I, J),

                // + L
                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(15),
                        O, I, J, L),


                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(50),
                        O, I, J, L),

                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(100),
                        O, I, J, L),

                // + S
                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(15),
                        O, I, J, L, S),

                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(50),
                        O, I, J, L, S),


                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(100),
                        O, I, J, L, S),

                // + Z
                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(15),
                        O, I, J, L, S, Z),

                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(50),
                        O, I, J, L, S, Z),

                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(100),
                        O, I, J, L, S, Z),

                // + T
                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(15),
                        O, I, J, L, S, Z, T),

                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(50),
                        O, I, J, L, S, Z, T),

                new ProbabilityFigureTypesLevel(queue,
                        new GlassEvent<>(criteria, LINES_REMOVED_FOR_NEXT_LEVEL),
                        lastFigureProbability(100),
                        O, I, J, L, S, Z, T));

        removedLinesWithoutOverflown = 0;
    }

    @Override
    public void glassOverflown() {
        removedLinesWithoutOverflown = 0;
        super.glassOverflown();
    }

    @Override
    public void linesRemoved(int amount) {
        removedLinesWithoutOverflown += amount;
        super.linesRemoved(amount);
    }

    @Override
    protected void onLevelChanged() {
        removedLinesWithoutOverflown = 0;
        super.onLevelChanged();
    }

    public GlassEvent nextLevelAcceptedCriteriaOnLinesRemovedEvent(int amount) {
        return new GlassEvent<>(GlassEvent.Type.WITHOUT_OVERFLOWN_LINES_REMOVED,
                removedLinesWithoutOverflown);
    }

    private static Randomizer lastFigureProbability(int i) {
        return new ProbabilityRandomizer(i);
    }

    public int getRemovedLinesWithoutOverflown() {
        return removedLinesWithoutOverflown;
    }
}
