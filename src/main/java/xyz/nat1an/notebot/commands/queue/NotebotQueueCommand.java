package xyz.nat1an.notebot.commands.queue;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import xyz.nat1an.notebot.NotebotPlayer;
import xyz.nat1an.notebot.suggestions.SongSuggestionProvider;

import static xyz.nat1an.notebot.Notebot.mc;

public class NotebotQueueCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> clientCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess) {
        clientCommandSourceCommandDispatcher.register(
            ClientCommandManager.literal("notebot")
                .then(ClientCommandManager.literal("queue")
                    .executes(NotebotQueueCommand::run))
        );
    }

    private static String listQueue() {
        StringBuilder result = new StringBuilder();

        result.append("§6Queue:");

        for (int i = 0; i < NotebotPlayer.queue.size(); i++) {
            result.append("\n§6- §e" + i + ": §a" + NotebotPlayer.queue.get(i));
        }

        return result.toString();
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        mc.player.sendMessage(Text.literal(listQueue()));

        return 1;
    }
}
