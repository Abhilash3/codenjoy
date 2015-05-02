package com.codenjoy.dojo.services.chat;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChatMessage {

    static DateCalendar calendar = new DateCalendar() {
        @Override
        public Date now() {
            return Calendar.getInstance().getTime();
        }
    };

    private Date time;
    private String playerName;
    private String message;

    public ChatMessage(String systemMessage) {
        this(calendar.now(), null, systemMessage);
    }

    public ChatMessage(Date time, String playerName, String message) {
        this.message = message;
        this.time = time;
        this.playerName = playerName;
    }

    public ChatMessage(String playerName, String message) {
        this(calendar.now(), playerName, message);
    }

    public boolean contains(String message) {
        return this.message.contains(message);
    }

    public boolean is(String playerName) {
        return this.playerName != null && this.playerName.equals(playerName);
    }

    @Override
    public String toString() {
        if (playerName == null) {
            return message;
        }

        return String.format("[%s] %s: %s", getDate(), playerName, message);
    }

    private String getDate() {
        if (time == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        int days = Days.daysBetween(new DateTime(time), new DateTime(calendar.now())).getDays();
        String date = sdf.format(time);
        if (days > 0) {
            date = days + " days ago, " + date;
        }
        return date;
    }

    public boolean isSystem() {
        return this.playerName == null;
    }

    public Date getTime() {
        return time;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getMessage() {
        return message;
    }

}
