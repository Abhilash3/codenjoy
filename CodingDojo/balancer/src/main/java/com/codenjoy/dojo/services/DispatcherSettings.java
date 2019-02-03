package com.codenjoy.dojo.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DispatcherSettings {

    @Autowired ConfigProperties config;
    @Autowired GameServers gameServers;

    private String urlCreatePlayer;
    private String urlRemovePlayer;
    private String urlGetPlayers;
    private String urlClearScores;
    private String urlExistsPlayer;
    private String urlGameEnabled;
    private String gameType;
    private List<String> servers;

    public DispatcherSettings() {
        // do nothing
    }

    public DispatcherSettings(String urlCreatePlayer, String urlRemovePlayer,
                              String urlGetPlayers, String urlClearScores,
                              String urlExistsPlayer, String urlGameEnabled,
                              String gameType, List<String> servers)
    {
        this.urlCreatePlayer = urlCreatePlayer;
        this.urlRemovePlayer = urlRemovePlayer;
        this.urlGetPlayers = urlGetPlayers;
        this.urlClearScores = urlClearScores;
        this.urlExistsPlayer = urlExistsPlayer;
        this.urlGameEnabled = urlGameEnabled;
        this.gameType = gameType;
        this.servers = servers;
    }

    @PostConstruct
    public void postConstruct() {
        urlCreatePlayer = config.getUrlCreatePlayer();
        urlRemovePlayer = config.getUrlRemovePlayer();
        urlGetPlayers = config.getUrlGetPlayers();
        urlClearScores = config.getUrlClearScores();
        urlExistsPlayer = config.getUrlExistsPlayer();
        urlGameEnabled = config.getUrlGameEnabled();
        gameType = config.getGameType();
        servers = config.getServers();

        updateGameServers();
    }

    private void updateGameServers() {
        if (servers != null) {
            gameServers.update(servers);
        }
    }

    public void updateFrom(DispatcherSettings settings) {
        urlRemovePlayer = settings.urlRemovePlayer;
        urlCreatePlayer = settings.urlCreatePlayer;
        urlGetPlayers = settings.urlGetPlayers;
        urlClearScores = settings.urlClearScores;
        urlExistsPlayer = settings.urlExistsPlayer;
        urlGameEnabled = settings.urlGameEnabled;
        gameType = settings.gameType;
        servers = settings.servers;

        updateGameServers();
    }

    public String getUrlCreatePlayer() {
        return urlCreatePlayer;
    }

    public String getUrlRemovePlayer() {
        return urlRemovePlayer;
    }

    public String getUrlGetPlayers() {
        return urlGetPlayers;
    }

    public String getUrlClearScores() {
        return urlClearScores;
    }

    public String getUrlExistsPlayer() {
        return urlExistsPlayer;
    }

    public String getUrlGameEnabled() {
        return urlGameEnabled;
    }

    public String getGameType() {
        return gameType;
    }

    public List<String> getServers() {
        return servers;
    }

    @Override
    public String toString() {
        return "DispatcherSettings{" +
                "urlCreatePlayer='" + urlCreatePlayer + '\'' +
                ", urlRemovePlayer='" + urlRemovePlayer + '\'' +
                ", urlGetPlayers='" + urlGetPlayers + '\'' +
                ", urlClearScores='" + urlClearScores + '\'' +
                ", urlExistsPlayer='" + urlExistsPlayer + '\'' +
                ", urlGameEnabled='" + urlGameEnabled + '\'' +
                ", gameType='" + gameType + '\'' +
                ", servers=" + servers +
                '}';
    }
}
