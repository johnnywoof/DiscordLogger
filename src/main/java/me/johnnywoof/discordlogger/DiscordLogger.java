package me.johnnywoof.discordlogger;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DiscordLogger {

    private final NativeEnvironment nativeEnvironment;
    private final Map<Integer, Long> recentMessages = new HashMap<>();
    private ConfigSettings settings;

    public DiscordLogger(NativeEnvironment nativeEnvironment) {
        this.nativeEnvironment = nativeEnvironment;
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
            this.nativeEnvironment.runAsyncTimer(new FlushLogHandlerTask(this.nativeEnvironment), 30, TimeUnit.SECONDS);

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

    }

    public ConfigSettings getSettings() {
        return this.settings;
    }

    void postMessage(final String message) {
        if (message.length() <= 2000) {

            //Generate a unique hash for the message.
            Integer hash = Hashing.md5().hashString(message, StandardCharsets.UTF_8).asInt();

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

                    HttpsURLConnection con = (HttpsURLConnection) DiscordLogger.this.settings.discordWebhookURL.openConnection();

                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", DiscordLogger.this.settings.userAgent);

                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());

                    // Discord seems to only accepts ASCII characters.
                    wr.writeBytes(new Gson().toJson(new Snowflake(flatternToAscii(message))));
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();

                    if (responseCode != 204) {
                        DiscordLogger.this.nativeEnvironment.log(Level.WARNING, "Discord responded with HTTP response code " + responseCode);
                    }

                    con.getInputStream().close();
                    con.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

        } else {
            throw new IllegalArgumentException("Message must not exceed 2,000 characters");
        }
    }

    private static String flatternToAscii(String in) {
        StringBuilder out = new StringBuilder();
        for (char ch : in.toCharArray()) {
            if (ch <= 127)
                out.append(ch);
            else
                out.append("\\u").append(String.format("%04x", (int) ch));
        }
        return out.toString();
    }

}