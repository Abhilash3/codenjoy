package com.codenjoy.dojo.tetris.model;

import com.codenjoy.dojo.tetris.model.Levels;
import com.codenjoy.dojo.tetris.model.LevelsFactory;
import com.codenjoy.dojo.tetris.model.PlayerFigures;
import org.junit.Test;

import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class LevelsFactoryTest {

    private LevelsFactory factory = new LevelsFactory();

    @Test
    public void shouldReturnAllLevels(){
        Set<Class<? extends Levels>> levels = factory.getAllLevelsInPackage();

        assertThat(levels).contains(EasyLevels.class, HardLevels.class, AllFigureLevels.class);
    }

    @Test
    public void shouldLoadClass(){
        Levels loaded = factory.getGameLevels(mock(PlayerFigures.class), "HardLevels");
        assertEquals(HardLevels.class, loaded.getClass());
    }

}
