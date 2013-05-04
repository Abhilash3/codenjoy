package com.codenjoy.dojo.web.controller;

import com.codenjoy.dojo.services.NullPlayer;
import com.codenjoy.dojo.services.Player;
import com.codenjoy.dojo.services.PlayerServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * User: apofig
 * Date: 9/20/12
 * Time: 3:13 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class RegistrationControllerTest {

    private RegistrationController controller;
    @Mock
    private Model model;
    @Mock
    private PlayerServiceImpl playerService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private BindingResult bindingResult;

    private ArgumentCaptor<Player> players = ArgumentCaptor.forClass(Player.class);

    @Before
    public void setUp() throws Exception {
        controller = new RegistrationController(playerService);
    }

    @Test
    public void shouldEnterUserIPAtRegistration() {
        when(request.getRemoteAddr()).thenReturn("IP");
        when(playerService.findPlayerByIp("IP")).thenReturn(new NullPlayer());

        String jsp = controller.openRegistrationForm(request, model);
        assertEquals("register", jsp);

        verify(model).addAttribute(eq("player"), players.capture());
        assertEquals("http://IP:8888", players.getValue().getCallbackUrl());
    }

    @Test
    public void shouldForbiddenRegistrationWhenUserAlreadyRegistered() {
        when(request.getRemoteAddr()).thenReturn("IP");
        Player player = new Player("vasia", "URL", null, null);
        when(playerService.findPlayerByIp("IP")).thenReturn(player);

        String jsp = controller.openRegistrationForm(request, model);
        assertEquals("already_registered", jsp);

        verify(model).addAttribute("user", "vasia");
        verify(model).addAttribute("url", "URL");
    }


    @Test
    public void shouldRegisterWhenLocalhost() {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        Player player = new Player("vasia", "URL", null, null);
        when(playerService.findPlayerByIp("127.0.0.1")).thenReturn(player);

        String jsp = controller.openRegistrationForm(request, model);
        assertEquals("register", jsp);

        verify(model).addAttribute(eq("player"), players.capture());
        assertEquals("http://127.0.0.1:8888", players.getValue().getCallbackUrl());
    }

    @Test
    public void shouldChangeLocalIpToLocalhostAndRegister() {
        when(request.getRemoteAddr()).thenReturn("0:0:0:0:0:0:0:1");

        Player player = new Player("vasia", "URL", null, null);
        when(playerService.findPlayerByIp("127.0.0.1")).thenReturn(player);

        String jsp = controller.openRegistrationForm(request, model);
        assertEquals("register", jsp);

        verify(model).addAttribute(eq("player"), players.capture());
        assertEquals("http://127.0.0.1:8888", players.getValue().getCallbackUrl());
    }

    @Test
    public void shouldRemoveUserByIp() {
        when(request.getRemoteAddr()).thenReturn("IP");

        String jsp = controller.removeUserFromGame(request, model);
        assertEquals("redirect:/", jsp);

        verify(playerService).removePlayerByIp("IP");
    }

    @Test
    public void shouldUpdatePlayerWhenExistsOnSubmitRegistrationForm() {
        when(bindingResult.hasErrors()).thenReturn(false);

        when(playerService.alreadyRegistered("vasia")).thenReturn(true);

        Player player = new Player("vasia", "URL", null, null);
        String jsp = controller.submitRegistrationForm(player, bindingResult);
        assertEquals("redirect:/board/vasia", jsp);

        verify(playerService).updatePlayer(player);
    }

    @Test
    public void shouldCreatePlayerWhenNotExistsOnSubmitRegistrationForm() {
        when(bindingResult.hasErrors()).thenReturn(false);

        when(playerService.alreadyRegistered("vasia")).thenReturn(false);

        Player player = new Player("vasia", "URL", null, null);
        String jsp = controller.submitRegistrationForm(player, bindingResult);
        assertEquals("redirect:/board/vasia", jsp);

        verify(playerService).addNewPlayer("vasia", "URL");
    }

    @Test
    public void shouldStayOnRegistrationFormWhenError() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String jsp = controller.submitRegistrationForm(null, bindingResult);
        assertEquals("register", jsp);

        verifyNoMoreInteractions(playerService);
    }
}
