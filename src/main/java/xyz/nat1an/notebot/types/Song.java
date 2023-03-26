/*
This file is part of Notebot.
Notebot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
Notebot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with Notebot. If not, see <https://www.gnu.org/licenses/>.
*/

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
