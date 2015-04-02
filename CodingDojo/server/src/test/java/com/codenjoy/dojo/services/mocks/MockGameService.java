package com.codenjoy.dojo.services.mocks;

import com.codenjoy.dojo.services.GameService;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * User: sanja
 * Date: 17.12.13
 * Time: 18:53
 */
public class MockGameService {
    @Bean(name = "gameService")
    public GameService gameService() throws Exception {
        return mock(GameService.class);
    }
}
