/*
This file is part of Notebot.
Notebot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
Notebot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with Notebot. If not, see <https://www.gnu.org/licenses/>.
*/

package xyz.nat1an.notebot.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import xyz.nat1an.notebot.Notebot;

public class NotebotFileManager {

  private static Path dir;
  private static Logger logger = Notebot.LOGGER;

  public static void init() {
    dir = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "notebot/");
    if (!dir.toFile().exists()) {
      dir.toFile().mkdirs();
    }
  }

  public static Path getDir() {
    return dir;
  }

  public static void createFile(String path) {
    try {
      if (!fileExists(path)) {
        getDir().resolve(path).getParent().toFile().mkdirs();
        Files.createFile(getDir().resolve(path));
      }
    } catch (Exception e) {
      logger.error("Error Creating File: " + path, e);
    }
  }

  public static boolean fileExists(String path) {
    try {
      return getDir().resolve(path).toFile().exists();
    } catch (Exception e) {
      return false;
    }
  }
}
