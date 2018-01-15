package me.johnnywoof.discordlogger.formatting;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.johnnywoof.discordlogger.DiscordLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class WebhookBuilder {

    private static final Gson GSON = new Gson();

    private boolean redactURL = true;
    private boolean redactIP = true;

    private StringBuilder messageBuilder = new StringBuilder();
    private boolean tts;
    private JsonArray embedArray = new JsonArray();

    public WebhookBuilder() {

    }

    public WebhookBuilder(boolean removeURL, boolean removeIP) {
        this.redactURL = removeURL;
        this.redactIP = removeIP;
        System.out.println("Current Values: " + removeURL + ", " + removeIP);
    }

    public WebhookBuilder addEmbed(EmbedBuilder builder) {
        return addEmbed(builder.toJSONObject());
    }

    public WebhookBuilder addEmbed(JsonObject object) {
        this.embedArray.add(object);
        return this;
    }

    public WebhookBuilder setMessage(String message) {
        message = redact(message.trim());
        if (message.length() >= DiscordLogger.MAX_DISCORD_CHARACTERS)
            message = message.substring(0, DiscordLogger.MAX_DISCORD_CHARACTERS);
        this.messageBuilder = new StringBuilder(message);
        return this;
    }

    public WebhookBuilder appendMessage(String message) {
        message = redact(message.trim());
        int maxLength = message.length() + this.messageBuilder.length();
        if (maxLength >= DiscordLogger.MAX_DISCORD_CHARACTERS) {
            int remaining = Math.abs(DiscordLogger.MAX_DISCORD_CHARACTERS - maxLength);
            for (char c : message.toCharArray()) {
                messageBuilder.append(c);
                remaining--;
                if (remaining <= 0)
                    break;
            }
        }

        return this;
    }

    public String redact(String message) {
        if (redactIP)
            message = message.replaceAll("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})", "[Redacted IP]");

        if (redactURL)
            message = message.replaceAll("http.*?\\s", "[Redacted URL]");

        return message;
    }

    public WebhookBuilder reset() {
        this.messageBuilder = new StringBuilder();
        this.tts = false;
        this.embedArray = new JsonArray();
        return this;
    }

    public void setRedactURL(boolean redactURL) {
        this.redactURL = redactURL;
    }

    public void setRedactIP(boolean redactIP) {
        this.redactIP = redactIP;
    }

    public String executeWith(String userAgent, URL url) throws IOException {

        String responseLog = null;

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", userAgent);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        JsonObject payloadJson = new JsonObject();

        payloadJson.addProperty("content", messageBuilder.toString());
        if (tts) payloadJson.addProperty("tts", true);
        payloadJson.add("embeds", embedArray);

        String payloadStr = GSON.toJson(payloadJson);

        con.setDoOutput(true);

        OutputStream outputStream = con.getOutputStream();
        outputStream.write(payloadStr.getBytes("UTF-8"));

        outputStream.flush();
        outputStream.close();

        int response = con.getResponseCode();
        if (response >= 300)
            responseLog = "Discord responded with HTTP response code " + response;

        con.getInputStream().close();
        con.disconnect();

        return responseLog;
    }

    public String toJsonString() {
        JsonObject payloadJson = new JsonObject();

        payloadJson.addProperty("content", messageBuilder.toString());
        if (tts) payloadJson.addProperty("tts", true);
        payloadJson.add("embeds", embedArray);

        return GSON.toJson(payloadJson);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebhookBuilder that = (WebhookBuilder) o;
        return tts == that.tts &&
                Objects.equals(messageBuilder, that.messageBuilder) &&
                Objects.equals(embedArray, that.embedArray);
    }

    @Override
    public int hashCode() {

        return Objects.hash(messageBuilder, tts, embedArray);
    }

    public boolean isEmpty() {
        boolean messageFlag = this.messageBuilder == null || this.messageBuilder.length() <= 0;
        boolean arrayflag = this.embedArray.size() <= 0;
        return messageFlag && arrayflag;
    }
}
