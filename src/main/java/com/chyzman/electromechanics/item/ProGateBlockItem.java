package com.chyzman.electromechanics.item;

import com.chyzman.electromechanics.block.gate.ProGateBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ProGateBlockItem extends BlockItem {

    public ProGateBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        var blockEntity = ProGateBlockEntity.createBlockEntity(BlockPos.ORIGIN, getBlock().getDefaultState());

        var itemStack = super.getDefaultStack();

        blockEntity.setStackNbt(itemStack);

        return itemStack;
    }
}
