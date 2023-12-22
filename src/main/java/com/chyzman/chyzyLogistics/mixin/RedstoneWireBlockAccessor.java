package com.chyzman.chyzyLogistics.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RedstoneWireBlock.class)
public interface RedstoneWireBlockAccessor {
    @Invoker("addPoweredParticles")
    void chyzy$addPoweredParticles(World world, Random random, BlockPos pos, Vec3d color, Direction direction, Direction direction2, float f, float g);

    @Accessor("wiresGivePower")
    void chyzy$wiresGivePower(boolean wiresGivePower);

    @Invoker("increasePower")
    int chyzy$increasePower(BlockState state);
}
