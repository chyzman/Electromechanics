package com.chyzman.electromechanics;

import com.chyzman.electromechanics.block.ListenerBlock;
import com.chyzman.electromechanics.mixin.ObserverBlockAccessor;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.FacingBlock;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ServerEventListeners {
    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            var triggered = false;
            if (!player.isSneaking()) {
                for (Direction direction : Direction.values()) {
                    if (!(world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof ListenerBlock)) {
                        if (world.getBlockState(hitResult.getBlockPos().offset(direction)).getBlock() instanceof ListenerBlock &&
                                world.getBlockState(hitResult.getBlockPos().offset(direction)).get(FacingBlock.FACING) == direction.getOpposite()) {
                            triggered = true;
                            chainListener(world, hitResult.getBlockPos().offset(direction));
                        } else if (world.getBlockState(hitResult.getBlockPos().offset(direction, 2)).getBlock() instanceof ListenerBlock &&
                                world.getBlockState(hitResult.getBlockPos().offset(direction, 2)).get(FacingBlock.FACING) == direction.getOpposite() &&
                                world.getBlockState(hitResult.getBlockPos().offset(direction, 3)).getBlock() instanceof ListenerBlock) {
                            triggered = true;
                            chainListener(world, hitResult.getBlockPos().offset(direction, 3));
                        }
                    }
                }
            }
            return (triggered && world.isClient) ? ActionResult.SUCCESS : ActionResult.PASS;
        });
    }

    public static boolean chainListener(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() instanceof ListenerBlock listenerBlock) {
            chainListener(world, pos.offset(world.getBlockState(pos).get(FacingBlock.FACING).getOpposite()));
            ((ObserverBlockAccessor) listenerBlock).chyzylogistics$callScheduleTick(world, pos);
        }
        return false;
    }
}