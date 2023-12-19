package com.chyzman.chyzyLogistics.block.detector;

import com.chyzman.chyzyLogistics.mixin.ObserverBlockAccessor;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class DetectorBlock extends ObserverBlock implements BlockEntityProvider {

    public DetectorBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DetectorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {return null;}
        return (world1, pos, state1, blockEntity) -> {
            var targetBlockEntity = world1.getBlockEntity(pos.offset(state1.get(FACING)));
            if (blockEntity instanceof DetectorBlockEntity detectorBlockEntity && targetBlockEntity != null) {
                if (detectorBlockEntity.tick(targetBlockEntity)) {
                    ((ObserverBlockAccessor) this).chyzylogistics$callScheduleTick(world, pos);
                }
            }
        };
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state;
    }
}