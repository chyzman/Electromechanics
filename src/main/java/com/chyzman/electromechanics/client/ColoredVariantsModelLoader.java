package com.chyzman.electromechanics.client;

import com.chyzman.electromechanics.Electromechanics;
import com.chyzman.electromechanics.mixin.ModelLoaderAccessor;
import com.chyzman.electromechanics.registries.RedstoneWires;
import com.chyzman.electromechanics.registries.SlimeBlocks;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.DelegatingUnbakedModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class ColoredVariantsModelLoader implements ModelResolver, BlockStateResolver  {

    public static final ColoredVariantsModelLoader INSTANCE = new ColoredVariantsModelLoader();

    private static final Map<String, Identifier> BLOCKSTATE_ID_CACHE = new HashMap<>();

    private static final Map<String, Block> variants = new HashMap<>();

    public static void init(){
        var STATIC_DEFS = ModelLoaderAccessor.gelatin$getSTATIC_DEFINITIONS();

        Map<Identifier, StateManager<Block, BlockState>> mutableMap = new LinkedHashMap<>(STATIC_DEFS);

        variants.putAll(SlimeBlocks.variantInfo());
        variants.putAll(RedstoneWires.variantInfo());

        for (var entry : variants.entrySet()) {
            Block defaultEntry = entry.getValue();

            Identifier baseId;

            if(entry.getKey().equals("redstone_wire")){
                baseId = new Identifier(entry.getKey());
            } else {
                baseId = new Identifier(Electromechanics.MODID, "colored_" + entry.getKey());
            }

            Collection<Property<?>> properties = defaultEntry.getStateManager().getProperties();

            StateManager<Block, BlockState> customStateManager = new StateManager.Builder<Block, BlockState>(defaultEntry)
                    .add(properties.toArray(new Property[0]))
                    .build(Block::getDefaultState, BlockState::new);

            customStateManager.getStates().forEach(state -> {
                ModelIdentifier identifier = BlockModels.getModelId(baseId, state);

                BLOCKSTATE_ID_CACHE.put(baseId + identifier.getVariant(), identifier);
            });

            mutableMap.put(baseId, customStateManager);
        }

        ModelLoaderAccessor.gelatin$setSTATIC_DEFINITIONS(ImmutableMap.copyOf(mutableMap));

        //---------------------------------

        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.resolveModel().register(INSTANCE);

            for (var entry : variants.entrySet()) {
                String variant = entry.getKey();

                for (DyeColor dyeColor : DyeColor.values()) {
                    pluginContext.registerBlockStateResolver(
                            SlimeBlocks.getColoredVariant(dyeColor, variant),
                            ColoredVariantsModelLoader.INSTANCE
                    );
                }
            }
        });

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public void reload(ResourceManager manager) {
                        ITEMS_MODEL_CACHE.clear();
                    }

                    @Override
                    public Identifier getFabricId() {
                        return new Identifier(Electromechanics.MODID, "reload_event");
                    }
                });
    }

    //----------------------------------------------------------------------------------------------------------------------

    private static final Map<String, Identifier> ITEMS_MODEL_CACHE = new HashMap<>();

    @Override
    @Nullable
    public UnbakedModel resolveModel(ModelResolver.Context context) {
        Identifier id = context.id();

        // Is the model being loaded Minecraft version
        if(id.getNamespace().equals("minecraft")) return null;

        // Is Item Model variant
        if (!id.getPath().contains("item/")) return null;

        String itemPath = id.getPath().replace("item/", "");

        Identifier baseModelId = null;

        for (var entry : variants.entrySet()) {
            String variant = entry.getKey();

            if (!SlimeBlocks.isVariant(id, variant)) continue;

            // Check if the model attempting to be resolved is the default of the colored variant
            if(Registries.ITEM.getId(entry.getValue().asItem()).getPath().equals(itemPath)) break;

            baseModelId = new Identifier(Electromechanics.MODID, "colored_" + variant);

            break;
        }

        if (baseModelId == null) return null;

        UnbakedModel model;

        String key = baseModelId + "/item";

        // If such should be loaded from Json as it is a base model or the needed model isn't loaded
        if (ITEMS_MODEL_CACHE.get(key) == null || itemPath.contains("colored")) {
            Identifier modelId = new Identifier(baseModelId.getNamespace(), "item/" + baseModelId.getPath());

            model = loadItemModel(context, modelId);

            ITEMS_MODEL_CACHE.put(key, modelId);
        } else {
            model = new DelegatingUnbakedModel(ITEMS_MODEL_CACHE.get(key));
        }

        return model;
    }

    public static UnbakedModel loadItemModel(ModelResolver.Context context, Identifier redirectID) {
        try {
            return ((ModelLoaderAccessor) context.loader()).gelatin$LoadModelFromJson(redirectID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //----------------------------------------------------------------------------------------------------------------------

    @Override
    public void resolveBlockStates(BlockStateResolver.Context context) {
        Block block = context.block();

        for (var state : block.getStateManager().getStates()) {
            ModelIdentifier modelId = BlockModels.getModelId(state);

            if(Objects.equals(modelId.getPath(), "slime_slab")) return;

            for (var entry : variants.entrySet()) {
                String variant = entry.getKey();

                if (!SlimeBlocks.isVariant(Registries.BLOCK.getId(block), variant)) continue;

                String key;

                if(entry.getKey().equals("redstone_wire")){
                    key = "minecraft" + ":" + variant + modelId.getVariant();
                } else {
                    key = Electromechanics.MODID + ":colored_" + variant + modelId.getVariant();
                }

                if (BLOCKSTATE_ID_CACHE.get(key) == null) {
                    throw new IllegalResolverStateException("A Block Variant was found to be missing the needed data to load models, which will cause massive issues! [Variant: " + variant + "]");
                }

                context.setModel(state, new DelegatingUnbakedModel(BLOCKSTATE_ID_CACHE.get(key)));
            }
        }
    }

    //----------------------------------------------------------------------------------------------------------------------

    public static class IllegalResolverStateException extends IllegalStateException {
        public IllegalResolverStateException(String s) {
            super(s);
        }
    }
}
