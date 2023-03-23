package xyz.nat1an.notebot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import xyz.nat1an.notebot.NotebotPlayer;

public class NotebotStopCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
            CommandManager.literal("notebot")
                .then(CommandManager.literal("stop")
                    .executes(NotebotStopCommand::run)
                )
        );
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        NotebotPlayer.playing = false;

        return 1;
    }
}
