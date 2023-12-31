package com.chyzman.chyzyLogistics.client;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.block.gate.ProGateBlockEntity;
import com.chyzman.chyzyLogistics.mixin.BlockEntityTypeAccessor;
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

    public static final Identifier REDSTONE_PATH_OFF = ChyzyLogistics.id("block/redstone_path_off");
    public static final Identifier REDSTONE_PATH_ON = ChyzyLogistics.id("block/redstone_path_on");

    public static final Identifier FULL_CHIP = ChyzyLogistics.id("block/full_chip");
    public static final Identifier HALF_CHIP = ChyzyLogistics.id("block/half_chip");
    public static final Identifier QUARTER_CHIP = ChyzyLogistics.id("block/quarter_chip");

    private static final JsonUnbakedModel ITEM_MODEL = null;
    private static final Identifier ITEM_MODEL_ID = ChyzyLogistics.id("item/gate/board/base");

    private static List<Identifier> gateItems = new ArrayList<>();

    public static void init(){
        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.addModels(ChyzyLogistics.id("block/gate/board/base"));
            pluginContext.addModels(ITEM_MODEL_ID);

            pluginContext.addModels(REDSTONE_PATH_OFF, REDSTONE_PATH_ON);

            pluginContext.addModels(FULL_CHIP, HALF_CHIP, QUARTER_CHIP);

            BlockStateResolver resolver = context -> {
//                var model = context.getOrLoadModel(new Identifier("builtin/entity"));

                for (var state : context.block().getStateManager().getStates()) {
                    context.setModel(state, new DelegatingUnbakedModel(new Identifier("builtin/entity")));
                }
            };

            for (Block block : ((BlockEntityTypeAccessor) ProGateBlockEntity.getBlockEntityType()).getBlocks()) {
                pluginContext.registerBlockStateResolver(block, resolver);

                gateItems.add(Registries.ITEM.getId(block.asItem()));
            }

            //pluginContext.resolveModel().register(new GateModelLoader());
        });
    }

    @Override
    public @Nullable UnbakedModel resolveModel(Context context) {
        var id = context.id();

        if(!id.getNamespace().equals(ChyzyLogistics.MODID)) return null;

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
