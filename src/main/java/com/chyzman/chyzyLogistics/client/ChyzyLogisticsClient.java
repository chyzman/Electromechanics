package com.chyzman.chyzyLogistics.client;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.client.utils.TranslationInjectionEvent;
import com.chyzman.chyzyLogistics.registries.SlimeBlocks;
import com.chyzman.chyzyLogistics.block.gate.GateBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;

public class ChyzyLogisticsClient implements ClientModInitializer {

    private static final RenderLayer TRANSLUCENT = RenderLayer.getTranslucent();

    @Override
    public void onInitializeClient() {
        ClientEventListeners.init();

        ColoredSlimeVariantsModelLoader.init();

        BlockRenderLayerMap.INSTANCE.putBlock(Block.getBlockFromItem(SlimeBlocks.getSlimeSlabs().get(0)), TRANSLUCENT);

        for (String s : SlimeBlocks.variantInfo().keySet()) {
            for (DyeColor value : DyeColor.values()) {
                var coloredBlock = Registries.BLOCK.get(new Identifier(ChyzyLogistics.MODID, value.asString() + "_" + s));

                BlockRenderLayerMap.INSTANCE.putBlock(coloredBlock, TRANSLUCENT);

                ColorProviderRegistry.BLOCK.register((BlockColorProvider) coloredBlock, coloredBlock);
                ColorProviderRegistry.ITEM.register((ItemColorProvider) coloredBlock.asItem(), coloredBlock.asItem());
            }
        }

        TranslationInjectionEvent.AFTER_LANGUAGE_LOAD.register(helper -> {
            helper.addBlock(SlimeBlocks.variantInfo().get("slime_slab"));

            SlimeBlocks.iterateVariants(helper::addBlock);
        });
        Registries.BLOCK.forEach(block -> {
            if (block instanceof GateBlock) {
                BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
            }
        });
    }
}