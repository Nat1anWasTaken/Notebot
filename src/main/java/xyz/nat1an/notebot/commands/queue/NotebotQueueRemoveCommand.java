package xyz.nat1an.notebot.commands.queue;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import xyz.nat1an.notebot.NotebotPlayer;

import static xyz.nat1an.notebot.Notebot.mc;

public class NotebotQueueRemoveCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> clientCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess) {
        clientCommandSourceCommandDispatcher.register(
            ClientCommandManager.literal("notebot")
                .then(ClientCommandManager.literal("queue")
                    .then(ClientCommandManager.literal("remove")
                        .then(
                            ClientCommandManager.argument(
                                "index",
                                IntegerArgumentType.integer()
                            )
                        )
                    )
                )
        );
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        int index = context.getArgument("index", Integer.class);

        String name;

        try {
            name = NotebotPlayer.queue.remove(index);
        } catch (IndexOutOfBoundsException e) {
            mc.player.sendMessage(Text.literal("§cIndex out of bounds."));
            return 0;
        }

        mc.player.sendMessage(Text.literal("§6Removed §a" + name + "§6 at §e" + index + "from the queue."));

        return 1;
    }
}
