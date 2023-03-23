package xyz.nat1an.notebot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.nat1an.notebot.NotebotPlayer;
import xyz.nat1an.notebot.suggestions.SongSuggestionProvider;
import xyz.nat1an.notebot.utils.NotebotFileManager;
import xyz.nat1an.notebot.utils.NotebotUtils;

import static xyz.nat1an.notebot.NotebotPlayer.song;
import static xyz.nat1an.notebot.commands.NotebotInfoCommand.listRequirements;

public class NotebotLoadCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
            CommandManager.literal("notebot")
                .then(CommandManager.literal("load")
                    .then(CommandManager.argument("song", StringArgumentType.greedyString()).suggests(new SongSuggestionProvider())
                        .executes(NotebotLoadCommand::run)
                    )
                )
        );
    }

    private static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        context.getSource().sendMessage(Text.literal("§6Loading 7c" + context.getArgument("song", String.class) + " §6..."));

        song = NotebotUtils.parse(
            NotebotFileManager.getDir().resolve(
                "songs/" + context.getArgument("song", String.class)
            )
        );

        context.getSource().sendMessage(Text.literal(listRequirements(song)));

        NotebotPlayer.loadSong();

        context.getSource().sendMessage(Text.literal("§6Loaded the song!"));

        return 1;
    }
}
