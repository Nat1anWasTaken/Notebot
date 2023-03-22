package xyz.nat1an.notebot.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xyz.nat1an.notebot.utils.NotebotFileManager;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;


// TODO: Song caching to make it faster
public class SongSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        // TODO: Song search
        String keyword = builder.getInput();  // This is not working yet. The keyword must be song name instead of whole command.

        for (String f : getSongs(keyword)) {
            builder.suggest(f);
        }

        return builder.buildFuture();
    }

    private Set<String> getSongs(String keyword) {
        Set<String> files = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        files.clear();

        NotebotFileManager.getDir().resolve("songs/").toFile().mkdirs();

        if (keyword.isEmpty()) {
            files.addAll(Arrays.asList(NotebotFileManager.getDir().resolve("songs/").toFile().list()));
            return files;
        }

        for (String f : NotebotFileManager.getDir().resolve("songs/").toFile().list()) {
            if (f.contains(keyword)) {
                files.add(f);
            }
        }

        return files;
    }
}
