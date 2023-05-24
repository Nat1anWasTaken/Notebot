/*
This file is part of Notebot.
Notebot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
Notebot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with Notebot. If not, see <https://www.gnu.org/licenses/>.
*/

package xyz.nat1an.notebot;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import xyz.nat1an.notebot.commands.NotebotInfoCommand;
import xyz.nat1an.notebot.commands.NotebotStartCommand;
import xyz.nat1an.notebot.commands.NotebotStopCommand;
import xyz.nat1an.notebot.commands.queue.NotebotQueueAddCommand;
import xyz.nat1an.notebot.commands.queue.NotebotQueueCleanCommand;
import xyz.nat1an.notebot.commands.queue.NotebotQueueCommand;
import xyz.nat1an.notebot.commands.queue.NotebotQueueRemoveCommand;

public class ModRegistries {
  public static void registerModStuff() {
    ModRegistries.registerCommands();
    ModRegistries.registerEvents();
  }

  private static void registerCommands() {
    ClientCommandRegistrationCallback.EVENT.register(NotebotInfoCommand::register);
    ClientCommandRegistrationCallback.EVENT.register(NotebotStartCommand::register);
    ClientCommandRegistrationCallback.EVENT.register(NotebotStopCommand::register);

    ClientCommandRegistrationCallback.EVENT.register(NotebotQueueCleanCommand::register);
    ClientCommandRegistrationCallback.EVENT.register(NotebotQueueAddCommand::register);
    ClientCommandRegistrationCallback.EVENT.register(NotebotQueueRemoveCommand::register);
    ClientCommandRegistrationCallback.EVENT.register(NotebotQueueCommand::register);
  }

  private static void registerEvents() {
    ClientTickEvents.END_CLIENT_TICK.register(NotebotPlayer::onTick);
  }
}
