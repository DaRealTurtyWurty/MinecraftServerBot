package dev.turtywurty.minecraftserverbot;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class CommandListener extends ListenerAdapter {
    private static final AtomicLong PID = new AtomicLong();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String name = event.getName();

        Optional<Long> ownerId = Environment.getInstance().getOwnerID();
        if(ownerId.isEmpty() || event.getUser().getIdLong() != ownerId.get()) {
            event.reply("You are not the owner of this bot!").mentionRepliedUser(false).setEphemeral(true).queue();
            return;
        }

        switch (name) {
            case "start" -> {
                if(PID.get() != 0) {
                    event.reply("Server is already running!").mentionRepliedUser(false).setEphemeral(true).queue();
                    return;
                }

                String startCommand = null;
                Optional<String> jarPath = Environment.getInstance().getJarPath();
                Optional<String> batchPath = Environment.getInstance().getBatchPath();
                Optional<String> serverStartCommand = Environment.getInstance().getServerStartCommand();
                while(startCommand == null) {
                    if(jarPath.isPresent()) {
                        Path asPath = Path.of(jarPath.get()).toAbsolutePath();
                        if(Files.notExists(asPath) || !jarPath.get().endsWith(".jar")) {
                            jarPath = Optional.empty();
                            continue;
                        }

                        startCommand = "java -jar " + asPath;
                    } else if(batchPath.isPresent()) {
                        Path asPath = Path.of(batchPath.get()).toAbsolutePath();
                        if(Files.notExists(asPath) || !batchPath.get().endsWith(".bat")) {
                            batchPath = Optional.empty();
                            continue;
                        }

                        startCommand = asPath.toString();
                    } else if(serverStartCommand.isPresent()) {
                        startCommand = serverStartCommand.get();
                    } else {
                        event.reply("Server start command is not present!").mentionRepliedUser(false).setEphemeral(true).queue();
                        return;
                    }
                }

                try {
                    Process process = new ProcessBuilder(startCommand).start();

                    try {
                        process.waitFor();
                        PID.set(process.pid());
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
                if(PID.get() == 0) {
                    event.reply("Server is not running!").mentionRepliedUser(false).setEphemeral(true).queue();
                    return;
                }

                event.reply("Stopping server...").mentionRepliedUser(false).queue();

                try {
                    Process process = new ProcessBuilder("taskkill", "/PID", String.valueOf(PID.get()), "/F").start();

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
