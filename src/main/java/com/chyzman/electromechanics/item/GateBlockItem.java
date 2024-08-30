package com.chyzman.electromechanics.item;

import com.chyzman.electromechanics.block.gate.GateBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class GateBlockItem extends BlockItem {

    public GateBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        var blockEntity = GateBlockEntity.createBlockEntity(BlockPos.ORIGIN, getBlock().getDefaultState());

        var itemStack = super.getDefaultStack();

        blockEntity.setStackNbt(itemStack, RegistryWrapper.WrapperLookup.of(Stream.of()));

        return itemStack;
    }
}
