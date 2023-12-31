package com.chyzman.electromechanics.block.gate;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ImplBlockEntityProvider extends BlockEntityProvider {

    default boolean onSyncedBlockEvent(World world, BlockPos pos, int type, int data) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.onSyncedBlockEvent(type, data);
    }

    @Nullable
    default NamedScreenHandlerFactory createScreenHandlerFactory(World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof NamedScreenHandlerFactory factory ? factory : null;
    }

    /**
     * {@return the ticker if the given type and expected type are the same, or {@code null} if they are different}
     */
    @Nullable
    static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> validateTicker(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker
    ) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
}
