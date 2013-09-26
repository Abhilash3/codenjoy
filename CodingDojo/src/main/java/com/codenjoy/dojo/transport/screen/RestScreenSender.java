package com.codenjoy.dojo.transport.screen;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: serhiy.zelenin
 * Date: 9/26/13
 * Time: 2:04 PM
 */
@Component
public class RestScreenSender implements ScreenSender, AsyncListener {
    private List<UpdateRequest> requests = new ArrayList<UpdateRequest>();
    private Set<AsyncContext> timedOutRequests = Collections.synchronizedSet(new HashSet<AsyncContext>());

    private PlayerDataSerializer serializer;

    private ScheduledExecutorService restSenderExecutorService;

    private static Logger logger = LoggerFactory.getLogger(RestScreenSender.class);

    @Autowired
    public RestScreenSender(ScheduledExecutorService restSenderExecutorService,
                            PlayerDataSerializer serializer) {
        this.restSenderExecutorService = restSenderExecutorService;
        this.serializer = serializer;
    }

    public synchronized void scheduleUpdate(UpdateRequest updateRequest) {
        logger.debug("Scheduled screen update {}. Context: {}",
                updateRequest, updateRequest.getAsyncContext());
        Iterator<UpdateRequest> iterator = requests.iterator();
        while (iterator.hasNext()) {
            UpdateRequest request = iterator.next();
            if (timedOutRequests.contains(request.getAsyncContext())) {
                iterator.remove();
            }
        }
        requests.add(updateRequest);
    }


    @Override
    public synchronized void sendUpdates(final Map<Player, PlayerData> playerScreens) {
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();

        for (final UpdateRequest updateRequest : requests) {
            tasks.add(new PlayerScreenSendCallable(updateRequest, playerScreens));
        }
        logger.debug("Executing {} screen updates", tasks.size());
        try {
            restSenderExecutorService.invokeAll(tasks, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Interrupted while waiting for all glass data to be sent on client.", e);
        }
        requests.clear();
    }



    private void sendUpdateForRequest(Map<Player, PlayerData> playerScreens, UpdateRequest updateRequest) {
        AsyncContext asyncContext = updateRequest.getAsyncContext();
        ServletResponse response = asyncContext.getResponse();
        try {
            PrintWriter writer = response.getWriter();
            serializer.writeValue(writer, playerScreens);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            asyncContext.complete();
        }
    }

    @Override
    public void onComplete(AsyncEvent event) throws IOException {
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
        logger.debug("Context timed out: {}", event.getAsyncContext());
        timedOutRequests.add(event.getAsyncContext());
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
    }

    private class PlayerScreenSendCallable implements Callable<Void> {
        private final UpdateRequest updateRequest;
        private final Map<Player, PlayerData> playerData;

        public PlayerScreenSendCallable(UpdateRequest updateRequest, Map<Player, PlayerData> playerData) {
            this.updateRequest = updateRequest;
            this.playerData = playerData;
        }

        @Override
        public Void call() {
            Map<Player, PlayerData> playersToUpdate = findScreensFor(updateRequest, playerData);
            if (playersToUpdate.isEmpty()) {
                updateRequest.getAsyncContext().complete();
                return null;
            }
            sendUpdateForRequest(playersToUpdate, updateRequest);
            return null;
        }

        private Map<Player, PlayerData> findScreensFor(UpdateRequest updateRequest, Map<Player, PlayerData> playerData) {
            HashMap<Player, PlayerData> result = new HashMap<Player, PlayerData>();
            for (Map.Entry<Player, PlayerData> entry : playerData.entrySet()) {
                if (updateRequest.isApplicableFor(entry.getKey())) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
            return result;
        }

    }

}

