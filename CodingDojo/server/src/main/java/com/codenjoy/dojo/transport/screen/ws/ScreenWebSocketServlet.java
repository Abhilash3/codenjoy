package com.codenjoy.dojo.transport.screen.ws;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 Codenjoy
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


import com.codenjoy.dojo.transport.ApplicationContextListener;
import com.codenjoy.dojo.transport.auth.AuthenticationService;
import com.codenjoy.dojo.transport.auth.DefaultAuthenticationService;
import com.codenjoy.dojo.transport.ws.PlayerSocket;
import com.codenjoy.dojo.transport.ws.PlayerSocketCreator;
import com.codenjoy.dojo.transport.ws.PlayerTransport;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ScreenWebSocketServlet extends WebSocketServlet{

    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        PlayerSocketCreator creator =
                new PlayerSocketCreator(getPlayerTransport(),
                        getAuthenticationService(),
                        PlayerSocket.CLIENT_SEND_FIRST);
        webSocketServletFactory.setCreator(creator);
    }

    private PlayerTransport getPlayerTransport() {
        return ApplicationContextListener.getContext().getBean("screenPlayerTransport", PlayerTransport.class);
    }

    public AuthenticationService getAuthenticationService() {
        return ApplicationContextListener.getContext().getBean(DefaultAuthenticationService.class);
    }
}
