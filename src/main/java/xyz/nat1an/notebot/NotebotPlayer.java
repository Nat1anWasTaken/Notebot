/*
This file is part of Notebot.
Notebot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
Notebot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with Notebot. If not, see <https://www.gnu.org/licenses/>.
*/

package xyz.nat1an.notebot;

import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import xyz.nat1an.notebot.types.Note;
import xyz.nat1an.notebot.types.Song;
import xyz.nat1an.notebot.utils.NotebotFileManager;
import xyz.nat1an.notebot.utils.NotebotUtils;

import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

import static xyz.nat1an.notebot.Notebot.mc;

public class NotebotPlayer {
    /* Status */
    public static boolean playing = false;

    /* Some settings */
    public static boolean loop = false;

    /* The loaded song */
    public static Song song;
    public static List<String> trail = new ArrayList<>();
    public static List<String> queue = new ArrayList<>();

    /* Map of noteblocks and their pitch around the player [blockpos:pitch] */
    private static Map<BlockPos, Integer> blockPitches = new HashMap<>();
    private static int timer = -10;
    private static int tuneDelay = 0;

    public static int getNote(BlockPos pos) {
        if (!isNoteblock(pos)) return -1;

        return mc.world.getBlockState(pos).get(NoteBlock.NOTE);
    }

    public static void playBlock(BlockPos pos) {
        if (!isNoteblock(pos)) return;

        mc.interactionManager.attackBlock(pos, Direction.UP);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    public static Instrument getInstrument(BlockPos pos) {
        if (!isNoteblock(pos)) return Instrument.HARP;

        return mc.world.getBlockState(pos).get(NoteBlock.INSTRUMENT);
    }

    public static boolean isNoteblock(BlockPos pos) {
        // Checks if this block is a noteblock and the noteblock can be played
        return mc.world.getBlockState(pos).getBlock() instanceof NoteBlock && mc.world.getBlockState(pos.up()).isAir();
    }

    public static void stop() {
        playing = false;
        song = null;
        blockPitches.clear();
        timer = -10;
        tuneDelay = 0;
    }

    public static boolean loadSong() {
        blockPitches.clear();

        try {
            if (!mc.interactionManager.getCurrentGameMode().isSurvivalLike()) {
                mc.player.sendMessage(Text.literal("§cNot in Survival mode!"));

                return false;
            } else if (song == null) {
                mc.player.sendMessage(Text.literal("§6No song in queue!, Use §c/notebot queue add §6to add a song."));

                return false;
            }
        } catch (NullPointerException e) {
            return false;
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

        return true;
    }

    public static void onTick(MinecraftClient client) {
        if (!playing) return;

        if (song == null) {
            if (queue.isEmpty()) {
                mc.player.sendMessage(Text.literal("§cYou have no songs in your queue!"));
                stop();
                return;
            }

            NotebotPlayer.song = NotebotUtils.parse(
                    NotebotFileManager.getDir().resolve(
                            "songs/" + NotebotPlayer.queue.remove(0)
                    )
            );

            loadSong();
        }

        // Tune Noteblocks
        for (Entry<BlockPos, Integer> e : blockPitches.entrySet()) {
            int note = getNote(e.getKey());
            if (note == -1)
                continue;

            if (note != e.getValue()) {
                if (tuneDelay < 5) {
                    tuneDelay++;
                    return;
                }

                int neededNote = e.getValue() < note ? e.getValue() + 25 : e.getValue();
                int reqTunes = Math.min(25, neededNote - note);
                for (int i = 0; i < reqTunes; i++)
                    mc.interactionManager.interactBlock(mc.player,
                            Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(e.getKey(), 1), Direction.UP, e.getKey(), true));

                tuneDelay = 0;

                return;
            }
        }

        // Loop
        if (timer - 10 > song.length) {
            if (loop) {
                timer = -10;
            } else if (!queue.isEmpty()) {
                song = null;
                return;
            } else {
                mc.player.sendMessage(Text.literal("§6The queue is empty, stopping..."));
                stop();
                return;
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
                if (isNoteblock(e.getKey()) && (i.pitch == getNote(e.getKey())) && (i.instrument == getInstrument(e.getKey()).ordinal()))
                    playBlock(e.getKey());
            }
        }
    }

}