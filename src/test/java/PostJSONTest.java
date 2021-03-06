import me.johnnywoof.discordlogger.formatting.EmbedBuilder;
import me.johnnywoof.discordlogger.formatting.WebhookBuilder;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class PostJSONTest {

    @Test
    public void postTest() throws IOException {

        WebhookBuilder webhookBuilder = new WebhookBuilder();

        EmbedBuilder builder = new EmbedBuilder()
                .addField("t", ".", false)
                .addField("log1", "Log Value 1 Inline\n\n", true)
                .addField("log2", "Log Value 2 Out of Line", false)
                .addField("HELLO SECTIONS!", "§", false)
                .setFooter("I have become invisible!").setTitle("ExCUUUUUUUSE ME").setColor(Color.RED);
        EmbedBuilder builder2 = new EmbedBuilder()
                .addField("t", ".", false)
                .addField("log1", "Log Value 1 Inline\n\n", true)
                .addField("log2", "Log Value 2 Out of Line", false)
                .setFooter("I have become invisible!").setTitle("ExCUUUUUUUSE ME").setColor(Color.GREEN);

        webhookBuilder.addEmbed(builder).addEmbed(builder2);

        URL url = new URL("https://discordapp.com/api/webhooks/400055354869809153/1YWA1keeL0UbtAAbFVHQuRid-25K75VY31--tBO7iB2tVI8VQy1m4Pf7oJfAOz3GSykj");

        webhookBuilder.executeWith("DiscordLogger", url);
    }

}
