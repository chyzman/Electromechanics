package com.chyzman.electromechanics.util;

import com.mojang.logging.LogUtils;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import io.wispforest.owo.serialization.format.nbt.NbtEndec;
import io.wispforest.owo.serialization.util.MapCarrier;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class BlockEntityOps {

    private static final KeyedEndec<NbtCompound> BLOCK_ENTITY_DATA = NbtEndec.COMPOUND.keyed("BlockEntityTag", new NbtCompound());

    public static final Logger LOGGER = LogUtils.getLogger();

    @Nullable
    public static <T extends BlockEntity> T createFromTypeAndStack(BlockEntityType<T> type, ItemStack stack){
        if(!(stack.getItem() instanceof BlockItem blockItem)) return null;

        return BlockEntityOps.createAndReadNbt(type, BlockPos.ORIGIN, blockItem.getBlock().getDefaultState(), stack);
    }

    @Nullable
    public static <T extends BlockEntity> T createAndReadNbt(BlockEntityType<T> type, BlockPos pos, BlockState state, MapCarrier carrier) {
        Identifier id = Registries.BLOCK_ENTITY_TYPE.getId(type);

        T blockEntity;

        try {
            blockEntity = type.instantiate(pos, state);
        } catch (Throwable var5) {
            LOGGER.error("Failed to create block entity {}", id, var5);
            return null;
        }

        if(blockEntity == null) {
            LOGGER.error("Failed to create valid block entity {}", id);
            return null;
        }

        return readFromCarrier(blockEntity, carrier);
    }

    @Nullable
    public static <T extends BlockEntity> T readFromCarrier(T blockEntity, MapCarrier carrier) {
        Identifier id = Registries.BLOCK_ENTITY_TYPE.getId(blockEntity.getType());

        if(carrier == null) {
            LOGGER.error("Failed to load data block entity due to null data {}", id);
            return null;
        }

        try {
            blockEntity.readNbt(carrier.get(BLOCK_ENTITY_DATA));
        } catch (Throwable var4xx) {
            LOGGER.error("Failed to load data for block entity {}", id, var4xx);
            return null;
        }

        return blockEntity;
    }
}
