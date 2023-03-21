package xyz.nat1an.notebot;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import xyz.nat1an.notebot.commands.TestCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import xyz.nat1an.notebot.events.AttackBlockEvent;

public class ModRegistries {
    public static void registerModStuff() {
        ModRegistries.registerCommands();
        ModRegistries.registerEvents();
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(TestCommand::register);
    }

    private static void registerEvents() {
        AttackBlockCallback.EVENT.register(new AttackBlockEvent());
    }
}
