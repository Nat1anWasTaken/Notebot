/*
This file is part of Notebot.
Notebot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
Notebot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with Notebot. If not, see <https://www.gnu.org/licenses/>.
*/

package xyz.nat1an.notebot.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import xyz.nat1an.notebot.NotebotPlayer;

public class QueueSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {
  @Override
  public CompletableFuture<Suggestions> getSuggestions(
      CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder)
      throws CommandSyntaxException {
    String keyword = builder.getInput().substring(builder.getStart());

    for (String i : NotebotPlayer.queue) {
      if (i.toLowerCase().contains(keyword.toLowerCase())) {
        builder.suggest(i);
      }
    }

    return builder.buildFuture();
  }
}
