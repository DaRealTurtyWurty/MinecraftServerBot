package dev.turtywurty.minecraftserverbot;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Optional;

public class Environment {
    private static final Environment INSTANCE = new Environment();
    private final Dotenv dotenv;

    private Environment() {
        this.dotenv = Dotenv.configure().load();
    }

    public static Environment getInstance() {
        return INSTANCE;
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(this.dotenv.get(key));
    }

    public String get(String key, String defaultValue) {
        return this.dotenv.get(key, defaultValue);
    }

    public Optional<String> getBotToken() {
        return get("BOT_TOKEN");
    }

    public Optional<String> getJarPath() {
        return get("JAR_PATH");
    }

    public Optional<String> getBatchPath() {
        return get("BATCH_PATH");
    }

    public Optional<String> getServerStartCommand() {
        return get("SERVER_START_COMMAND");
    }

    public Optional<String> getServerStopCommand() {
        return get("SERVER_STOP_COMMAND");
    }

    public Optional<Long> getLong(String key) {
        return get(key).map(Long::parseLong);
    }

    public Optional<Long> getOwnerID() {
        return getLong("OWNER_ID");
    }
}
