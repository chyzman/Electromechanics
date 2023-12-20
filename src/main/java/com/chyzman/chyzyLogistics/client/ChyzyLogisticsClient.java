package com.chyzman.chyzyLogistics.client;

import com.chyzman.chyzyLogistics.block.gate.GateBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.registry.Registries;

public class ChyzyLogisticsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEventListeners.init();
        Registries.BLOCK.forEach(block -> {
            if (block instanceof GateBlock) {
                BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
            }
        });
    }
}