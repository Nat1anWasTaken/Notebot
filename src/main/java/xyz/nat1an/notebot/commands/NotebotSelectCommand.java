package xyz.nat1an.notebot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import net.minecraft.text.Text;
import xyz.nat1an.notebot.suggestions.SongSuggestionProvider;

public class NotebotSelectCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
            CommandManager.literal("notebot")
                .then(CommandManager.literal("select")
                    .then(CommandManager.argument("song", StringArgumentType.greedyString()).suggests(new SongSuggestionProvider())
                    )
                )
        );
    }

    public int execute(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("You've selected " + context.getArgument("song", Text.class) + "!"));

        return 1;
    }
}
