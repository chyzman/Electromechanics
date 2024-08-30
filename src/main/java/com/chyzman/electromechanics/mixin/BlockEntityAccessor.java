package com.chyzman.electromechanics.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockEntity.class)
public interface BlockEntityAccessor {
    @Invoker("writeNbt")
    void chyzyLogistics$callWriteNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup);
}