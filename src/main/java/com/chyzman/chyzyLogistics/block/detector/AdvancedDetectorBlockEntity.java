package com.chyzman.chyzyLogistics.block.detector;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.mixin.BlockEntityAccessor;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AdvancedDetectorBlockEntity extends DetectorBlockEntity {

    private NbtPathArgumentType.NbtPath path;

    public AdvancedDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(ChyzyLogistics.ADVANCED_DETECTOR_BLOCK_ENTITY, pos, state);
        try {
            this.path = NbtPathArgumentType.nbtPath().parse(new StringReader(""));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public NbtPathArgumentType.NbtPath path() {
        return path;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public void path(NbtPathArgumentType.NbtPath path) {
        this.path = path;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(world, pos);
    }

    @Override
    public boolean tick(BlockEntity targetBlockEntity) {
        var returned = false;
        var nbt = new NbtCompound();
        ((BlockEntityAccessor) targetBlockEntity).chyzyLogistics$callWriteNbt(nbt);
        try {
            var value = path.get(nbt);
            if (previousValue != null && !previousValue.equals(value)) {
                returned = true;
            }
            previousValue = value;
        } catch (CommandSyntaxException ignored) {
            if (!previousValue.isEmpty()) returned = true;
            previousValue = List.of();
        }
        return returned;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("Path", path.toString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        try {
            this.path = NbtPathArgumentType.nbtPath().parse(new StringReader(nbt.getString("Path")));
        } catch (Exception ignored) {
        }
    }
}