package com.chyzman.chyzyLogistics.block.redstone;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ColorGatherer {

    @Nullable Vec3d getColor(World world, BlockPos pos, BlockState state);

    default int getArgbColor(World world, BlockPos pos, BlockState state) {
        var rgbVec = getColor(world, pos, state);

        if(rgbVec == null) return -1;

        return MathHelper.packRgb((float) rgbVec.getX(), (float) rgbVec.getY(), (float) rgbVec.getZ());
    }
}
