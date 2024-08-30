package com.chyzman.electromechanics.mixin;

import com.chyzman.electromechanics.compat.SlimeBlockBoots;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class Accessories_BlockMixin {

    @Inject(method = "onLandedUpon", at = @At("HEAD"), cancellable = true)
    private void electromechanics$adjustLandingBehavior(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if(!(entity instanceof LivingEntity living) || entity.bypassesLandingEffects()) return;

        var capability = living.accessoriesCapability();

        if(capability == null) return;

        var equippedSlimeBoots = capability.getFirstEquipped((stack) -> stack.isIn(SlimeBlockBoots.SLIME_BLOCK_TAG));

        if(equippedSlimeBoots == null) return;

        if (equippedSlimeBoots.stack().getCount() >= 2) {
            entity.handleFallDamage(fallDistance, 0.0F, world.getDamageSources().fall());

            ci.cancel();
        }
    }

    @Inject(method = "onEntityLand", at = @At("HEAD"), cancellable = true)
    private void electromechanics$adjustEntityLanding(BlockView world, Entity entity, CallbackInfo ci) {
        if(!(entity instanceof LivingEntity living) || entity.bypassesLandingEffects() ) return;

        var capability = living.accessoriesCapability();

        if(capability == null) return;

        var equippedSlimeBoots = capability.getFirstEquipped((stack) -> stack.isIn(SlimeBlockBoots.SLIME_BLOCK_TAG));

        if(equippedSlimeBoots == null) return;

        if (equippedSlimeBoots.stack().getCount() >= 2) {
            this.bounce(entity);

            ci.cancel();
        }
    }

    @Unique
    private void bounce(Entity entity) {
        Vec3d vec3d = entity.getVelocity();

        if (vec3d.y < 0.0) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
        }
    }
}
