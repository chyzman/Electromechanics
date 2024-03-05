package com.chyzman.electromechanics.client;

import com.chyzman.electromechanics.Electromechanics;
import com.chyzman.electromechanics.block.gate.GateBlock;
import com.chyzman.electromechanics.block.gate.GateBlockEntity;
import com.chyzman.electromechanics.block.redstone.RedstoneEvents;
import com.chyzman.electromechanics.client.be.GateBlockEntityRender;
import com.chyzman.electromechanics.client.be.GateBlockItemRender;
import com.chyzman.electromechanics.client.utils.LangUtils;
import com.chyzman.electromechanics.client.utils.TranslationInjectionEvent;
import com.chyzman.electromechanics.item.GateBlockItem;
import com.chyzman.electromechanics.registries.RedstoneLogisticalBlocks;
import com.chyzman.electromechanics.registries.RedstoneWires;
import com.chyzman.electromechanics.registries.SlimeBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ElectromechanicsClient implements ClientModInitializer {

    private static final RenderLayer TRANSLUCENT = RenderLayer.getTranslucent();
    private static final RenderLayer CUTOUT = RenderLayer.getCutout();

    @Override
    public void onInitializeClient() {
        ClientEventListeners.init();

        GateModelLoader.init();

        BlockEntityRendererFactories.register(GateBlockEntity.getBlockEntityType(), GateBlockEntityRender::new);

        for (Item item : RedstoneLogisticalBlocks.getBlockItems()) {
            if(!(item instanceof GateBlockItem blockItem)) continue;

            BuiltinItemRendererRegistry.INSTANCE.register(blockItem, new GateBlockItemRender());

            BlockRenderLayerMap.INSTANCE.putBlock(blockItem.getBlock(), CUTOUT);
        }

        ColoredVariantsModelLoader.init();

        BlockRenderLayerMap.INSTANCE.putBlock(Block.getBlockFromItem(SlimeBlocks.getSlimeSlabs().get(0)), TRANSLUCENT);

        SlimeBlocks.iterateVariants(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, TRANSLUCENT));

        for (String variant : SlimeBlocks.variantInfo().keySet()) {
            for (DyeColor value : DyeColor.values()) {
                var coloredBlock = Registries.BLOCK.get(new Identifier(Electromechanics.MODID, value.asString() + "_" + variant));

                ColorProviderRegistry.BLOCK.register((BlockColorProvider) coloredBlock, coloredBlock);
                ColorProviderRegistry.ITEM.register((ItemColorProvider) coloredBlock.asItem(), coloredBlock.asItem());
            }
        }

        for(var entry : RedstoneWires.variantInfo().entrySet()){
            var variant = entry.getKey();

            BlockColorProvider provider = (state, world, pos, tintIndex) -> {
                var vec3d = RedstoneWires.getColor(state);

                if(vec3d == null) return -1;

                return MathHelper.packRgb((float)vec3d.getX(), (float)vec3d.getY(), (float)vec3d.getZ());
            };

            for (DyeColor value : DyeColor.values()) {
                var coloredBlock = Registries.BLOCK.get(new Identifier(Electromechanics.MODID, value.asString() + "_" + variant));

                ColorProviderRegistry.BLOCK.register(provider, coloredBlock);
                ColorProviderRegistry.ITEM.register((ItemColorProvider) coloredBlock.asItem(), coloredBlock.asItem());
            }
        }

        RedstoneWires.iterateVariants(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, CUTOUT));

        RedstoneEvents.PARTICLE_COLOR_GATHERER_EVENT.register((world, pos, state) -> RedstoneWires.getColor(state));

        TranslationInjectionEvent.AFTER_LANGUAGE_LOAD.register(helper -> {
            helper.addBlock(SlimeBlocks.variantInfo().get("slime_slab"));

            SlimeBlocks.iterateVariants(helper::addBlock);

            RedstoneWires.iterateVariants(block -> {
                var name = LangUtils.toEnglishName(RedstoneWires.getDyeColor(block).getName() + "stone_dust");

                if(name.contains("Red")) name = "Redderstone Dust";

                helper.addTranslation(block.getTranslationKey(), name);
            });

            for (Item item : RedstoneLogisticalBlocks.getBlockItems()) {
                if(!(item instanceof BlockItem blockItem)) continue;

                var block = blockItem.getBlock();

                if(!(block instanceof GateBlock)) continue;

                helper.addBlock(blockItem.getBlock());
            }
        });
    }
}