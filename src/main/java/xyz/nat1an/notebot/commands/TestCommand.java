package xyz.nat1an.notebot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.nat1an.notebot.NoteBot;

public class TestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("hello")
                        .then(CommandManager.argument("arg", StringArgumentType.word())
                                .executes(TestCommand::run))
        );
    }

    private static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            var text = context.getArgument("arg", String.class);

            context.getSource().sendMessage(Text.literal("Hello! " + text));
        } catch (Exception e) {
            NoteBot.LOGGER.error(e.toString());
        }
        return 1;

    }
}
