package xyz.nat1an.notebot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.block.enums.Instrument;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.nat1an.notebot.NotebotPlayer;
import xyz.nat1an.notebot.suggestions.SongSuggestionProvider;
import xyz.nat1an.notebot.utils.NotebotFileManager;
import xyz.nat1an.notebot.utils.NotebotUtils;

import java.util.Map;

public class NotebotInfoCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
            CommandManager.literal("notebot")
                .then(CommandManager.literal("info")
                    .then(CommandManager.argument("song", StringArgumentType.greedyString()).suggests(new SongSuggestionProvider())
                        .executes(NotebotInfoCommand::run)
                    )
                )
        );
    }

    public static String listRequirements(NotebotPlayer.Song song) {
        StringBuilder result = new StringBuilder();

        result.append("Song: ").append(song.name);

        for (Map.Entry<Instrument, ItemStack> e : NotebotUtils.INSTRUMENT_TO_ITEM.entrySet()) {
            int count = (int) song.requirements.stream().filter(n -> n.instrument == e.getKey().ordinal()).count();

            if (count != 0) {
                result.append("\n- ").append(e.getValue().getName().getString()).append(": ").append(count);
            }
        }

        return result.toString();
    }

    private static int run(CommandContext<ServerCommandSource> context) {
        NotebotPlayer.Song song = NotebotUtils.parse(
            NotebotFileManager.getDir().resolve(
                "songs/" + context.getArgument("song", String.class)
            )
        );

        context.getSource().sendMessage(Text.literal(listRequirements(song)));

        return 1;
    }
}
