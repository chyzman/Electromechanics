package com.chyzman.chyzyLogistics;

import com.chyzman.chyzyLogistics.block.ListenerBlock;
import com.chyzman.chyzyLogistics.mixin.ObserverBlockAccessor;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.FacingBlock;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;

public class ServerEventListeners {
    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            var triggered = false;
            if (!player.isSneaking()) {
                for (Direction direction : Direction.values()) {
                    if (world.getBlockState(hitResult.getBlockPos().offset(direction)).getBlock() instanceof ListenerBlock listenerBlock &&
                            world.getBlockState(hitResult.getBlockPos().offset(direction)).get(FacingBlock.FACING) == direction.getOpposite()) {
                        triggered = true;
                        ((ObserverBlockAccessor) listenerBlock).chyzylogistics$callScheduleTick(world, hitResult.getBlockPos().offset(direction));
                    }
                }
            }
            return triggered ? ActionResult.SUCCESS : ActionResult.PASS;
        });
    }
}