package xyz.nat1an.notebot;

import com.google.common.collect.Multimap;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import xyz.nat1an.notebot.utils.NotebotFileManager;
import xyz.nat1an.notebot.utils.NotebotUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import static xyz.nat1an.notebot.Notebot.mc;

public class NotebotPlayer {
    /* Status */
    public static boolean playing = false;

    /* Some settings */
    public static boolean loop = false;
    public static boolean autoPlay = false;
    public static boolean autoPlayFilter = true;
    public static boolean autoPlayAntiDuplicate = true;
    public static int autoPlayTrailLength = 5;

    /* The loaded song */
    public static Song song;
    public static List<String> trail = new ArrayList<>();

    /* Map of noteblocks and their pitch around the player [blockpos:pitch] */
    private static Map<BlockPos, Integer> blockPitches = new HashMap<>();
    private static int timer = -10;
    private static int tuneDelay = 0;

    public static void loadSong() {
        blockPitches.clear();

        try {
            if (!mc.interactionManager.getCurrentGameMode().isSurvivalLike()) {
                mc.player.sendMessage(Text.literal("§cNot in Survival mode!"));

                return;
            } else if (song == null) {
                mc.player.sendMessage(Text.literal("§6No Song Loaded!, Use §c/notebot §6select to select a song."));

                return;
            }
        } catch (NullPointerException e) {
            return;
        }

        timer = -10;

        BlockPos playerEyePos = new BlockPos((int) mc.player.getEyePos().x, (int) mc.player.getEyePos().y, (int) mc.player.getEyePos().z);

        List<BlockPos> noteblocks = BlockPos.streamOutwards(
            playerEyePos, 4, 4, 4
        ).filter(
            NotebotPlayer::isNoteblock).map(BlockPos::toImmutable
        ).toList();

        for (Note note : song.requirements) {
            for (BlockPos pos : noteblocks) {
                if (blockPitches.containsKey(pos)) continue;

                int instrument = getInstrument(pos).ordinal();
                if (note.instrument == instrument && blockPitches.entrySet().stream().filter(e -> e.getValue() == note.pitch).noneMatch(e -> getInstrument(e.getKey()).ordinal() == instrument)) {
                    blockPitches.put(pos, note.pitch);
                    break;
                }
            }
        }

        int required = song.requirements.size();
        if (required > blockPitches.size()) {
            mc.player.sendMessage(Text.literal("§6Warning: Missing §c" + (required - blockPitches.size()) + " §6Noteblocks"));
        }
    }

    public static Instrument getInstrument(BlockPos pos) {
        if (!isNoteblock(pos)) return Instrument.HARP;

        return mc.world.getBlockState(pos).get(NoteBlock.INSTRUMENT);
    }

    public static boolean isNoteblock(BlockPos pos) {
        // Checks if this block is a noteblock and the noteblock can be played
        return mc.world.getBlockState(pos).getBlock() instanceof NoteBlock && mc.world.getBlockState(pos.up()).isAir();
    }

    public static void onTick(MinecraftClient client) {
        if (!playing) return;

        // Tune Noteblocks
        for (Entry<BlockPos, Integer> e : blockPitches.entrySet()) {
            int note = getNote(e.getKey());
            if (note == -1) continue;

            if (note != e.getValue()) {
                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(e.getKey(), 1), Direction.UP, e.getKey(), true));
                return;
            }
        }

        // Loop
        if (timer - 10 > song.length) {
            if (autoPlay) {
                if (autoPlayTrailLength > 0) {
                    trail.add(song.filename);

                    if (trail.size() > autoPlayTrailLength) {
                        mc.player.sendMessage(Text.literal("§6The trail is full. Cleaning..."));
                        trail.clear();
                        trail.add(song.filename);
                    }
                    mc.player.sendMessage(Text.literal("§6The trail is now: §c" + trail.toString()));
                }

                int retryTimes = 0;

                while (true) {
                    retryTimes++;
                    if (retryTimes > 20) {
                        mc.player.sendMessage(Text.literal("§cCannot find any song matches the condition. Disabling notebot..."));
                        playing = false;
                    }

                    File[] files = NotebotFileManager.getDir().resolve("notebot/").toFile().listFiles();
                    Path path = files[ThreadLocalRandom.current().nextInt(files.length)].toPath();

                    song = NotebotUtils.parse(path);

//                    TODO: Make this filter configurable
                    if (autoPlayFilter) {
                        if (!song.filename.startsWith("!")) continue;
                    }

                    if (autoPlayAntiDuplicate) {
                        if (trail.contains(song.filename)) {
                            mc.player.sendMessage(Text.literal("§6This song is played before. Picking a new one..."));
                            continue;
                        }
                    }
                    break;
                }

            } else if (loop) {
                timer = -10;
            }
        }

        if (timer == -10) {
            mc.player.sendMessage(Text.literal("§6Now Playing: §a" + song.filename));
        }

        timer++;

        Collection<Note> curNotes = song.notes.get(timer);

        if (curNotes.isEmpty()) return;

        for (Entry<BlockPos, Integer> e : blockPitches.entrySet()) {
            for (Note i : curNotes) {
                if (isNoteblock(e.getKey()) && (i.pitch == getNote(e.getKey())))
                    playBlock(e.getKey());
            }
        }
    }

    public static int getNote(BlockPos pos) {
        if (!isNoteblock(pos)) return -1;

        return mc.world.getBlockState(pos).get(NoteBlock.NOTE);
    }

    public static void playBlock(BlockPos pos) {
        if (!isNoteblock(pos)) return;

        mc.interactionManager.attackBlock(pos, Direction.UP);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    public static class Song {

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

    public static class Note {

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

}