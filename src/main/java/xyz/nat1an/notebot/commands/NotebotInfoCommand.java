package xyz.nat1an.notebot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.enums.Instrument;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import xyz.nat1an.notebot.NotebotPlayer;
import xyz.nat1an.notebot.suggestions.SongSuggestionProvider;
import xyz.nat1an.notebot.utils.NotebotFileManager;
import xyz.nat1an.notebot.utils.NotebotUtils;

import java.util.Map;

import static xyz.nat1an.notebot.Notebot.mc;

public class NotebotInfoCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> clientCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess) {
        clientCommandSourceCommandDispatcher.register(
            ClientCommandManager.literal("notebot")
                .then(ClientCommandManager.literal("info")
                    .then(ClientCommandManager.argument("song", StringArgumentType.greedyString()).suggests(new SongSuggestionProvider())
                        .executes(NotebotInfoCommand::run)
                    )
                )
        );
    }

    public static String listRequirements(NotebotPlayer.Song song) {
        StringBuilder result = new StringBuilder();

        result.append("§6Song: §e").append(song.name);

        for (Map.Entry<Instrument, ItemStack> e : NotebotUtils.INSTRUMENT_TO_ITEM.entrySet()) {
            int count = (int) song.requirements.stream().filter(n -> n.instrument == e.getKey().ordinal()).count();

            if (count != 0) {
                result.append("\n§6- §e").append(e.getValue().getName().getString()).append(": §a").append(count);
            }
        }

        return result.toString();
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        NotebotPlayer.Song song = NotebotUtils.parse(
            NotebotFileManager.getDir().resolve(
                "songs/" + context.getArgument("song", String.class)
            )
        );

        mc.player.sendMessage(Text.literal(listRequirements(song)));

        return 1;
    }
}
