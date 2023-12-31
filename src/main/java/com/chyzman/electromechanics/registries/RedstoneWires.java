package com.chyzman.electromechanics.registries;

import com.chyzman.electromechanics.ElectromechanicsLogistics;
import com.chyzman.electromechanics.item.ColoredBlockItem;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RedstoneWires {

    private static final Map<Block, DyeColor> BLOCK_TO_DYE_COLOR = new HashMap<>();

    private static final Map<DyeColor, Vec3d[]> COLOR_DATA = Util.make(() -> {
        var map = new HashMap<DyeColor, Vec3d[]>();

        for (DyeColor value : DyeColor.values()) {
            Color color = Color.ofDye(value);

            Vec3d[] dyeColorVariants = Util.make(new Vec3d[16], colors -> {
                float red = color.red();
                float green = color.green();
                float blue = color.blue();

                double averageComp = (red + green + blue) / 3;

                for (int i = 0; i < colors.length; i++) {
                    float f;
                    Vec3d vec3d;

                    if(averageComp > 0.3){
                        f = 0.4f + (0.6f * ((float)i / 15.0F));

                        vec3d = new Vec3d(red * f, green * f, blue * f);
                    } else {
                        f = 0.35f + (0.6f * ((float)i / 15.0F));

                        vec3d = new Vec3d(red * f, green * f, blue * f);
                    }

                    colors[i] = vec3d;
                }
            });

            map.put(value, dyeColorVariants);
        }

        return map;
    });

    private static final Map<String, Block> DUST_VARIANTS = new HashMap<>();

    private static final List<Item> DUSTS = new ArrayList<>();

    public static void init(){
        DUST_VARIANTS.put("redstone_wire", Blocks.REDSTONE_WIRE);

        for (DyeColor value : DyeColor.values()) {
            RegistryUtils.registerBlockAndItem(value.asString() + "_redstone_wire",
                    () -> {
                        var block = new RedstoneWireBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_WIRE).mapColor(value));

                        BLOCK_TO_DYE_COLOR.put(block, value);

                        return block;
                    },
                    block -> {
                        var item = new ColoredBlockItem(value, block, new FabricItemSettings());

                        DUSTS.add(item);

                        return item;
                    });
        }
    }

    public static Map<String, Block> variantInfo(){
        return ImmutableMap.copyOf(DUST_VARIANTS);
    }

    public static List<Item> getDusts(){
        return ImmutableList.copyOf(DUSTS);
    }

    public static void iterateVariants(Consumer<Block> consumer){
        for (var entry : DUST_VARIANTS.entrySet()) {
            for(DyeColor dyeColor : DyeColor.values()){
                consumer.accept(getColoredVariant(dyeColor, entry.getKey()));
            }
        }
    }

    public static Block getColoredVariant(DyeColor color, String type){
        return Registries.BLOCK.get(new Identifier(ElectromechanicsLogistics.MODID, color.asString() + "_" + type));
    }

    @Nullable
    public static DyeColor getDyeColor(Block block){
        return BLOCK_TO_DYE_COLOR.get(block);
    }

    @Nullable
    public static Vec3d[] getColorArray(Block block){
        var dyeColor = RedstoneWires.BLOCK_TO_DYE_COLOR.get(block);

        if(dyeColor == null) return null;

        return getColorArray(dyeColor);
    }

    @Nullable
    public static Vec3d[] getColorArray(DyeColor dyeColor){
        return RedstoneWires.COLOR_DATA.get(dyeColor);
    }

    @Nullable
    public static Vec3d getColor(BlockState state){
        if(!(state.getBlock() instanceof RedstoneWireBlock)) return null;

        var dyeColor = getDyeColor(state.getBlock());

        if(dyeColor == null) return null;

        return getColorArray(dyeColor)[state.get(RedstoneWireBlock.POWER)];
    }
}
