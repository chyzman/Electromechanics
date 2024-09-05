package com.chyzman.electromechanics.data;

import com.chyzman.electromechanics.registries.RedstoneLogisticalBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EMTagGen {

    public static class Blocks extends FabricTagProvider.BlockTagProvider {

        public Blocks(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                    .add(RedstoneLogisticalBlocks.STERN_COPPER,
                            RedstoneLogisticalBlocks.OBSERVER_BUTTON,
                            RedstoneLogisticalBlocks.DETECTOR,
                            RedstoneLogisticalBlocks.ADVANCED_DETECTOR,
                            RedstoneLogisticalBlocks.LISTENER);

            this.getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                    .add(RedstoneLogisticalBlocks.STERN_COPPER,
                            RedstoneLogisticalBlocks.OBSERVER_BUTTON,
                            RedstoneLogisticalBlocks.DETECTOR,
                            RedstoneLogisticalBlocks.ADVANCED_DETECTOR,
                            RedstoneLogisticalBlocks.LISTENER);
        }
    }
}
