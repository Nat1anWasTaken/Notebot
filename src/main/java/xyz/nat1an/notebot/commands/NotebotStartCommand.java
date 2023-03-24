package xyz.nat1an.notebot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import xyz.nat1an.notebot.NotebotPlayer;
import xyz.nat1an.notebot.utils.NotebotFileManager;
import xyz.nat1an.notebot.utils.NotebotUtils;

import static xyz.nat1an.notebot.Notebot.mc;

public class NotebotStartCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> clientCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess) {
        clientCommandSourceCommandDispatcher.register(
            ClientCommandManager.literal("notebot")
                .then(ClientCommandManager.literal("start")
                    .executes(NotebotStartCommand::run)
                )
        );
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        if (NotebotPlayer.song == null) {
            if (NotebotPlayer.queue.isEmpty()) {
                mc.player.sendMessage(Text.literal("§cYou need to load a song first!"));
                return 0;
            }

            NotebotPlayer.song = NotebotUtils.parse(
                NotebotFileManager.getDir().resolve(
                    "songs/" + NotebotPlayer.queue.remove(0)
                )
            );
        }

        if (!NotebotPlayer.loadSong()) {
            return 0;
        }

        NotebotPlayer.playing = true;

        return 1;
    }
}
