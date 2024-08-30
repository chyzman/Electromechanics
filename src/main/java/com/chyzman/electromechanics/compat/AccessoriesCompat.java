package com.chyzman.electromechanics.compat;

import com.chyzman.electromechanics.registries.SlimeBlocks;
import io.wispforest.accessories.api.AccessoriesAPI;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;

public class AccessoriesCompat {

    public static void init() {
        AccessoriesAPI.registerAccessory(Blocks.SLIME_BLOCK.asItem(), SlimeBlockBoots.INSTANCE);

        for (var slimeBlock : SlimeBlocks.getSlimeBlocks()) {
            AccessoriesAPI.registerAccessory(slimeBlock.asItem(), SlimeBlockBoots.INSTANCE);
        }
    }
}
