package dev.turtywurty.minecraftserverbot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String name = event.getName();

        switch (name) {
            case "start" -> {
                Optional<String> startCommand = Environment.getInstance().getServerStartCommand();
                if(startCommand.isEmpty()) {
                    event.reply("Server start command is not present!").mentionRepliedUser(false).setEphemeral(true).queue();
                    return;
                }

                event.reply("Starting server...").mentionRepliedUser(false).queue();

                try {
                    Process process = new ProcessBuilder(startCommand.get()).start();

                    try {
                        process.waitFor();
                        event.getHook().editOriginal("Server started!").mentionRepliedUser(false).queue();
                    } catch (InterruptedException exception) {
                        MinecraftServerBot.LOGGER.error("An error occurred while waiting for the server to start!", exception);
                    }
                } catch (IOException exception) {
                    event.getHook().editOriginal("An error occurred while starting the server!").mentionRepliedUser(false).queue();
                    MinecraftServerBot.LOGGER.error("An error occurred while starting the server!", exception);
                }
            }
            case "stop" -> {
                Optional<String> stopCommand = Environment.getInstance().getServerStopCommand();
                if(stopCommand.isEmpty()) {
                    event.reply("Server stop command is not present!").mentionRepliedUser(false).setEphemeral(true).queue();
                    return;
                }

                event.reply("Stopping server...").mentionRepliedUser(false).queue();

                try {
                    Process process = new ProcessBuilder(stopCommand.get()).start();

                    try {
                        process.waitFor();
                        event.getHook().editOriginal("Server stopped!").mentionRepliedUser(false).queue();
                    } catch (InterruptedException exception) {
                        MinecraftServerBot.LOGGER.error("An error occurred while waiting for the server to stop!", exception);
                    }
                } catch (IOException exception) {
                    event.getHook().editOriginal("An error occurred while stopping the server!").mentionRepliedUser(false).queue();
                    MinecraftServerBot.LOGGER.error("An error occurred while stopping the server!", exception);
                }
            }
            default -> event.reply("Unknown command!").mentionRepliedUser(false).setEphemeral(true).queue();
        }
    }
}
