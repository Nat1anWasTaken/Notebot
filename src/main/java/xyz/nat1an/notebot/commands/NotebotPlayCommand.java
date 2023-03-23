package xyz.nat1an.notebot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.nat1an.notebot.NotebotPlayer;

public class NotebotPlayCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
            CommandManager.literal("notebot")
                .then(CommandManager.literal("play")
                    .executes(NotebotPlayCommand::run)
                )
        );
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        if (NotebotPlayer.song == null) {
            context.getSource().sendMessage(Text.literal("§cYou need to load a song first!"));
            return 0;
        }

        if (!NotebotPlayer.loadSong()) {
            return 0;
        }

        NotebotPlayer.playing = true;

        return 1;
    }
}
