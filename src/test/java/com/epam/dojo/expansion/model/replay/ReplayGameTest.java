package com.epam.dojo.expansion.model.replay;

import com.codenjoy.dojo.services.NullJoystick;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static org.junit.Assert.assertEquals;

/**
 * Created by Oleksandr_Baglai on 2017-09-22.
 */
public class ReplayGameTest {

    @Test
    public void testIsReplayGame() {
        assertEquals(true, ReplayGame.isReplay("{'startFromTick':0,'replayName':'game-E@1e16c0aa-1','playerName':'P@57bc27f5'}"));

        assertEquals(false, ReplayGame.isReplay("{'total':10,'current':0,'lastPassed':9,'multiple':true}"));
        assertEquals(false, ReplayGame.isReplay(""));
        assertEquals(false, ReplayGame.isReplay(null));
    }

    @Test
    public void shouldCreateLoggerReader() {
        final String[] actualReplayName = {null};
        final String[] actualPlayerName = {null};

        ReplayGame game = new ReplayGame(new JSONObject("{'startFromTick':0,'replayName':'game-E@1e16c0aa-1','playerName':'P@57bc27f5'}")){
            @Override
            protected LoggerReader getLoggerReader(String replayName, String playerName) {
                actualReplayName[0] = replayName;
                actualPlayerName[0] = playerName;
                return null;
            }
        };
        assertEquals("game-E@1e16c0aa-1", actualReplayName[0]);
        assertEquals("P@57bc27f5", actualPlayerName[0]);
    }

    @Test
    public void shouldNullJoystick() {
        ReplayGame game = new ReplayGame(new JSONObject("{'startFromTick':0,'replayName':'game-E@1e16c0aa-1','playerName':'P@57bc27f5'}"));
        assertEquals(NullJoystick.INSTANCE, game.getJoystick());
    }

    @Test
    public void shouldIsGameOverFalse() {
        ReplayGame game = new ReplayGame(new JSONObject("{'startFromTick':0,'replayName':'game-E@1e16c0aa-1','playerName':'P@57bc27f5'}"));
        assertEquals(false, game.isGameOver());
    }


    private JSONObject lobbyBoard;
    private JSONObject lobbyCurrentAction;
    private List<JSONObject> lobbyOtherCurrentActions;
    private Point lobbyPt;
    private List<JSONObject> currentActions;
    private List<List<JSONObject>> otherCurrentActions;
    private List<Point> basePositions;
    private List<JSONObject> boards;

    @Before
    public void setup() {
        currentActions = new LinkedList<>();
        otherCurrentActions = new LinkedList<>();
        basePositions = new LinkedList<>();
        boards = new LinkedList<>();

        lobbyBoard = new JSONObject("{'lb':0}");
        lobbyCurrentAction = new JSONObject("{'lca':0}");
        lobbyOtherCurrentActions = Arrays.asList(
                new JSONObject("{'loca':10}"),
                new JSONObject("{'loca':20}"));
        lobbyPt = pt(-1, -1);

        // It is important that there are different objects, but not their contents
        for (int i = 0; i < 4; i++) {
            currentActions.add(new JSONObject("{'ca':" + i + "}"));
            otherCurrentActions.add(Arrays.asList(
                    new JSONObject("{'oca':" + (10 + i) + "}"),
                    new JSONObject("{'oca':" + (20 + i) + "}")));
            basePositions.add(pt(i, i));
            boards.add(new JSONObject("{'b':" + i + "}"));
        }
    }

    @Test
    public void shouldProcessAllData() {
        // given
        ReplayGame game = createGame(0);

        // replay not started, tick=-1 and we are at "replays lobby"
        assertAtLobby(game);

        // new game, tick = -1, because need clearScore()
        game.newGame();
        assertAtLobby(game);

        // clearScore, tick = 0
        game.clearScore();
        assertTick(game, 0);

        // tick++ = 1
        game.tick();
        assertTick(game, 1);

        // tick++ = 2
        game.tick();
        assertTick(game, 2);

        // tick++ = 3
        game.tick();
        assertTick(game, 3);

        // tick++ is out of, so we go to lobby
        game.tick();
        assertAtLobby(game);

        // then restart again
        // clearScore, tick = 0
        game.clearScore();
        assertTick(game, 0);

        // tick++ = 1
        game.tick();
        assertTick(game, 1);

        // tick++ = 2
        game.tick();
        assertTick(game, 2);

        // tick++ = 3
        game.tick();
        assertTick(game, 3);

        // tick++ is out of, so we go to lobby
        game.tick();
        assertAtLobby(game);
    }

    @Test
    public void shouldProcessAllData_startFromOther() {
        // given
        ReplayGame game = createGame(2);

        // replay not started, tick=-1 and we are at "replays lobby"
        assertAtLobby(game);

        // new game, tick = -1, because need clearScore()
        game.newGame();
        assertAtLobby(game);

        // clearScore, tick = 2
        game.clearScore();
        assertTick(game, 2);

        // tick++ = 3
        game.tick();
        assertTick(game, 3);

        // tick++ is out of, so we go to lobby
        game.tick();
        assertAtLobby(game);

        // then restart again
        // clearScore, tick = 2
        game.clearScore();
        assertTick(game, 2);

        // tick++ = 3
        game.tick();
        assertTick(game, 3);

        // tick++ is out of, so we go to lobby
        game.tick();
        assertAtLobby(game);
    }

    @Test
    public void shouldNotTickWhenNotStart() {
        // given
        ReplayGame game = createGame(0);

        // replay not started, tick=-1 and we are at "replays lobby"
        assertAtLobby(game);

        // new game, tick = -1, because need clearScore()
        game.tick();
        assertAtLobby(game);
    }

    @Test
    public void shouldNotTickWhenNoMoreTicks() {
        // given
        ReplayGame game = createGame(3);

        // replay not started, tick=-1 and we are at "replays lobby"
        assertAtLobby(game);

        // new game, tick = -1, because need clearScore()
        game.newGame();
        assertAtLobby(game);

        // clearScore, tick = 3
        game.clearScore();
        assertTick(game, 3);

        // tick++ is out of, so we go to lobby
        game.tick();
        assertAtLobby(game);

        // tick++ is out of, so we go to lobby
        game.tick();
        assertAtLobby(game);
    }

    @NotNull
    private ReplayGame createGame(int startFrom) {
        return new ReplayGame(new JSONObject("{'startFromTick':" + startFrom + ",'replayName':'game-E@1e16c0aa-1','playerName':'P@57bc27f5'}")){
                @Override
                protected LoggerReader getLoggerReader(String replayName, String playerName) {
                    return new LoggerReader() {
                        private boolean isOutOf(int tick) {
                            return tick < 0 || tick >= boards.size();
                        }

                        @Override
                        public JSONObject getCurrentAction(int tick) {
                            if (isOutOf(tick)) {
                                return lobbyCurrentAction;
                            }
                            return currentActions.get(tick);
                        }

                        @Override
                        public List<JSONObject> getOtherCurrentActions(int tick) {
                            if (isOutOf(tick)) {
                                return lobbyOtherCurrentActions;
                            }
                            return otherCurrentActions.get(tick);
                        }

                        @Override
                        public Point getBasePosition(int tick) {
                            if (isOutOf(tick)) {
                                return lobbyPt;
                            }
                            return basePositions.get(tick);
                        }

                        @Override
                        public JSONObject getBoard(int tick) {
                            if (isOutOf(tick)) {
                                return lobbyBoard;
                            }
                            return boards.get(tick);
                        }

                        @Override
                        public int size() {
                            return boards.size();
                        }
                    };
                }
            };
    }

    private void assertTick(ReplayGame game, int tick) {
        assertEquals(false, game.noMoreTicks());

        assertEquals("{'b':" + tick + "}",
                JsonUtils.cleanSorted(game.getBoardAsString()));

        assertEquals("{'lastAction':{'ca':" + tick + "},'otherLastActions':[{'oca':1" + tick + "},{'oca':2" + tick + "}]}",
                JsonUtils.cleanSorted(game.getHero().getAdditionalData()));

        assertEquals(pt(tick, tick).toString(),
                game.getHero().getCoordinate().toString());
    }

    private void assertAtLobby(ReplayGame game) {
        assertEquals(true, game.noMoreTicks());

        assertEquals("{'lb':0}",
                JsonUtils.cleanSorted(game.getBoardAsString()));

        assertEquals("{'lastAction':{'lca':0},'otherLastActions':[{'loca':10},{'loca':20}]}",
                JsonUtils.cleanSorted(game.getHero().getAdditionalData()));

        assertEquals("[-1,-1]",
                game.getHero().getCoordinate().toString());
    }
}


