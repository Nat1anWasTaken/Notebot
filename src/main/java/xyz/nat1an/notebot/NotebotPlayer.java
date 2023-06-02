/*
This file is part of Notebot.
Notebot is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
Notebot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with Notebot. If not, see <https://www.gnu.org/licenses/>.
*/

package xyz.nat1an.notebot;

import net.minecraft.block.Material;
import net.minecraft.block.Block;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
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
/*
    public static Instrument getInstrument(BlockPos pos) {
        if (!isNoteblock(pos)) return Instrument.HARP;
        return mc.world.getBlockState(pos).get(NoteBlock.INSTRUMENT);
    }
*/
    public static Instrument getInstrumentUnderneath(BlockPos pos) {
        if (!isNoteblock(pos)) return Instrument.HARP;

        // Retrieve the block underneath
        BlockPos posUnderneath = pos.down();
        Block blockUnderneath = mc.world.getBlockState(posUnderneath).getBlock();

        // Return the instrument associated with the block underneath
        return blockToInstrument(blockUnderneath);
    }

    public static Instrument blockToInstrument(Block block) {

        // Specific block checks
        Identifier blockId = Registries.BLOCK.getId(block);
        String blockIdString = blockId.toString();
        Instrument instrument = Instrument.HARP;  // Default to Harp for any other block


        if (blockIdString.equals("minecraft:dirt" ) || blockIdString.equals("minecraft:air")) {
            return Instrument.HARP;
        } else if (blockIdString.equals("minecraft:clay")) {
            return Instrument.FLUTE;
        } else if (blockIdString.equals("minecraft:gold_block")) {
            return Instrument.BELL;
        } else if (blockIdString.equals("minecraft:packed_ice")) {
            return Instrument.CHIME;
        } else if (blockIdString.equals("minecraft:bone_block")) {
            return Instrument.XYLOPHONE;
        } else if (blockIdString.equals("minecraft:iron_block")) {
            return Instrument.IRON_XYLOPHONE;
        } else if (blockIdString.equals("minecraft:soul_sand")) {
            return Instrument.COW_BELL;
        } else if (blockIdString.equals("minecraft:pumpkin")) {
            return Instrument.DIDGERIDOO;
        } else if (blockIdString.equals("minecraft:emerald_block")) {
            return Instrument.BIT;
        } else if (blockIdString.equals("minecraft:hay_block")) {
            return Instrument.BANJO;
        } else if (blockIdString.equals("minecraft:glowstone")) {
            return Instrument.PLING;
        } else if (blockIdString.equals("minecraft:sand") || blockIdString.equals("minecraft:gravel") || blockIdString.equals("minecraft:concrete_powder")) {
            return Instrument.SNARE;
        } else if (Arrays.asList("minecraft:stone", "minecraft:cobblestone", "minecraft:blackstone", "minecraft:netherrack", "minecraft:nylium", "minecraft:obsidian",
                "minecraft:quartz", "minecraft:sandstone", "minecraft:ores", "minecraft:bricks", "minecraft:corals",
                "minecraft:respawn_anchor", "minecraft:bedrock", "minecraft:concrete").contains(blockIdString)) {
            return Instrument.BASEDRUM;
        } else if (blockIdString.equals("minecraft:glass")) {
            return Instrument.HAT;
        }


        Material material = block.getDefaultState().getMaterial();

        // Check for blocks with specific materials
        if (material.equals(Material.WOOD)) {
            return Instrument.BASS;
        }
        if (material.equals(Material.WOOL)) {
            return Instrument.GUITAR;
        }
        if (material.equals(Material.GLASS)) {
            return Instrument.HAT;
        }
        if (material.equals(Material.STONE)) {
            return Instrument.BASEDRUM;
        }



        return instrument;
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
                playerEyePos, 5, 5, 5
        ).filter(
                NotebotPlayer::isNoteblock).map(BlockPos::toImmutable
        ).toList();

        HashMap<Instrument, Integer> requiredInstruments = new HashMap<>();
        HashMap<Instrument, Integer> foundInstruments = new HashMap<>();

        for (Note note : song.requirements) {
            Instrument instrument = Instrument.values()[note.instrument];
            requiredInstruments.put(instrument, requiredInstruments.getOrDefault(instrument, 0) + 1);
            for (BlockPos pos : noteblocks) {
                if (blockPitches.containsKey(pos)) continue;

                Instrument blockInstrument = getInstrumentUnderneath(pos);
                if (note.instrument == blockInstrument.ordinal() && blockPitches.entrySet().stream().filter(e -> e.getValue() == note.pitch).noneMatch(e -> getInstrumentUnderneath(e.getKey()).ordinal() == blockInstrument.ordinal())) {
                    blockPitches.put(pos, note.pitch);
                    foundInstruments.put(blockInstrument, foundInstruments.getOrDefault(blockInstrument, 0) + 1);
                    break;
                }
            }
        }

        for (Instrument instrument : requiredInstruments.keySet()) {
            int requiredCount = requiredInstruments.get(instrument);
            int foundCount = foundInstruments.getOrDefault(instrument, 0);
            int missingCount = requiredCount - foundCount;

            if (missingCount > 0) {
                mc.player.sendMessage(Text.literal("§6Warning: Missing §c" + missingCount + " §6" + instrument + " Noteblocks"));
            }
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
                if (isNoteblock(e.getKey()) && (i.pitch == getNote(e.getKey())) && (i.instrument == getInstrumentUnderneath(e.getKey()).ordinal()))
                    playBlock(e.getKey());
            }
        }
    }

}
