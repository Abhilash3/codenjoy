package net.tetris.online.service;

import com.codenjoy.dojo.tetris.model.Figure;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: serhiy.zelenin
 * Date: 10/27/12
 * Time: 9:08 PM
 */
public class GameLogFile {
    private static Logger logger = LoggerFactory.getLogger(GameLogFile.class);

    private ServiceConfiguration configuration;
    private String playerName;
    private String timeStamp;
    private File playerLogsDir;
    private File logFile;
    private PrintWriter printWriter;
    private boolean openWrite;
    private BufferedReader reader;
    private boolean openRead;
    private String currentLine;
    private Pattern figurePattern = Pattern.compile("\\?figure=\\s*(\\w*)");
    private Pattern nextPattern = Pattern.compile("\\&next=\\s*(\\w*)");

    public GameLogFile(ServiceConfiguration configuration, String playerName, String timeStamp) {
        this.configuration = configuration;
        this.playerName = playerName;
        this.timeStamp = timeStamp;
        playerLogsDir = new File(configuration.getLogsDir(), playerName);
        logFile = new File(playerLogsDir, timeStamp);
    }

    private void openWrite() {
        playerLogsDir.mkdirs();

        try {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
            openWrite = true;
        } catch (IOException e) {
            logger.error("Unable to create game log for player " + playerName, e);
        }
    }

    public void close() {
        IOUtils.closeQuietly(printWriter);
        IOUtils.closeQuietly(reader);
    }

    public void log(String request, String response) {
        if (!openWrite) {
            openWrite();
        }
        printWriter.println(request + "@" + response);
        if (printWriter.checkError()) {
            logger.error("Unable to log record: '{}' for player {}", request + "@" + response, playerName);
        }
    }

    public boolean readNextStep() {
        if (!openRead) {
            try {
                openRead();
            } catch (FileNotFoundException e) {
                logger.warn("Requested game log file for player " + playerName + "does not exist", e);
                return false;
            }
        }
        try {
            currentLine = reader.readLine();
        } catch (IOException e) {
            logger.warn("Unable to read game log file:" + getPath() + " for player: " + playerName + ". Replay will stop silently.", e);
            currentLine = null;
            return false;
        }
        return currentLine != null;
    }

    private void openRead() throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(logFile));
        openRead = true;
    }

    public String getPath() {
        return logFile.getAbsolutePath();
    }

    public Figure.Type getCurrentFigure() {
        Matcher matcher = figurePattern.matcher(currentLine);
        if (matcher.find()) {
            try {
                return Figure.Type.valueOf(matcher.group(1));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    public String getCurrentResponse() {
        return currentLine.split("\\@")[1];
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<Figure.Type> getFutureFigures() {
        Matcher matcher = nextPattern.matcher(currentLine);
        if (matcher.find()) {
            try {
                String next = matcher.group(1);
                LinkedList<Figure.Type> nextTypes = new LinkedList<Figure.Type>();
                for (int i = 0; i < next.length(); i++) {
                    nextTypes.add(Figure.Type.valueOf("" + next.charAt(i)));
                }
                return nextTypes;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
