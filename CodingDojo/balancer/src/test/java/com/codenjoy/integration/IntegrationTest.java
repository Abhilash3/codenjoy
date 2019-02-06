package com.codenjoy.integration;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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


import com.codenjoy.dojo.services.ConfigProperties;
import com.codenjoy.dojo.services.GameServers;
import com.codenjoy.dojo.services.TimerService;
import com.codenjoy.dojo.services.dao.GameServer;
import com.codenjoy.dojo.services.dao.Players;
import com.codenjoy.dojo.services.dao.Scores;
import com.codenjoy.dojo.services.entity.Player;
import com.codenjoy.dojo.services.hash.Hash;
import com.codenjoy.dojo.utils.JsonUtils;
import com.codenjoy.integration.mocker.SpringMockerJettyRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class IntegrationTest {

    private TimerService timer;
    private ConfigProperties config;
    private Players players;
    private Scores scores;
    private GameServers gameServers;
    private GameServer game;

    private SpringMockerJettyRunner runner;
    private String context;
    private int port;
    private RestTemplate rest;

    private String adminPassword;

    @BeforeTest
    public void setupJetty() throws Exception {
        runner = new SpringMockerJettyRunner("src/main/webapp", "/codenjoy-balancer"){{
            mockBean("gameServer");
            spyBean("gameServers");
            spyBean("configProperties");
            spyBean("players");
            spyBean("scores");
        }};

        port = runner.start(new Random().nextInt(1000) + 10000);

        rest = new RestTemplate();

        context = runner.getUrl();
        System.out.println(context);

        timer = runner.getBean(TimerService.class, "timerService");
        config = runner.getBean(ConfigProperties.class, "configProperties");
        players = runner.getBean(Players.class, "players");
        scores = runner.getBean(Scores.class, "scores");
        gameServers = runner.getBean(GameServers.class, "gameServers");
        game = runner.getBean(GameServer.class, "gameServer");
        timer.resume();

        adminPassword = Hash.md5(config.getAdminPassword());
    }

    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Target(java.lang.annotation.ElementType.METHOD)
    public @interface Clean {}

    @BeforeMethod
    public void before(Method method) {
        if (Arrays.stream(method.getDeclaredAnnotations())
                .filter(a -> a.annotationType().equals(Clean.class))
                .findAny()
                .isPresent())
        {
            players.removeAll();
            scores.removeAll();
            resetMocks();
        }
    }

    @AfterMethod
    public void after() {
        resetMocks();
    }

    @Clean
    @Test
    public void shouldRegister_whenNotPresent() {
        shouldCreateNewPlayerOnGame("12345678901234567890");

        assertPost("/rest/register",

                "{" +
                "  'email':'test@gmail.com'," +
                "  'firstName':'Stiven'," +
                "  'lastName':'Pupkin'," +
                "  'password':'13cf481db24b78c69ed39ab8663408c0'," +
                "  'code':'12345678901234567890'," +
                "  'city':'city'," +
                "  'skills':'Si Senior'," +
                "  'comment':'no comment'" +
                "}",

                "{\n" +
                "  'code':'12345678901234567890',\n" +
                "  'email':'test@gmail.com',\n" +
                "  'id':'btcfedopmomo66einibynbe',\n" +
                "  'server':'localhost:8080'\n" +
                "}");

        verify(game).createNewPlayer(
                "localhost:8080",
                "test@gmail.com",
                "Stiven Pupkin",
                "13cf481db24b78c69ed39ab8663408c0",
                "127.0.0.1",
                "0", // create new
                "{}"
        );

        verify(players, times(1)).create(any(Player.class));

        assertEquals(players.get("test@gmail.com").toString(),
                "Player{email='test@gmail.com', " +
                        "firstName='Stiven', " +
                        "lastName='Pupkin', " +
                        "password='13cf481db24b78c69ed39ab8663408c0', " +
                        "city='city', " +
                        "skills='Si Senior', " +
                        "comment='no comment', " +
                        "code='12345678901234567890', " +
                        "server='localhost:8080'}");
    }

    @Test(dependsOnMethods = "shouldRegister_whenNotPresent")
    public void shouldSameRegisterFailed_whenPresent() {
        shouldCreateNewPlayerOnGame("12345678901234567890");

        assertPost("/rest/register",

                "{" +
                "  'email':'test@gmail.com'," +
                "  'firstName':'Stiven'," +
                "  'lastName':'Pupkin'," +
                "  'password':'13cf481db24b78c69ed39ab8663408c0'," +
                "  'code':'12345678901234567890'," +
                "  'city':'city'," +
                "  'skills':'Si Senior'," +
                "  'comment':'no comment'" +
                "}",

                "IllegalArgumentException: User already registered");

        verify(players, never()).create(any(Player.class));

        assertEquals(players.get("test@gmail.com").toString(),
                "Player{email='test@gmail.com', " +
                        "firstName='Stiven', " +
                        "lastName='Pupkin', " +
                        "password='13cf481db24b78c69ed39ab8663408c0', " +
                        "city='city', " +
                        "skills='Si Senior', " +
                        "comment='no comment', " +
                        "code='12345678901234567890', " +
                        "server='localhost:8080'}");
    }

    @Clean
    @Test
    public void shouldRegisterError_whenGameServerIsNotResponding() {
        shouldThrowWhenCreateNewPlayerOnGame(new RuntimeException("Shit happens"));

        assertPost("/rest/register",

                "{" +
                "  'email':'test@gmail.com'," +
                "  'firstName':'Stiven'," +
                "  'lastName':'Pupkin'," +
                "  'password':'13cf481db24b78c69ed39ab8663408c0'," +
                "  'code':'12345678901234567890'," +
                "  'city':'city'," +
                "  'skills':'Si Senior'," +
                "  'comment':'no comment'" +
                "}",

                "RuntimeException: [At game server: RuntimeException: Shit happens]");

        verify(game).createNewPlayer(
                "localhost:8080",
                "test@gmail.com",
                "Stiven Pupkin",
                "13cf481db24b78c69ed39ab8663408c0",
                "127.0.0.1",
                "0", // create new
                "{}"
        );

        verify(players, never()).create(any(Player.class));

        assertEquals(players.get("test@gmail.com"),
                null);
    }

    @Clean
    @Test
    public void shouldRegisterValidationError_whenBadEmail() {
        assertPost("/rest/register",

                "{" +
                "  'email':'NOT_EMAIL'," +    // there is an error
                "  'firstName':'Stiven'," +
                "  'lastName':'Pupkin'," +
                "  'password':'13cf481db24b78c69ed39ab8663408c0'," +
                "  'code':'12345678901234567890'," +
                "  'city':'city'," +
                "  'skills':'Si Senior'," +
                "  'comment':'no comment'" +
                "}",

                "IllegalArgumentException: Player name is invalid: NOT_EMAIL");

        verifyNoMoreInteractions(game);
        verify(players, never()).create(any(Player.class));
        assertEquals(players.get("test@gmail.com"),
                null);
    }

    @Test(dependsOnMethods = "shouldRegister_whenNotPresent")
    public void shouldSuccessfulLogin_whenRegistered() {
        shouldCheckIfExistsOnGame(true);

        assertPost("/rest/login",

                "{" +
                "  'email':'test@gmail.com'," +
                "  'password':'13cf481db24b78c69ed39ab8663408c0'" +
                "}",

                "{\n" +
                "  'code':'12345678901234567890',\n" +
                "  'email':'test@gmail.com',\n" +
                "  'id':'btcfedopmomo66einibynbe',\n" +
                "  'server':'localhost:8080'\n" +
                "}");

        verify(game).existsOnServer("localhost:8080", "test@gmail.com");
    }

    @Test(dependsOnMethods = "shouldRegister_whenNotPresent")
    public void shouldExistOnGameServer_whenRegistered() {
        shouldCheckIfExistsOnGame(true);

        assertGet("/rest/player/test@gmail.com/active/12345678901234567890",
                "true");

        verify(game).existsOnServer("localhost:8080", "test@gmail.com");
    }

    @Test(dependsOnMethods = "shouldRegister_whenNotPresent")
    public void shouldExitFromGameServer_whenRegistered() {
        shouldExitFromGame(true);

        assertGet("/rest/player/test@gmail.com/exit/12345678901234567890",
                "true");

        verify(game).remove("localhost:8080", "test@gmail.com", "12345678901234567890");
    }

    @Test(dependsOnMethods = "shouldRegister_whenNotPresent")
    public void shouldJoinToGameServer_whenRegistered() {
        shouldCheckIfExistsOnGame(false);

        assertGet("/rest/player/test@gmail.com/join/12345678901234567890",
                "true");

        verify(game).existsOnServer("localhost:8080", "test@gmail.com");
        verify(game).createNewPlayer(
                "localhost:8080",
                "test@gmail.com",
                "Stiven Pupkin",
                "13cf481db24b78c69ed39ab8663408c0",
                "127.0.0.1",
                null, // try load from save
                null
        );
    }

    @Test(dependsOnMethods = "shouldJoinToGameServer_whenRegistered")
    public void shouldRemoveFromServer_whenRegistered() {
        shouldExitFromGame(true);

        assertGet("/rest/remove/test@gmail.com/" + adminPassword,
                "true");

        verify(game).remove("localhost:8080", "test@gmail.com", "12345678901234567890");
        verify(players).remove("test@gmail.com");
    }

    private void resetMocks() {
        verifyNoMoreInteractions(game);
        reset(game, players, scores, config, gameServers);
    }

    private void shouldCreateNewPlayerOnGame(String code) {
        doReturn(code).when(game).createNewPlayer(anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString());
    }

    private void shouldThrowWhenCreateNewPlayerOnGame(Exception exception) {
        doThrow(exception).when(game).createNewPlayer(anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString());
    }

    private void shouldCheckIfExistsOnGame(boolean exists) {
        doReturn(exists).when(game).existsOnServer(anyString(), anyString());
    }

    private void shouldExitFromGame(boolean exited) {
        doReturn(exited).when(game).remove(anyString(), anyString(), anyString());
    }

    private void assertPost(String url, String json, String answer) {
        try {
            String result = rest.postForObject(
                    context + url,
                    jsonRequest(json),
                    String.class);
            assertJson(result, answer);
        } catch (HttpStatusCodeException e) {
            assertEquals(answer, e.getResponseBodyAsString());
        }
    }

    private void assertGet(String url, String answer) {
        try {
            String result = rest.getForObject(
                    context + url,
                    String.class);

            assertJson(result, answer);
        } catch (HttpStatusCodeException e) {
            assertEquals(answer, e.getResponseBodyAsString());
        }
    }

    private void assertJson(String actual, String expected) {
        boolean isJson = actual.startsWith("{");
        assertEquals(isJson ? fix2(JsonUtils.prettyPrint(actual)) : actual,
                expected);
    }

    private HttpEntity jsonRequest(String json) {
        return new HttpEntity(fix(json), new HttpHeaders(){{
                setContentType(MediaType.APPLICATION_JSON);
            }});
    }

    private String fix(String json) {
        return json.replace("'", "\"");
    }

    private String fix2(String json) {
        return json.replace("\"", "'");
    }

}
