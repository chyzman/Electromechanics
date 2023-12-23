package com.chyzman.chyzyLogistics.block.redstone;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

public class RedstoneEvents {

    public static final Event<ColorGatherer> PARTICLE_COLOR_GATHERER_EVENT = EventFactory.createArrayBacked(
            ColorGatherer.class,
            (invokers) -> (world, pos, state) -> {
                for (ColorGatherer invoker : invokers) {
                    Vec3d color = invoker.getColor(world, pos, state);

                    if(color != null) return color;
                }

                return null;
            }
    );

    public static final Event<ValidConnection> SHOULD_CANCEl_CONNECTION = EventFactory.createArrayBacked(
            ValidConnection.class,
            (invokers) -> (world, pos, state, pos2, state2) -> {
                for (ValidConnection invoker : invokers) {
                    if(invoker.shouldCancel(world, pos, state, pos2, state2)) return true;
                }

                return false;
            }
    );

    public interface ValidConnection {
        boolean shouldCancel(BlockView world, BlockPos pos, BlockState state, BlockPos pos2, BlockState state2);
    }
}
