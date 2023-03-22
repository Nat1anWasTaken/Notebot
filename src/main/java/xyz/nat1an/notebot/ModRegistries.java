package xyz.nat1an.notebot;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import xyz.nat1an.notebot.commands.NotebotSelectCommand;

public class ModRegistries {
    public static void registerModStuff() {
        ModRegistries.registerCommands();
        ModRegistries.registerEvents();
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(NotebotSelectCommand::register);
    }

    private static void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(NotebotPlayer::onTick);
    }
}
