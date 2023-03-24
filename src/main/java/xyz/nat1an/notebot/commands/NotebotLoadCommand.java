package xyz.nat1an.notebot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import xyz.nat1an.notebot.suggestions.SongSuggestionProvider;
import xyz.nat1an.notebot.utils.NotebotFileManager;
import xyz.nat1an.notebot.utils.NotebotUtils;

import static xyz.nat1an.notebot.Notebot.mc;
import static xyz.nat1an.notebot.NotebotPlayer.song;
import static xyz.nat1an.notebot.commands.NotebotInfoCommand.listRequirements;

public class NotebotLoadCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> clientCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess) {
        clientCommandSourceCommandDispatcher.register(
            ClientCommandManager.literal("notebot")
                .then(ClientCommandManager.literal("load")
                    .then(ClientCommandManager.argument("song", StringArgumentType.greedyString())
                        .suggests(new SongSuggestionProvider())
                        .executes(NotebotLoadCommand::run)
                    )
                )
        );
    }


    private static int run(CommandContext<FabricClientCommandSource> context) {
        mc.player.sendMessage(Text.literal("§6Loading §c" + context.getArgument("song", String.class) + " §6..."));

        song = NotebotUtils.parse(
            NotebotFileManager.getDir().resolve(
                "songs/" + context.getArgument("song", String.class)
            )
        );

        mc.player.sendMessage(Text.literal(listRequirements(song)));

        mc.player.sendMessage(Text.literal("§6Loaded the song!"));

        return 1;
    }
}
