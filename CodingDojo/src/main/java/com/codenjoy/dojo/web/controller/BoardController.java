package com.codenjoy.dojo.web.controller;

import com.codenjoy.dojo.services.chat.ChatService;
import com.codenjoy.dojo.services.Player;
import com.codenjoy.dojo.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class BoardController {
    public static final ArrayList<Object> EMPTY_LIST = new ArrayList<Object>();

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ChatService chatService;

    public BoardController() {
    }

    //for unit test
    BoardController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @RequestMapping(value = "/board/{playerName}",method = RequestMethod.GET)
    public String board(ModelMap model, HttpSession session, @PathVariable("playerName") String playerName) {
        Player player = playerService.findPlayer(playerName);
        if (player == null) {
            model.addAttribute("players", EMPTY_LIST);
        }else{
            model.addAttribute("players", Collections.singletonList(player));
            model.addAttribute("playerName", player.getName());
        }
        model.addAttribute("allPlayersScreen", false);

        setIsRegistered(model, session, playerName);

        gameSettings(model);
        return "board";
    }

    private void setIsRegistered(ModelMap model, HttpSession session, String playerName) {
        String registered = (String)session.getAttribute("playerName");
        boolean value = registered != null && registered.equals(playerName);
        model.addAttribute("registered", value);
    }

    @RequestMapping(value = "/board",method = RequestMethod.GET)
    public String boardAll(ModelMap model, HttpSession session) {
        String playerName = (String) session.getAttribute("playerName");
        if (playerService.isSingleBoardGame()) {
            if (playerName == null) {
                return "redirect:/register";
            }
            return "redirect:/board/" + playerName;
        }

        setIsRegistered(model, session, playerName);

        gameSettings(model);
        model.addAttribute("players", playerService.getPlayers());
        model.addAttribute("allPlayersScreen", true);
        return "board";
    }

    private void gameSettings(ModelMap model) {
        model.addAttribute("boardSize", playerService.getBoardSize());
        model.addAttribute("gameType", playerService.getGameType());
    }

    @RequestMapping(value = "/leaderboard",method = RequestMethod.GET)
    public String leaderBoard(ModelMap model) {
        List<Player> players = new ArrayList<Player>(playerService.getPlayers());
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player player1, Player player2) {
                return player2.getScore() - player1.getScore();
            }
        });

        model.addAttribute("players", players);
        return "leaderboard";
    }

    @RequestMapping(value = "/help")
    public String help() {
        return "help";
    }

    @RequestMapping(value = "/chat", method = RequestMethod.GET)
    public void chat(HttpSession session, @RequestParam("playerName") String name, @RequestParam("message") String message) {
        String playerName = (String) session.getAttribute("playerName");
        if (playerName == null || !playerName.equals(name)) return;
        chatService.chat(playerName, message);
    }
}
