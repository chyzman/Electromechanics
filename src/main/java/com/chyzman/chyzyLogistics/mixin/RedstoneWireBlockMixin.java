package com.chyzman.chyzyLogistics.mixin;

import com.chyzman.chyzyLogistics.block.gate.GateBlock;
import com.chyzman.chyzyLogistics.block.gate.MonoGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin {

    @Inject(method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z",
            at = @At("HEAD"), cancellable = true)
    private static void dontConnectToMyDamnGates(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof GateBlock gateBlock && !gateBlock.wireConnectsTo(state, dir)) {
            cir.setReturnValue(false);
        }
    }
}