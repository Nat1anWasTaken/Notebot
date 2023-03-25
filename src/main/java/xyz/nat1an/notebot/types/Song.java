package xyz.nat1an.notebot.types;

import com.google.common.collect.Multimap;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Song {

    public String filename;
    public String name;
    public String author;
    public String format;

    public Multimap<Integer, Note> notes;
    public Set<Note> requirements = new HashSet<>();
    public int length;

    public Song(String filename, String name, String author, String format, Multimap<Integer, Note> notes) {
        this.filename = filename;
        this.name = name;
        this.author = author;
        this.format = format;
        this.notes = notes;

        notes.values().stream().distinct().forEach(requirements::add);
        length = notes.keySet().stream().max(Comparator.naturalOrder()).orElse(0);
    }
}
