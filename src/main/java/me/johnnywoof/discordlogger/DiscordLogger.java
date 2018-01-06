package me.johnnywoof.discordlogger;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DiscordLogger {

    private final NativeEnvironment nativeEnvironment;
    private final Map<Integer, Long> recentMessages = new HashMap<>();
    private URL discordWebhookURL;
    private String userAgent;
    private String messagePrefix;
    private Collection<String> keywords;

    public DiscordLogger(NativeEnvironment nativeEnvironment) {
        this.nativeEnvironment = nativeEnvironment;
    }

    public void onEnable() {

        this.nativeEnvironment.saveDefaultConfig();

        ConfigSettings settings = this.nativeEnvironment.getConfig();

        if (settings == null) {
            this.nativeEnvironment.log(Level.SEVERE, "Failed to load configuration settings");
            return;
        }

        if (settings.keywords == null || settings.keywords.isEmpty()) {
            this.nativeEnvironment.log(Level.WARNING, "No keywords were detected in configuration file!");
            return;
        }

        if (settings.discordWebhookURL == null || settings.discordWebhookURL.isEmpty()) {
            this.nativeEnvironment.log(Level.WARNING, "No discord webhook url was detected in the configuration file!");
            return;
        }

        this.keywords = Collections.unmodifiableCollection(settings.keywords);
        this.userAgent = (settings.userAgent == null || settings.userAgent.isEmpty()) ? "DiscordLogger" : settings.userAgent;
        this.messagePrefix = (settings.messagePrefix == null || settings.messagePrefix.isEmpty()) ? null : settings.messagePrefix;

        if (!"DiscordLogger".equals(this.userAgent))
            this.nativeEnvironment.log(Level.INFO, "User-Agent is " + this.userAgent);

        if (settings.messagePrefix != null)
            this.nativeEnvironment.log(Level.INFO, "Message prefix is " + this.messagePrefix);

        try {

            this.discordWebhookURL = new URL(settings.discordWebhookURL);

        } catch (MalformedURLException e) {
            this.nativeEnvironment.log(Level.SEVERE, "Exception when creating URL object");
            e.printStackTrace();
            return;
        }

        System.setOut(new ConsoleReaderPrintStream(this, System.out));
        System.setErr(new ConsoleReaderPrintStream(this, System.err));

    }

    public void onDisable() {

        if (System.out instanceof ConsoleReaderPrintStream)
            System.setOut(((ConsoleReaderPrintStream) System.out).getRawStream());

        if (System.err instanceof ConsoleReaderPrintStream)
            System.setErr(((ConsoleReaderPrintStream) System.err).getRawStream());

    }

    public String getMessagePrefix() {
        return this.messagePrefix;
    }

    public Collection<String> getKeywords() {
        return this.keywords;
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

                    HttpsURLConnection con = (HttpsURLConnection) DiscordLogger.this.discordWebhookURL.openConnection();

                    con.setRequestMethod("POST");
                    con.setRequestProperty("User-Agent", DiscordLogger.this.userAgent);

                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(new Gson().toJson(new Snowflake(message)));
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

}