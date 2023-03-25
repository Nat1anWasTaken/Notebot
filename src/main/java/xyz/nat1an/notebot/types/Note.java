package xyz.nat1an.notebot.types;

import xyz.nat1an.notebot.NotebotPlayer;

public class Note {

    public int pitch;
    public int instrument;

    public Note(int pitch, int instrument) {
        this.pitch = pitch;
        this.instrument = instrument;
    }

    @Override
    public int hashCode() {
        return pitch * 31 + instrument;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Note)) return false;

        Note other = (Note) obj;
        return instrument == other.instrument && pitch == other.pitch;
    }
}
