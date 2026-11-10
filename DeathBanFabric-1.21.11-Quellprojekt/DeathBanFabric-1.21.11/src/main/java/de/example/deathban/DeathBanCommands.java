package de.example.deathban;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

final class DeathBanCommands {
    static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(CommandManager.literal("deathban")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("on").executes(ctx -> setEnabled(ctx.getSource(), true)))
                .then(CommandManager.literal("off").executes(ctx -> setEnabled(ctx.getSource(), false)))
                .then(CommandManager.literal("settime")
                    .then(CommandManager.argument("dauer", StringArgumentType.word())
                        .executes(ctx -> {
                            String duration = StringArgumentType.getString(ctx, "dauer");
                            DeathBanConfig c = DeathBanMod.config();
                            c.duration = duration;
                            DeathBanMod.saveQuietly();
                            ctx.getSource().sendFeedback(() -> Text.literal("DeathBan-Dauer: " + duration), true);
                            return 1;
                        })))
                .then(CommandManager.literal("unban")
                    .then(CommandManager.argument("uuid", StringArgumentType.word())
                        .executes(ctx -> {
                            String uuid = StringArgumentType.getString(ctx, "uuid");
                            boolean removed = DeathBanMod.config().bans.remove(uuid) != null;
                            DeathBanMod.saveQuietly();
                            ctx.getSource().sendFeedback(() -> Text.literal(
                                removed ? "DeathBan entfernt." : "Kein Ban für diese UUID gefunden."), true);
                            return removed ? 1 : 0;
                        })))
                .then(CommandManager.literal("status").executes(ctx -> {
                    DeathBanConfig c = DeathBanMod.config();
                    ctx.getSource().sendFeedback(() -> Text.literal(
                        "DeathBan: " + (c.enabled ? "AN" : "AUS") + " | Dauer: " + c.duration
                        + " | aktive Einträge: " + c.bans.size()), false);
                    return 1;
                }))
            )
        );
    }

    private static int setEnabled(ServerCommandSource source, boolean enabled) {
        DeathBanMod.config().enabled = enabled;
        DeathBanMod.saveQuietly();
        source.sendFeedback(() -> Text.literal("DeathBan " + (enabled ? "aktiviert." : "deaktiviert.")), true);
        return 1;
    }
}
