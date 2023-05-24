/*
This file is part of Notebot.
Notebot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
Notebot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with Notebot. If not, see <https://www.gnu.org/licenses/>.
*/

package xyz.nat1an.notebot.commands.queue;

import static xyz.nat1an.notebot.Notebot.mc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import xyz.nat1an.notebot.NotebotPlayer;

public class NotebotQueueCommand {
  public static void register(
      CommandDispatcher<FabricClientCommandSource> clientCommandSourceCommandDispatcher,
      CommandRegistryAccess commandRegistryAccess) {
    clientCommandSourceCommandDispatcher.register(
        ClientCommandManager.literal("notebot")
            .then(ClientCommandManager.literal("queue").executes(NotebotQueueCommand::run)));
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
