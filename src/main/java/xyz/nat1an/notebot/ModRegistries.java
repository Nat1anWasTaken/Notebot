package xyz.nat1an.notebot;

import com.mojang.brigadier.Command;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import xyz.nat1an.notebot.commands.NotebotInfoCommand;
import xyz.nat1an.notebot.commands.NotebotLoadCommand;
import xyz.nat1an.notebot.commands.NotebotPlayCommand;
import xyz.nat1an.notebot.commands.NotebotStopCommand;

public class ModRegistries {
    public static void registerModStuff() {
        ModRegistries.registerCommands();
        ModRegistries.registerEvents();
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(NotebotInfoCommand::register);
        CommandRegistrationCallback.EVENT.register(NotebotLoadCommand::register);
        CommandRegistrationCallback.EVENT.register(NotebotPlayCommand::register);
        CommandRegistrationCallback.EVENT.register(NotebotStopCommand::register);
    }

    private static void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(NotebotPlayer::onTick);
    }
}
