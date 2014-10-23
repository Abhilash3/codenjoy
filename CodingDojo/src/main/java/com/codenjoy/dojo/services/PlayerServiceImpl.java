package com.codenjoy.dojo.services;

import com.apofig.profiler.Profiler;
import com.codenjoy.dojo.services.chat.ChatService;
import com.codenjoy.dojo.services.playerdata.PlayerData;
import com.codenjoy.dojo.transport.screen.ScreenRecipient;
import com.codenjoy.dojo.transport.screen.ScreenSender;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * User: oleksandr.baglai
 * Date: 10/1/12
 * Time: 6:48 AM
 */
@Component("playerService")
public class PlayerServiceImpl implements PlayerService {
    private static Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private Map<Player, String> cacheBoards = new HashMap<Player, String>();
    private boolean registration = true;

    @Autowired private PlayerGames playerGames;
    @Autowired private ScreenSender<ScreenRecipient, PlayerData> screenSender;
    @Autowired private PlayerControllerFactory playerControllerFactory;
    @Autowired private GameService gameService;
    @Autowired private ChatService chatService;
    @Autowired private AutoSaver autoSaver;
//    @Autowired private ActionLogger actionLogger;

    @Override
    public Player register(String name, String password, String callbackUrl, String gameName) {
        lock.writeLock().lock();
        try {
            if (!registration) return Player.NULL;

            return register(new Player.PlayerBuilder(name, password, callbackUrl, gameName, 0, Protocol.WS.name()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Player register(Player.PlayerBuilder playerBuilder) {
        playerGames.remove(get(playerBuilder.getName()));

        Player player = playerBuilder.getPlayer(gameService);
        PlayerController controller = playerControllerFactory.get(player.getProtocol());
        Game game = playerBuilder.getGame();
        playerGames.add(player, game, controller);

        return player;
    }

    @Override
    public void tick() {
        lock.writeLock().lock();
        try {
//long time = Calendar.getInstance().getTime().getTime();
            autoSaver.tick();

            if (playerGames.isEmpty()) {
                return;
            }
            playerGames.tick();
            sendScreenUpdates();
            requestControls();
//System.out.println(Calendar.getInstance().getTime().getTime() - time + "ms");
//            actionLogger.log(playerGames);

        } catch (Error e) {
            e.printStackTrace();
            logger.error("nextStepForAllGames throws", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void requestControls() {
        for (PlayerGame playerGame : playerGames) {
            Player player = playerGame.getPlayer();
            PlayerController controller = playerGame.getController();

            try {
                String board = cacheBoards.get(player).replace("\n", "");

                controller.requestControl(player, board);
            } catch (IOException e) {
                logger.error("Unable to send control request to player " + player.getName() +
                        " URL: " + player.getCallbackUrl(), e);
            }
        }
    }

    private void sendScreenUpdates() {
        HashMap<ScreenRecipient, PlayerData> map = new HashMap<ScreenRecipient, PlayerData>();

        String chatLog = chatService.getChatLog();

        cacheBoards.clear();

        for (PlayerGame playerGame : playerGames) {
            Game game = playerGame.getGame();
            Player player = playerGame.getPlayer();

            GameType gameType = player.getGameType();    // TODO слишком много тут делается высокоуровневого
            int boardSize = gameType.getBoardSize().getValue();
            GuiPlotColorDecoder decoder = new GuiPlotColorDecoder(gameType.getPlots());
            String scores = getScoresJSON(gameType.gameName());
            String coordinates = getCoordinatesJSON(gameType.gameName());

            // TODO передавать размер поля (и чат) не каждому плееру отдельно, а всем сразу
            String boardAsString = game.getBoardAsString(); // TODO дольше всего строчка выполняется, прооптимизировать!
            String encoded = decoder.encode(boardAsString);
            cacheBoards.put(player, boardAsString);

            map.put(player, new PlayerData(boardSize,
                    encoded,
                    gameType.gameName(),  // TODO переименовать везде в gameType либо gameName
                    player.getScore(),
                    game.getMaxScore(),
                    game.getCurrentScore(),
                    player.getCurrentLevel() + 1,
                    player.getMessage(),
                    chatLog,
                    scores,
                    coordinates));
        }

        screenSender.sendUpdates(map);
//System.out.println("----" + PointImpl.counter);
//PointImpl.counter = 0;
    }

    private String getCoordinatesJSON(String gameType) {
        JSONObject result = new JSONObject();
        for (PlayerGame playerGame : playerGames.getAll(gameType)) {
            Player player = playerGame.getPlayer();
            Game game = playerGame.getGame();
            Point pt = game.getHero();
            result.put(player.getName(), map(pt));
        }
        return result.toString();
    }

    private Map<String, Integer> map(Point pt) {
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put("x", pt.getX());
        result.put("y", pt.getY());
        return result;
    }

    private String getScoresJSON(String gameType) {
        JSONObject scores = new JSONObject();
        for (PlayerGame playerGame : playerGames.getAll(gameType)) {
            Player player = playerGame.getPlayer();
            scores.put(player.getName(), player.getScore());
        }
        return scores.toString();
    }

    @Override
    public List<Player> getAll() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(playerGames.players());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Player> getAll(String gameName) {
        lock.writeLock().lock();
        try {
            return private_getAll(gameName);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private List<Player> private_getAll(String gameName) {
        List<Player> result = new LinkedList<Player>();
        for (PlayerGame playerGame : playerGames) {
            Player player = playerGame.getPlayer();
            if (player.getGameName().equals(gameName)) {
                result.add(player);
            }
        }
        return result;
    }

    @Override
    public void remove(String name) {
        lock.writeLock().lock();
        try {
            playerGames.remove(get(name));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updateAll(List<PlayerInfo> players) {
        lock.writeLock().lock();
        try {
            if (players == null) {
                return;
            }
            Iterator<PlayerInfo> iterator = players.iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                if (player.getName() == null) {
                    iterator.remove();
                }
            }

            if (playerGames.size() != players.size()) {
                throw new IllegalArgumentException("Diff players count");
            }

            for (int index = 0; index < playerGames.size(); index ++) {
                Player playerToUpdate = playerGames.players().get(index);
                Player newPlayer = players.get(index);

                playerToUpdate.setCallbackUrl(newPlayer.getCallbackUrl());
                playerToUpdate.setName(newPlayer.getName());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean contains(String name) {
        lock.readLock().lock();
        try {
            return get(name) != Player.NULL;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Player get(String name) {
        lock.readLock().lock();
        try {
            return playerGames.get(name).getPlayer();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void removeAll() {
        lock.writeLock().lock();
        try {
            playerGames.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Joystick getJoystick(String name) {
        lock.writeLock().lock();
        try {
            return playerGames.get(name).getGame().getJoystick();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void closeRegistration() {
        registration = false;
    }

    @Override
    public boolean isRegistrationOpened() {
        return registration;
    }

    @Override
    public void openRegistration() {
        registration = true;
    }

    @Override
    public void cleanAllScores() {
        lock.writeLock().lock();
        try {
            for (PlayerGame playerGame : playerGames) {
                Game game = playerGame.getGame();
                Player player = playerGame.getPlayer();

                player.clearScore();

                game.newGame();
                game.clearScore();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Player getByCode(String code) {
        lock.readLock().lock();
        try {
            if (code == null) return Player.NULL;
            for (Player player : playerGames.players()) {
                if (player.getCode().equals(code)) {
                    return player;
                }
            }
            return Player.NULL;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Player getRandom(String gameType) {
        lock.readLock().lock();
        try {
            if (playerGames.isEmpty()) return Player.NULL;

            if (gameType == null) {
                return playerGames.iterator().next().getPlayer();
            }

            Iterator<Player> iterator = private_getAll(gameType).iterator();
            if (!iterator.hasNext()) return Player.NULL;
            return iterator.next();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public GameType getAnyGameWithPlayers() {
        lock.readLock().lock();
        try {
            if (playerGames.isEmpty()) return GameType.NULL;

            return playerGames.iterator().next().getPlayer().getGameType();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean login(String name, String password) {
        lock.readLock().lock();
        try {
            return get(name).itsMe(password);
        } finally {
            lock.readLock().unlock();
        }
    }


}
