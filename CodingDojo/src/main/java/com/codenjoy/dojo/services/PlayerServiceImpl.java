package com.codenjoy.dojo.services;

import com.codenjoy.dojo.battlecity.services.BattlecityGame;
import com.codenjoy.dojo.bomberman.services.BombermanGame;
import com.codenjoy.dojo.loderunner.services.LoderunnerGame;
import com.codenjoy.dojo.minesweeper.services.MinesweeperGame;
import com.codenjoy.dojo.services.chat.ChatService;
import com.codenjoy.dojo.services.playerdata.PlayerData;
import com.codenjoy.dojo.services.settings.Settings;
import com.codenjoy.dojo.snake.services.SnakeGame;
import com.codenjoy.dojo.transport.screen.ScreenRecipient;
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

    @Autowired
    private com.codenjoy.dojo.transport.screen.ScreenSender<ScreenRecipient, PlayerData> screenSender;

    @Autowired
    private GameSaver saver;

    @Autowired
    private TimerService timerService;

    private List<Player> players = new ArrayList<Player>();
    private List<Game> games = new ArrayList<Game>();
    private List<PlayerController> controllers = new ArrayList<PlayerController>();

    private GameType gameType;
    private GuiPlotColorDecoder decoder;
    private ReadWriteLock lock;
    private boolean justStart = true;

    @Autowired
    private PlayerControllerFactory playerControllerFactory;
    private int count = 0;

    @Autowired
    private ChatService chatService;

    public PlayerServiceImpl() {
        lock = new ReentrantReadWriteLock(true);

        selectGame(LoderunnerGame.class.getSimpleName());
    }

    // for testing
    void setGameType(GameType gameType, GameSaver saver) {
        this.gameType = gameType;
        this.saver = saver;
        decoder = new GuiPlotColorDecoder(gameType.getPlots());
    }

    @Override
    public Player addNewPlayer(String name, String password, String callbackUrl) {
        lock.writeLock().lock();
        try {
            return register(new Player.PlayerBuilder(name, password, callbackUrl, getPlayersMinScore(), getProtocol().name()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void removePlayer(Player player) {
        int index = players.indexOf(player);
        if (index < 0) return;
        players.remove(index);
        Game game = games.remove(index);
        removeController(player, index);
        game.destroy();
    }

    private void removeController(Player player, int index) {
        PlayerController controller = controllers.remove(index);
        if (controller != null) {
            controller.unregisterPlayerTransport(player);
        }
    }

    private Player register(Player.PlayerBuilder playerBuilder) {
        Player player = playerBuilder.getPlayer(gameType);

        Player currentPlayer = getPlayer(player.getName());

        if (currentPlayer != null) {
            players.indexOf(currentPlayer);
            removePlayer(currentPlayer);
        }

        players.add(player);
        Game game = playerBuilder.getGame();
        games.add(game);

        createController(player, game);

        return player;
    }

    private void createController(Player player, Game game) {
        PlayerController controller = playerControllerFactory.get(player.getProtocol());
        controllers.add(controller);
        controller.registerPlayerTransport(player, game.getJoystick());
    }

    private int getPlayersMinScore() {
        int result = 0;
        for (Player player : players) {
            result = Math.min(player.getScore(), result);
        }
        return result;
    }

    @Override
    public void nextStepForAllGames() {
        lock.writeLock().lock();
        try {
            saveLoadAll(); // TODO автосохранялку может сделать не так топорно? SZ: +1

            if (games.size() == 0 || players.size() == 0) {
                return;
            }

            for (Game game : games) {
                if (game.isGameOver()) {
                    game.newGame();
                }
                game.tick();
            }

            HashMap<ScreenRecipient, PlayerData> map = new HashMap<ScreenRecipient, PlayerData>();

            String chatLog = chatService.getChatLog();
            int boardSize = gameType.getBoardSize().getValue();

            String scores = getScoresJSON();

            for (int i = 0; i < games.size(); i++) {
                Game game = games.get(i);
                Player player = players.get(i);

                // TODO передавать размер поля (и чат) не каждому плееру отдельно, а всем сразу
                map.put(player, new PlayerData(boardSize,
                        decoder.encode(game.getBoardAsString()),
                        player.getScore(),
                        game.getMaxScore(),
                        game.getCurrentScore(),
                        player.getCurrentLevel() + 1,
                        player.getMessage(),
                        chatLog,
                        scores));
            }

            screenSender.sendUpdates(map);

            for (int index = 0; index < players.size(); index++) {
                Player player = players.get(index);
                Game game = games.get(index);
                try {
                    String board = game.getBoardAsString().replace("\n", "");

                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Sent for player '%s' board \n%s", player, board));
                    }

                    controllers.get(index).requestControl(player, board);
                } catch (IOException e) {
                    logger.error("Unable to send control request to player " + player.getName() +
                            " URL: " + player.getCallbackUrl(), e);
                }
            }
        } catch (Error e) {
            e.printStackTrace();
            logger.error("nextStepForAllGames throws", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private String getScoresJSON() {
        JSONObject scores = new JSONObject();
        for (int i = 0; i < games.size(); i++) {
            Player player = players.get(i);
            Game game = games.get(i);

            scores.put(player.getName(), player.getScore());
        }
        return scores.toString();
    }

    private void saveLoadAll() {
        if (justStart) {
            justStart = false;
            loadAllGames();
        } else {
            count++;
            int saveOn = 30;
            if (count%saveOn == (saveOn - 1)) {
                saveAllGames();
            }
        }
    }

    @Override
    public String getGameType() {
        return gameType.gameName();
    }

    @Override
    public void saveAllGames() {
        lock.readLock().lock();
        try {
            for (Player player : players) {
                saver.saveGame(player);
            }
            saver.saveChat(chatService);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void loadAllGames() {
        lock.readLock().lock();
        try {
            for (String playerName : saver.getSavedList()) {
                loadGame(playerName);
            }
            saver.loadChat(chatService);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void loadGame(String playerName) {
        Player.PlayerBuilder builder = saver.loadGame(playerName);
        if (builder != null) {
            register(builder);
        }
    }

    @Override
    public void savePlayerGame(String name) {
        lock.readLock().lock();
        try {
            Player player = getPlayer(name);
            if (player != null) {
                saver.saveGame(player); 
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private Player getPlayer(String playerName) {
        for (Player player : players) {
            if (player.getName().equals(playerName)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public void loadPlayerGame(String name) {
        lock.writeLock().lock();
        try {
            loadGame(name);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Player> getPlayers() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(players);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void gameOverPlayerByName(String name) {
        lock.writeLock().lock();
        try {
            removePlayer(findPlayer(name));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updatePlayer(Player player) {
        lock.writeLock().lock();
        try {
            for (Player playerToUpdate : players) {
                if (playerToUpdate.getName().equals(player.getName())) {
                    playerToUpdate.setCallbackUrl(player.getCallbackUrl());
                    return;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void updatePlayers(List<PlayerInfo> players) {
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

            if (this.players.size() != players.size()) {
                throw new IllegalArgumentException("Diff players count");
            }

            for (int index = 0; index < players.size(); index ++) {
                Player playerToUpdate = this.players.get(index);
                Player newPlayer = players.get(index);

                playerToUpdate.setCallbackUrl(newPlayer.getCallbackUrl());
                playerToUpdate.setName(newPlayer.getName());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean alreadyRegistered(String playerName) {
        lock.readLock().lock();
        try {
            return findPlayer(playerName) != null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Player findPlayer(String playerName) {
        lock.readLock().lock();
        try {
            for (Player player : players) {
                if (player.getName().equals(playerName)) {
                    return player;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void removeAll() {
        lock.writeLock().lock();
        try {
            for (Player player : players.toArray(new Player[0])) {
                removePlayer(player);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    void clean() {
        players.clear();
        controllers.clear();
        games.clear();
    }

    private List<Game> getBoards() {
        return games;
    }

    @Override
    public Player findPlayerByIp(String ip) {
        lock.readLock().lock();
        try {
            for (Player player : players) {
                if (player.getCallbackUrl().contains(ip)) {
                    return player;
                }
            }
            return new NullPlayer();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void removePlayerByIp(String ip) {
        lock.writeLock().lock();
        try {
            int index = players.indexOf(findPlayerByIp(ip));
            if (index < 0) return;
            players.remove(index);
            games.remove(index);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public int getBoardSize() {
        return gameType.getBoardSize().getValue();
    }

    @Override
    public List<PlayerInfo> getPlayersGames() {
        List<PlayerInfo> result = new LinkedList<PlayerInfo>();
        for (Player player : players) {
            result.add(new PlayerInfo(player));
        }

        List<String> savedList = saver.getSavedList();  
        for (String name : savedList) {
            boolean notFound = true;
            for (PlayerInfo player : result) {
                if (name.equals(player.getName())) {  // TODO тут как-то был NPE
                    player.setSaved(true);
                    notFound = false;
                }
            }

            if (notFound) {
                result.add(new PlayerInfo(name, "", true));
            }
        }

        Collections.sort(result, new Comparator<PlayerInfo>() {
            @Override
            public int compare(PlayerInfo o1, PlayerInfo o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        return result;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.WS;
    }

    @Override
    public Joystick getJoystick(String playerName) {
        lock.writeLock().lock();
        try {
            int index = players.indexOf(findPlayer(playerName));
            if (index < 0) {
                return new NullJoystick();
            }
            return games.get(index).getJoystick();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean isSingleBoardGame() {
        return gameType.isSingleBoardGame();
    }

    @Override
    public void cleanAllScores() {   // TODO test me
        lock.writeLock().lock();
        try {
            for (Player player : players) {
                player.clearScore();
            }
            for (Game game : games) {
                game.newGame();
            }
            for (Game game : games) {
                game.clearScore();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Settings getGameSettings() {
        return gameType.getGameSettings();
    }

    @Override
    public void selectGame(String name) {   // TODO test me
        for (Class<? extends GameType> game : getCodenjoyGames()) {
            if (game.getSimpleName().equals(name)) {
                removeAll();
                try {
                    gameType = game.newInstance();
                    decoder = new GuiPlotColorDecoder(gameType.getPlots());
                    if (timerService != null) {
                        timerService.pause();
                    }
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
    }

    private List<Class<? extends GameType>> getCodenjoyGames() {
        return Arrays.asList(SnakeGame.class,
                BombermanGame.class,
                MinesweeperGame.class,
                BattlecityGame.class,
                LoderunnerGame.class);
    }

    @Override
    public List<String> getGames() {  // TODO test me
        List<String> result = new LinkedList<String>();
        for (Class<? extends GameType> game : getCodenjoyGames()) {
            result.add(game.getSimpleName());
        }
        return result;
    }

    @Override
    public void removePlayerSaveByName(String playerName) {
        saver.delete(playerName);
    }

    @Override
    public void removeAllPlayerSaves() {
        for (String playerName : saver.getSavedList()) {
            saver.delete(playerName);
        }
    }

    @Override
    public String getPlayerByCode(String code) {
        lock.writeLock().lock();
        try {
            if (code == null) return null;
            for (Player player : players) {
                if (player.getCode().equals(code)) {
                    return player.getName();
                }
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String getRandomPlayerName() {
        lock.writeLock().lock();
        try {
            if (players.size() == 0) return null;
            if (findPlayer("apofig") != null) return "apofig";
            if (findPlayer("admin") != null) return "admin";
            return players.iterator().next().getName();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean login(String name, String password) {
        lock.writeLock().lock();
        try {
            Player player = findPlayer(name);
            return (player != null && player.itsMe(password));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Map<String, List<String>> getSprites() {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        for (Class<? extends GameType> gameType : getCodenjoyGames()) {
            List<String> sprites = new LinkedList<String>();
            GameType game = loadGameType(gameType);

            for (Enum e : game.getPlots()) {
               sprites.add(e.name().toLowerCase());
            }

            result.put(game.gameName(), sprites);
        }
        return result;
    }

    private GameType loadGameType(Class<? extends GameType> gameType) {
        try {
            return gameType.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
