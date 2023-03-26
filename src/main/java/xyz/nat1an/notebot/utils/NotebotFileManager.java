package xyz.nat1an.notebot.utils;

import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import xyz.nat1an.notebot.Notebot;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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