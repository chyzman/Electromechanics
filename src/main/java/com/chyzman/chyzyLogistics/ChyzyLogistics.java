package com.chyzman.chyzyLogistics;

import com.chyzman.chyzyLogistics.block.detector.AdvancedDetectorBlockEntity;
import com.chyzman.chyzyLogistics.block.detector.DetectorBlockEntity;
import com.chyzman.chyzyLogistics.registries.ChyzyLogisticsRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;

import static com.chyzman.chyzyLogistics.util.ChyzyLogisticsRegistryHelper.id;

public class ChyzyLogistics implements ModInitializer {
    public static final String MODID = "chyzylogistics";

    public static final BlockEntityType<DetectorBlockEntity> DETECTOR_BLOCK_ENTITY = net.minecraft.registry.Registry.register(Registries.BLOCK_ENTITY_TYPE, id("detector"), FabricBlockEntityTypeBuilder.create(DetectorBlockEntity::new, ChyzyLogisticsRegistry.DETECTOR_BLOCK).build());
    public static final BlockEntityType<AdvancedDetectorBlockEntity> ADVANCED_DETECTOR_BLOCK_ENTITY = net.minecraft.registry.Registry.register(Registries.BLOCK_ENTITY_TYPE, id("advanced_detector"), FabricBlockEntityTypeBuilder.create(AdvancedDetectorBlockEntity::new, ChyzyLogisticsRegistry.ADVANCED_DETECTOR_BLOCK).build());

    @Override
    public void onInitialize() {
        ChyzyLogisticsRegistry.init();
        ServerEventListeners.init();
    }
}