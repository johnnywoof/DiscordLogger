package me.johnnywoof.discordlogger.generic;

import me.johnnywoof.discordlogger.util.ConfigSettings;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public interface NativeEnvironment {

    void log(Level level, String message);

    void saveDefaultConfig();

    ConfigSettings getDiscordLoggerConfig();

    void runAsync(Runnable runnable);

    void runAsyncTimer(Runnable runnable, long delay, TimeUnit timeUnit);

    void hookLogStreams() throws Exception;

    void unhookLogStreams() throws Exception;

    void flushLogHook();

    default List<Object[]> logToEmbedList(String str) {
        List<Object[]> objList = new LinkedList<>();

        Map<String, String> entryMap = new HashMap<>();
        entryMap.put("Logged Message: ", str);

        objList.add(new Object[]{entryMap, true});
        return objList;
    }

}
