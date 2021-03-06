package me.johnnywoof.discordlogger;

import com.google.common.hash.Hashing;
import me.johnnywoof.discordlogger.formatting.WebhookBuilder;
import me.johnnywoof.discordlogger.generic.NativeEnvironment;
import me.johnnywoof.discordlogger.util.ConfigSettings;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DiscordLogger {

    private static final List<DiscordLogger> INSTANCE_LIST = new CopyOnWriteArrayList<>();
    public static final int MAX_DISCORD_CHARACTERS = 2000;
    private final NativeEnvironment nativeEnvironment;
    private final Map<Integer, Long> recentMessages = new HashMap<>();
    private ConfigSettings settings;

    public DiscordLogger(NativeEnvironment nativeEnvironment) {
        this.nativeEnvironment = nativeEnvironment;
        INSTANCE_LIST.add(this);
    }

    public static void postPluginMessage(final WebhookBuilder builder) {
        //In theory there should only be one instance because there's only one plugin per server but w/e
        for (DiscordLogger logger : INSTANCE_LIST) {
            logger.postMessage(builder);
        }
    }

    private static String flattenToAscii(String in) {
        StringBuilder out = new StringBuilder();
        for (char ch : in.toCharArray()) {
            if (ch <= 127)
                out.append(ch);
            else
                out.append("\\u").append(String.format("%04x", (int) ch));
        }
        return out.toString();
    }

    public void onEnable() {

        this.nativeEnvironment.saveDefaultConfig();

        this.settings = this.nativeEnvironment.getDiscordLoggerConfig();

        if (this.settings == null) {
            this.nativeEnvironment.log(Level.SEVERE, "Failed to load configuration settings");
            return;
        }

        try {

            this.nativeEnvironment.hookLogStreams();

            // Timer task will be closed in Spigot and Bungeecord once plugin is disabled
            // Consider a new method to disable timer tasks if adding a new native environment
            this.nativeEnvironment.runAsyncTimer(nativeEnvironment::flushLogHook, 30, TimeUnit.SECONDS);

            this.nativeEnvironment.log(Level.INFO, "Successfully hooked logger stream");

        } catch (Exception e) {
            this.nativeEnvironment.log(Level.SEVERE, "Failed to hook logger stream");
            e.printStackTrace();
        }

    }

    public void onDisable() {

        try {
            this.nativeEnvironment.unhookLogStreams();
        } catch (Exception e) {
            e.printStackTrace();
        }

        INSTANCE_LIST.remove(this);
    }

    public ConfigSettings getSettings() {
        return this.settings;
    }

    public void postMessage(final WebhookBuilder payload) {

        //Generate a unique hash for the message.
        Integer hash = Hashing.md5().hashInt(payload.hashCode()).asInt();

        //Don't spam discord with the same message
        if (this.recentMessages.containsKey(hash)) {
            if ((System.currentTimeMillis() - this.recentMessages.get(hash)) < TimeUnit.HOURS.toMillis(1))
                return;
        }

        //Clean expired entries
        this.recentMessages.entrySet().removeIf(en -> (System.currentTimeMillis() - en.getValue()) > TimeUnit.HOURS.toMillis(1));

        //Add it to the throttle
        this.recentMessages.put(hash, System.currentTimeMillis());

        this.nativeEnvironment.runAsync(() -> {

            try {

                String response = payload.executeWith(DiscordLogger.this.settings.userAgent, DiscordLogger.this.settings.discordWebhookURL);
                if (response != null) {
                    nativeEnvironment.log(Level.INFO, response);
                }

            } catch (IOException e) {
                e.printStackTrace();
                nativeEnvironment.log(Level.SEVERE, payload.toJsonString());
            }

        });

    }

    public NativeEnvironment getEnvironment() {
        return this.nativeEnvironment;
    }
}