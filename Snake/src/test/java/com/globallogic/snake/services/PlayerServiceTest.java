package com.globallogic.snake.services;
import com.globallogic.snake.model.Board;
import com.globallogic.snake.model.Joystick;
import com.globallogic.snake.model.Snake;
import com.globallogic.snake.model.artifacts.Apple;
import com.globallogic.snake.model.artifacts.ArtifactGenerator;
import com.globallogic.snake.model.artifacts.Stone;
import com.globallogic.snake.services.playerdata.PlayerData;
import com.globallogic.snake.services.playerdata.Plot;
import com.globallogic.snake.services.playerdata.PlotColor;
import com.globallogic.snake.services.playerdata.PlotsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.globallogic.snake.model.TestUtils.assertContainsPlot;
import static junit.framework.Assert.*;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {PlayerService.class,
        MockScreenSenderConfiguration.class,
        MockPlayerController.class,
        MockArtifactGenerator.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class PlayerServiceTest {

    private ArgumentCaptor<Map> screenSendCaptor;
    private ArgumentCaptor<Player> playerCaptor;
    private ArgumentCaptor<Integer> xCaptor;
    private ArgumentCaptor<Integer> yCaptor;
    private ArgumentCaptor<List> plotsCaptor;
    private ArgumentCaptor<Board> boardCaptor;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ScreenSender screenSender;

    @Autowired
    private PlayerController playerController;

    @Autowired
    private ArtifactGenerator artifactGenerator;

    @Before
    @SuppressWarnings("all")
    public void setUp() throws IOException {
        screenSendCaptor = ArgumentCaptor.forClass(Map.class);
        playerCaptor = ArgumentCaptor.forClass(Player.class);
        xCaptor = ArgumentCaptor.forClass(Integer.class);
        yCaptor = ArgumentCaptor.forClass(Integer.class);
        plotsCaptor = ArgumentCaptor.forClass(List.class);
        boardCaptor = ArgumentCaptor.forClass(Board.class);

        playerService.clear();
        Mockito.reset(playerController, screenSender, artifactGenerator);
        setupArtifacts();
    }

    @Test
    public void shouldSendCoordinatesToPlayerBoard() throws IOException {
        Player vasya = createPlayer("vasya");

        playerService.nextStepForAllGames();

        assertSentToPlayers(vasya);
        List<Plot> plots = getPlotsFor(vasya);
        assertContainsPlot(2, 3, PlotColor.STONE, plots);
        assertContainsPlot(1, 2, PlotColor.APPLE, plots);
        assertContainsPlot(8, 7, PlotColor.HEAD, plots);
        assertContainsPlot(7, 7, PlotColor.TAIL, plots);
    }

    @Test
    public void shouldRequestControlFromAllPlayers() throws IOException {
        Player vasya = createPlayer("vasya");
        Player petya = createPlayer("petya");

        playerService.nextStepForAllGames();

        assertSentToPlayers(vasya, petya);
        verify(playerController, times(2)).requestControl(playerCaptor.capture(), Matchers.<Joystick>any(), boardCaptor.capture());

        assertHostsCaptured("http://vasya:1234", "http://petya:1234");
    }

    @Test
    public void shouldRequestControlFromAllPlayersWithGlassState() throws IOException {
        createPlayer("vasya");

        playerService.nextStepForAllGames();

        verify(playerController).requestControl(playerCaptor.capture(), Matchers.<Joystick>any(), boardCaptor.capture());
        Board board = boardCaptor.getValue();
        List<Plot> sentPlots = new PlotsBuilder(board).get();
        assertEquals(4, sentPlots.size());
        assertContainsPlot(2, 3, PlotColor.STONE, sentPlots);
        assertContainsPlot(1, 2, PlotColor.APPLE, sentPlots);
        assertContainsPlot(8, 7, PlotColor.HEAD, sentPlots);
        assertContainsPlot(7, 7, PlotColor.TAIL, sentPlots);
    }

    private void setupArtifacts() {
        when(artifactGenerator.generateApple(any(Snake.class), any(Stone.class), anyInt())).thenReturn(new Apple(1, 2));
        when(artifactGenerator.generateStone(any(Snake.class), any(Apple.class), anyInt())).thenReturn(new Stone(2, 3));
    }

    @Test
    public void shouldSendAdditionalInfoToAllPlayers() throws IOException {
        Player vasya = createPlayer("vasya");
        Player petya = createPlayer("petya");

        playerService.nextStepForAllGames();

        verify(screenSender).sendUpdates(screenSendCaptor.capture());
        Map<Player, PlayerData> data = screenSendCaptor.getValue();

        Map<String, String> expected = new HashMap<>();
        expected.put("vasya", "PlayerData[BoardSize:15, Plots:[Plot{x=1, y=2, color=APPLE}, Plot{x=2, y=3, color=STONE}, " +
                "Plot{x=7, y=7, color=TAIL}, Plot{x=8, y=7, color=HEAD}], Score:0, CurrentLevel:1]");

        expected.put("petya", "PlayerData[BoardSize:15, Plots:[Plot{x=1, y=2, color=APPLE}, Plot{x=2, y=3, color=STONE}, " +
                "Plot{x=7, y=7, color=TAIL}, Plot{x=8, y=7, color=HEAD}], Score:0, CurrentLevel:1]");

        assertEquals(2, data.size());

        for (Map.Entry<Player, PlayerData> entry : data.entrySet()) {
            assertEquals(expected.get(entry.getKey().getName()), entry.getValue().toString());
        }
    }

    @Test
    public void shouldNewUserHasZerroScoresWhenLastLoggedIfOtherPlayerHasPositiveScores() {
        // given
        Player vasya = createPlayer("vasya");
        forceAllPlayerSnakesEatApple(); // vasia +10

        // when
        Player petya = createPlayer("petya");

        // then
        assertEquals(0, petya.getScore());
    }

    @Test
    public void shouldNewUserHasMinimumPlayersScoresWhenLastLoggedIfSomePlayersHasNegativeScores() {
        // given
        Player vasya = createPlayer("vasya");
        forceAllPlayerSnakesEatApple(); // vasia +10
        Player petya = createPlayer("petya");

        // when
        forceKillAllPlayerSnakes(); // vasia & petia -500
        Player katya = createPlayer("katya");

        // then
        assertEquals(petya.getScore(), katya.getScore());
        assertEquals(PlayerScores.GAME_OVER_PENALTY, petya.getScore());
    }

    @Test
    public void shouldNewUserHasMinimumPlayersScoresWhenLastLoggedAfterNextStep() {
        // given
        Player vasya = createPlayer("vasya");
        forceAllPlayerSnakesEatApple(); // vasia +10
        Player petya = createPlayer("petya");
        forceKillAllPlayerSnakes(); // vasia & petia -500
        Player katya = createPlayer("katya");

        // when
        playerService.nextStepForAllGames();
        Player olia = createPlayer("olia");

        // then
        assertEquals(olia.getScore(), katya.getScore());
        assertEquals(olia.getScore(), petya.getScore());
    }

    @Test
    public void shouldRemoveAllPlayerDataWhenRemovePlayer() {
        // given
        createPlayer("vasya");
        createPlayer("petya");

        // when
        playerService.removePlayer("http://vasya:1234");

        //then
        assertNull(playerService.findPlayer("vasya"));
        assertNotNull(playerService.findPlayer("petya"));
        assertEquals(1, playerService.getBoards().size());
    }

    @Test
    public void shouldFindPlayerWhenGetByIp() {
        // given
        Player newPlayer = createPlayer("vasya_ip");

        // when
        Player player = playerService.findPlayerByIp("vasya_ip");

        //then
        assertSame(newPlayer, player);
    }

    @Test
    public void shouldGetNullPlayerWhenGetByNotExistsIp() {
        // given
        createPlayer("vasya_ip");

        // when
        Player player = playerService.findPlayerByIp("kolia_ip");

        //then
        assertEquals(NullPlayer.class, player.getClass());
    }

    private void forceKillAllPlayerSnakes() {
        for (Board board : playerService.getBoards()) {
            board.getSnake().killMe();
        }
    }

    private Player createPlayer(String userName) {
        return playerService.addNewPlayer(userName, "http://" + userName + ":1234");
    }


    private List<Plot> getPlotsFor(Player vasya) {
        Map<Player, PlayerData> value = screenSendCaptor.getValue();
        return value.get(vasya).getPlots();
    }

    private void assertSentToPlayers(Player ... players) {
        verify(screenSender).sendUpdates(screenSendCaptor.capture());
        Map sentScreens = screenSendCaptor.getValue();
        assertEquals(players.length, sentScreens.size());
        for (Player player : players) {
            assertTrue(sentScreens.containsKey(player));
        }
    }

    private void forceAllPlayerSnakesEatApple() {
        for (Board board : playerService.getBoards()) {
            board.getSnake().grow();
        }
    }

    private void assertHostsCaptured(String ... hostUrls) {
        assertEquals(hostUrls.length, playerCaptor.getAllValues().size());
        for (int i = 0; i < hostUrls.length; i++) {
            String hostUrl = hostUrls[i];
            assertEquals(hostUrl, playerCaptor.getAllValues().get(i).getCallbackUrl());
        }
    }

}
