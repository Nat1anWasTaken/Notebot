package xyz.nat1an.notebot.events;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AttackBlockEvent implements AttackBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        final BlockState blockState = world.getBlockState(pos);

        player.sendMessage(Text.literal("Hey! You hit a  " + blockState.getBlock().getName().getString() + "!"));

        return ActionResult.PASS;
    }
}
