package com.chyzman.chyzyLogistics.client;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.client.utils.LangUtils;
import com.chyzman.chyzyLogistics.client.utils.TranslationInjectionEvent;
import com.chyzman.chyzyLogistics.registries.RedstoneWires;
import com.chyzman.chyzyLogistics.registries.SlimeBlocks;
import com.chyzman.chyzyLogistics.block.gate.GateBlock;
import com.chyzman.chyzyLogistics.util.Colored;
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

import java.util.HashMap;
import java.util.Map;

public class ChyzyLogisticsClient implements ClientModInitializer {

    private static final RenderLayer TRANSLUCENT = RenderLayer.getTranslucent();
    private static final RenderLayer CUTOUT = RenderLayer.getCutout();

    @Override
    public void onInitializeClient() {
        ClientEventListeners.init();

        ColoredVariantsModelLoader.init();

        BlockRenderLayerMap.INSTANCE.putBlock(Block.getBlockFromItem(SlimeBlocks.getSlimeSlabs().get(0)), TRANSLUCENT);

        Map<String, Block> variants = new HashMap<>();

        variants.putAll(SlimeBlocks.variantInfo());
        variants.putAll(RedstoneWires.variantInfo());

        SlimeBlocks.iterateVariants(block -> {
            BlockRenderLayerMap.INSTANCE.putBlock(block, TRANSLUCENT);
        });

        for (String s : variants.keySet()) {
            for (DyeColor value : DyeColor.values()) {
                var coloredBlock = Registries.BLOCK.get(new Identifier(ChyzyLogistics.MODID, value.asString() + "_" + s));

                ColorProviderRegistry.BLOCK.register((BlockColorProvider) coloredBlock, coloredBlock);
                ColorProviderRegistry.ITEM.register((ItemColorProvider) coloredBlock.asItem(), coloredBlock.asItem());
            }
        }

        RedstoneWires.iterateVariants(block -> {
            BlockRenderLayerMap.INSTANCE.putBlock(block, CUTOUT);
        });

        TranslationInjectionEvent.AFTER_LANGUAGE_LOAD.register(helper -> {
            helper.addBlock(SlimeBlocks.variantInfo().get("slime_slab"));

            SlimeBlocks.iterateVariants(helper::addBlock);
            RedstoneWires.iterateVariants(block -> {
                var name = LangUtils.toEnglishName(((Colored)block).getColor().getName() + "stone_dust");

                if(name.contains("red")) name = "Redderstone Dust";

                helper.addTranslation(block.getTranslationKey(), name);
            });
        });
        Registries.BLOCK.forEach(block -> {
            if (block instanceof GateBlock) {
                BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
            }
        });
    }
}