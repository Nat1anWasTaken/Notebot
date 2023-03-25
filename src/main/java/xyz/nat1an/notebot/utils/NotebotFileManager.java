package xyz.nat1an.notebot.utils;

import net.minecraft.client.MinecraftClient;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Gets the mod directory in your minecraft folder.
     **/
    public static Path getDir() {
        return dir;
    }

    /**
     * Creates a file, doesn't do anything if the file already exists.
     **/
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

    /**
     * Returns true if a file exists, returns false otherwise
     **/
    public static boolean fileExists(String path) {
        try {
            return getDir().resolve(path).toFile().exists();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Deletes a file if it exists.
     **/
    public static void deleteFile(String path) {
        try {
            Files.deleteIfExists(getDir().resolve(path));
        } catch (Exception e) {
            logger.error("Error Deleting File: " + path, e);
        }
    }
}