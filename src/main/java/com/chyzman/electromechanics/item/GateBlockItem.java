package com.chyzman.electromechanics.item;

import com.chyzman.electromechanics.block.gate.GateBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class GateBlockItem extends BlockItem {

    public GateBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        var blockEntity = GateBlockEntity.createBlockEntity(BlockPos.ORIGIN, getBlock().getDefaultState());

        var itemStack = super.getDefaultStack();

        blockEntity.setStackNbt(itemStack);

        return itemStack;
    }
}
