package com.chyzman.chyzyLogistics.mixin;

import net.minecraft.block.ObserverBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ObserverBlock.class)
public interface ObserverBlockAccessor {
    @Invoker("scheduleTick")
    void chyzylogistics$callScheduleTick(WorldAccess world, BlockPos pos);
}