package de.example.deathban;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class DeathBanMod implements ModInitializer {
    private static DeathBanConfig config;
    private static Path configPath;

    @Override
    public void onInitialize() {
        Path dir = Path.of("config");
        configPath = dir.resolve("deathban.json");
        try {
            Files.createDirectories(dir);
            config = DeathBanConfig.load(configPath);
            config.save(configPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not load DeathBan config", e);
        }

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!config.enabled || !(entity instanceof ServerPlayerEntity player)) return;
            long until = config.durationMillis() == -1L
                    ? -1L : System.currentTimeMillis() + config.durationMillis();

            config.bans.put(player.getUuid().toString(), until);
            saveQuietly();
            String message = until == -1L
                    ? "Du bist gestorben und dauerhaft gebannt."
                    : "Du bist gestorben und für " + config.duration + " gebannt.";
            player.networkHandler.disconnect(Text.literal(message));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (!config.enabled) return;
            UUID uuid = handler.player.getUuid();
            Long until = config.bans.get(uuid.toString());
            if (until == null) return;

            if (until != -1L && System.currentTimeMillis() >= until) {
                config.bans.remove(uuid.toString());
                saveQuietly();
                return;
            }

            String message = until == -1L
                    ? "Du bist dauerhaft gebannt."
                    : "Du bist noch für " + formatRemaining(until - System.currentTimeMillis()) + " gebannt.";
            handler.disconnect(Text.literal(message));
        });

        DeathBanCommands.register();
    }

    static DeathBanConfig config() { return config; }
    static void saveQuietly() {
        try { config.save(configPath); }
        catch (IOException e) { System.err.println("[DeathBan] Could not save config: " + e.getMessage()); }
    }

    static String formatRemaining(long millis) {
        long seconds = Math.max(0, millis / 1000);
        long days = seconds / 86400; seconds %= 86400;
        long hours = seconds / 3600; seconds %= 3600;
        long minutes = seconds / 60;
        if (days > 0) return days + "d " + hours + "h";
        if (hours > 0) return hours + "h " + minutes + "m";
        return minutes + "m";
    }
}
