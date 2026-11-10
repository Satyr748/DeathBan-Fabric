package de.example.deathban;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

final class DeathBanConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    boolean enabled = true;
    String duration = "24h";
    boolean hardcoreHeartsEnabled = false;
    String resourcePackUrl = "";
    Map<String, Long> bans = new HashMap<>();

    long durationMillis() {
        if (duration == null) return 86_400_000L;
        String value = duration.trim().toLowerCase();
        if (value.equals("permanent") || value.equals("perm")) return -1L;
        try {
            long amount = Long.parseLong(value.substring(0, value.length() - 1));
            return Math.multiplyExact(amount, switch (value.charAt(value.length() - 1)) {
                case 's' -> 1_000L;
                case 'm' -> 60_000L;
                case 'h' -> 3_600_000L;
                case 'd' -> 86_400_000L;
                default -> throw new IllegalArgumentException("Use s, m, h or d");
            });
        } catch (Exception ignored) {
            return 86_400_000L;
        }
    }

    static DeathBanConfig load(Path path) throws IOException {
        if (!Files.exists(path)) return new DeathBanConfig();
        DeathBanConfig loaded = GSON.fromJson(Files.readString(path), DeathBanConfig.class);
        return loaded == null ? new DeathBanConfig() : loaded;
    }

    void save(Path path) throws IOException {
        Files.writeString(path, GSON.toJson(this));
    }
}
