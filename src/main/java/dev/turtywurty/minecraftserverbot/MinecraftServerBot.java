package dev.turtywurty.minecraftserverbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class MinecraftServerBot {
    public static final Logger LOGGER = LoggerFactory.getLogger(MinecraftServerBot.class);

    public static void main(String[] args) {
        Optional<String> botToken = Environment.getInstance().getBotToken();
        if (botToken.isEmpty()) {
            LOGGER.error("Bot token is not present!");
            return;
        }

        JDA jda = JDABuilder.createLight(botToken.get())
                .addEventListeners(new CommandListener())
                .build();

        try {
            jda.awaitReady();
        } catch (InterruptedException exception) {
            LOGGER.error("An error occurred while waiting for the bot to be ready!", exception);
            return;
        }

        jda.getPresence().setActivity(Activity.playing("Minecraft"));
        jda.updateCommands()
                .addCommands(
                        Commands.slash("start", "Starts the Minecraft server"),
                        Commands.slash("stop", "Stops the Minecraft server"))
                .queue();

        LOGGER.info("Bot is online!");
    }
}
