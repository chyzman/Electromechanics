package com.chyzman.electromechanics;

import com.chyzman.electromechanics.block.ListenerBlock;
import com.chyzman.electromechanics.mixin.ObserverBlockAccessor;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.FacingBlock;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashSet;

public class ServerEventListeners {
    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!player.isSneaking()) {
                for (Direction direction : Direction.values()) {
                    if (!(world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof ListenerBlock)) {
                        var pos = hitResult.getBlockPos().offset(direction);
                        if (world.getBlockState(pos).getBlock() instanceof ListenerBlock && world.getBlockState(pos).get(FacingBlock.FACING) == direction.getOpposite()) {
                            if (world.isClient) return ActionResult.SUCCESS;
                            var chained = new HashSet<BlockPos>();
                            for (int i = 0; i < Math.pow(2, 13); i++) {
                                if (world.getBlockState(pos).getBlock() instanceof ListenerBlock listenerBlock && !chained.contains(pos)) {
                                    chained.add(pos);
                                    ((ObserverBlockAccessor) listenerBlock).chyzylogistics$callScheduleTick(world, pos);
                                    pos = pos.offset(world.getBlockState(pos).get(FacingBlock.FACING).getOpposite());
                                } else {
                                    return ActionResult.SUCCESS;
                                }
                            }
                        }
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}