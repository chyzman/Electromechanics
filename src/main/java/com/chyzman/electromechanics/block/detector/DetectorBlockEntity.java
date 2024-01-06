package com.chyzman.electromechanics.block.detector;

import com.chyzman.electromechanics.Electromechanics;
import com.chyzman.electromechanics.mixin.BlockEntityAccessor;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class DetectorBlockEntity extends BlockEntity {

    protected List<?> previousValue = null;

    public DetectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public DetectorBlockEntity(BlockPos pos, BlockState state) {
        super(Electromechanics.DETECTOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(world, pos);
    }

    public boolean tick(BlockEntity targetBlockEntity) {
        var returned = false;
        var nbt = new NbtCompound();
        ((BlockEntityAccessor) targetBlockEntity).chyzyLogistics$callWriteNbt(nbt);
        var value = List.of(nbt);
        if (previousValue != null && !previousValue.equals(value)) {
            returned = true;
        }
        previousValue = value;
        return returned;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }
}