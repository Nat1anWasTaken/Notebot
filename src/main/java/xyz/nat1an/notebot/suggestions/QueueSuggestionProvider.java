package xyz.nat1an.notebot.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import xyz.nat1an.notebot.NotebotPlayer;

import java.util.concurrent.CompletableFuture;

public class QueueSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String keyword = builder.getInput().substring(builder.getStart());

        for (String i : NotebotPlayer.queue) {
            if (i.toLowerCase().contains(keyword.toLowerCase())) {
                builder.suggest(i);
            }
        }

        return builder.buildFuture();
    }
}
