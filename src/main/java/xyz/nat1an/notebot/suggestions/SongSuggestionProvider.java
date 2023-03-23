package xyz.nat1an.notebot.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import xyz.nat1an.notebot.utils.NotebotFileManager;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;


// TODO: Song caching to make it faster
public class SongSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        // TODO: Song search
        String keyword = builder.getInput().substring(builder.getStart());

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
