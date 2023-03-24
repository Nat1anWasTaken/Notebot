package xyz.nat1an.notebot.commands.queue;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import xyz.nat1an.notebot.NotebotPlayer;
import xyz.nat1an.notebot.suggestions.SongSuggestionProvider;

public class NotebotQueueAddCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> clientCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess) {
        clientCommandSourceCommandDispatcher.register(
            ClientCommandManager.literal("notebot")
                .then(ClientCommandManager.literal("queue")
                    .then(ClientCommandManager.literal("add")
                        .then(ClientCommandManager.argument("song", StringArgumentType.greedyString())
                            .suggests(new SongSuggestionProvider())
                            .executes(NotebotQueueAddCommand::run)
                        )
                    )
                )
        );
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        NotebotPlayer.queue.add(context.getArgument("song", String.class));

        return 1;
    }
}
