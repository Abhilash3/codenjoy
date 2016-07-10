package net.tetris.services;

import com.codenjoy.dojo.tetris.model.Plot;
import com.codenjoy.dojo.transport.GameState;
import com.codenjoy.dojo.transport.PlayerTransport;
import com.codenjoy.dojo.transport.ws.WebSocketPlayerTransport;
import com.codenjoy.dojo.tetris.model.Figure;
import net.tetris.dom.TetrisJoystik;

import java.io.IOException;
import java.util.List;

/**
 * User: serhiy.zelenin
 * Date: 4/8/13
 * Time: 11:04 PM
 */
public class WsPlayerController implements PlayerController {

    private PlayerTransport transport;

    @Override
    public void requestControl(Player player, Figure.Type type, int x, int y, TetrisJoystik joystick, List<Plot> plots, List<Figure.Type> futureFigures) throws IOException {
        GameState gameState = new TetrisGameState(plots, type, x, y, futureFigures, false);
        transport.sendState(player.getName(), gameState);
    }

    @Override
    public void registerPlayerTransport(Player player, TetrisJoystik joystick) {
        transport.registerPlayerEndpoint(player.getName(), new WsTetrisPlayerResponseHandler(player, joystick), null);
    }

    @Override
    public void unregisterPlayerTransport(Player player) {
        transport.unregisterPlayerEndpoint(player.getName());
    }

    public void setTransport(WebSocketPlayerTransport transport) {
        this.transport = transport;
    }

}
