package com.chyzman.chyzyLogistics.block.slime;

import com.chyzman.chyzyLogistics.data.SlimeTags;
import com.chyzman.chyzyLogistics.registries.SlimeBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SlimeBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Unique;

public class SlimeSlab extends SlabBlock {

    public static final MapCodec<SlimeSlab> CODEC = createCodec(SlimeSlab::new);

    public SlimeSlab(Settings settings) {
        super(settings);
    }

    @Override
    public MapCodec<? extends SlimeSlab> getCodec() {
        return CODEC;
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.bypassesLandingEffects()) {
            super.onLandedUpon(world, state, pos, entity, fallDistance);
        } else {
            entity.handleFallDamage(fallDistance, 0.0F, world.getDamageSources().fall());
        }
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        if (entity.bypassesLandingEffects()) {
            super.onEntityLand(world, entity);
        } else {
            this.bounce(entity);
        }
    }

    private void bounce(Entity entity) {
        Vec3d vec3d = entity.getVelocity();

        if (vec3d.y < 0.0) {
            double d = entity instanceof LivingEntity ? 1.0 : 0.8;
            entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
        }
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        double d = Math.abs(entity.getVelocity().y);

        if (d < 0.1 && !entity.bypassesSteppingEffects()) {
            double e = 0.4 + d * 0.2;
            entity.setVelocity(entity.getVelocity().multiply(e, 1.0, e));
        }

        super.onSteppedOn(world, pos, state, entity);
    }

    public static boolean isSlimeSlab(BlockState state){
        return state.isIn(SlimeTags.Blocks.SLIME_SLABS) && state.contains(SlabBlock.TYPE);
    }
}
