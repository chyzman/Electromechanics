package com.chyzman.electromechanics.client;

import com.chyzman.electromechanics.Electromechanics;
import com.chyzman.electromechanics.block.gate.GateBlockEntity;
import com.chyzman.electromechanics.mixin.BlockEntityTypeAccessor;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.DelegatingUnbakedModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.block.Block;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GateModelLoader implements ModelResolver {

    public static final Identifier REDSTONE_PATH_GRAY = Electromechanics.id("block/redstone_path_gray");

    public static final Identifier REDSTONE_PATH_OFF = Electromechanics.id("block/redstone_path_off");
    public static final Identifier REDSTONE_PATH_ON = Electromechanics.id("block/redstone_path_on");

    public static final Identifier FULL_CHIP = Electromechanics.id("block/chips/full_chip");
    public static final Identifier HALF_CHIP = Electromechanics.id("block/chips/half_chip");
    public static final Identifier QUARTER_CHIP = Electromechanics.id("block/chips/quarter_chip");

    private static final JsonUnbakedModel ITEM_MODEL = null;
    private static final Identifier ITEM_MODEL_ID = Electromechanics.id("item/gate/board/base");
    private static final Identifier GATE_MODEL_ID = Electromechanics.id("block/gate");

    private static List<Identifier> gateItems = new ArrayList<>();

    public static void init(){
        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.addModels(Electromechanics.id("block/gate/board/base"));

            pluginContext.addModels(ITEM_MODEL_ID);
            pluginContext.addModels(GATE_MODEL_ID);

            pluginContext.addModels(REDSTONE_PATH_GRAY, REDSTONE_PATH_OFF, REDSTONE_PATH_ON);

            pluginContext.addModels(FULL_CHIP, HALF_CHIP, QUARTER_CHIP);

            BlockStateResolver resolver = context -> {
                for (var state : context.block().getStateManager().getStates()) {
                    context.setModel(state, new DelegatingUnbakedModel(GATE_MODEL_ID));
                }
            };

            for (Block block : ((BlockEntityTypeAccessor) GateBlockEntity.getBlockEntityType()).getBlocks()) {
                pluginContext.registerBlockStateResolver(block, resolver);

                gateItems.add(Registries.ITEM.getId(block.asItem()));
            }

            pluginContext.resolveModel().register(new GateModelLoader());
        });
    }

    @Override
    public @Nullable UnbakedModel resolveModel(Context context) {
        var id = context.id();

        if(!id.getNamespace().equals(Electromechanics.MODID)) return null;

        // Is Item Model variant
        if (!id.getPath().contains("item/")) return null;

        var path = id.getPath();

        boolean shouldRedirect = false;

        for (Identifier gateItem : gateItems) {
            if(path.contains(gateItem.getPath())){
                shouldRedirect = true;
                break;
            }
        }

        if(!shouldRedirect) return null;

        if(id.equals(ITEM_MODEL_ID)){
            return JsonUnbakedModel.deserialize(
                    """
                    {
                        "parent": "builtin/entity",
                        "textures": {
                            "particle": "chyzylogistics:block/gate/board/base_with_arrow"
                        },
                        "display": {
                            "gui": {
                                "rotation": [ 30, 45, 0 ],
                                "translation": [ 0, 0, 0],
                                "scale":[ 0.625, 0.625, 0.625 ]
                            },
                            "ground": {
                                "rotation": [ 0, 0, 0 ],
                                "translation": [ 0, 3, 0],
                                "scale":[ 0.25, 0.25, 0.25 ]
                            },
                            "head": {
                                "rotation": [ 0, 180, 0 ],
                                "translation": [ 0, 0, 0],
                                "scale":[ 1, 1, 1]
                            },
                            "fixed": {
                                "rotation": [ 0, 180, 0 ],
                                "translation": [ 0, 0, 0],
                                "scale":[ 0.5, 0.5, 0.5 ]
                            },
                            "thirdperson_righthand": {
                                "rotation": [ 75, 315, 0 ],
                                "translation": [ 0, 2.5, 0],
                                "scale": [ 0.375, 0.375, 0.375 ]
                            },
                            "firstperson_righthand": {
                                "rotation": [ 0, 315, 0 ],
                                "translation": [ 0, 0, 0],
                                "scale": [ 0.4, 0.4, 0.4 ]
                            }
                        }
                    }
                    """
            );
        }

        return new DelegatingUnbakedModel(ITEM_MODEL_ID);
    }
}
