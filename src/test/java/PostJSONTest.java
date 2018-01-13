import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.johnnywoof.discordlogger.formatting.EmbedBuilder;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostJSONTest {

    @Test
    public void postTest() throws IOException {

        EmbedBuilder builder = new EmbedBuilder()
                .addField("t", ".", false)
                .addField("log1", "Log Value 1 Inline\n\n", true)
                .addField("log2", "Log Value 2 Out of Line", false)
                .setFooter("I have become invisible!").setTitle("ExCUUUUUUUSE ME").setColor(Color.RED);
        JsonObject obj1 = new JsonObject();
        obj1.addProperty("content", "");
        JsonArray array = new JsonArray();
        array.add(builder.toJSONObject());
        obj1.add("embeds", array);

        String json = new Gson().toJson(obj1);

        URL url = new URL("https://discordapp.com/api/webhooks/400055354869809153/1YWA1keeL0UbtAAbFVHQuRid-25K75VY31--tBO7iB2tVI8VQy1m4Pf7oJfAOz3GSykj");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "DiscordLogger");
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        con.setDoOutput(true);
        OutputStream wr = con.getOutputStream();

        String str = builder.toJSONObject().toString();
        if (str.length() >= 1997)
            throw new UnsupportedEncodingException("Welp.");

        wr.write(json.getBytes());
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        System.out.println(responseCode);

        if (responseCode != 204) {
            throw new MalformedURLException("Oops");
        }

        con.getInputStream().close();
        con.disconnect();
    }

}
