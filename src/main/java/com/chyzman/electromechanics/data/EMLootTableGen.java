package com.chyzman.electromechanics.data;

import com.chyzman.electromechanics.registries.RedstoneLogisticalBlocks;
import com.chyzman.electromechanics.registries.RedstoneWires;
import com.chyzman.electromechanics.registries.SlimeBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class EMLootTableGen extends FabricBlockLootTableProvider {

    protected EMLootTableGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        RedstoneWires.getDusts().forEach(this::addItem);

        RedstoneLogisticalBlocks.getBlockItems().forEach(this::addItem);

        SlimeBlocks.getSlimeBlocks().forEach(this::addItem);

        SlimeBlocks.getSlimeSlabs().forEach(this::addItem);
    }

    private void addItem(Item item) {
        if(item instanceof BlockItem blockItem) {
            this.addDrop(blockItem.getBlock(), blockItem);
        }
    }
}
