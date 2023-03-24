package xyz.nat1an.notebot;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import xyz.nat1an.notebot.commands.NotebotInfoCommand;
import xyz.nat1an.notebot.commands.NotebotStartCommand;
import xyz.nat1an.notebot.commands.NotebotStopCommand;
import xyz.nat1an.notebot.commands.queue.NotebotQueueAddCommand;
import xyz.nat1an.notebot.commands.queue.NotebotQueueCommand;
import xyz.nat1an.notebot.commands.queue.NotebotQueueRemoveCommand;

public class ModRegistries {
    public static void registerModStuff() {
        ModRegistries.registerCommands();
        ModRegistries.registerEvents();
    }

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register(NotebotInfoCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(NotebotStartCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(NotebotStopCommand::register);

        ClientCommandRegistrationCallback.EVENT.register(NotebotQueueAddCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(NotebotQueueRemoveCommand::register);
        ClientCommandRegistrationCallback.EVENT.register(NotebotQueueCommand::register);
    }

    private static void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(NotebotPlayer::onTick);
    }
}
