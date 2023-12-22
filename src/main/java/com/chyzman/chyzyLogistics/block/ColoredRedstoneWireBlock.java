package com.chyzman.chyzyLogistics.block;

import com.chyzman.chyzyLogistics.mixin.RedstoneWireBlockAccessor;
import com.chyzman.chyzyLogistics.util.Colored;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ColoredRedstoneWireBlock extends RedstoneWireBlock implements BlockColorProvider, Colored {

    public static Map<DyeColor, Vec3d[]> COLOR_DATA = new HashMap<>();

    static {
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

            COLOR_DATA.put(value, dyeColorVariants);
        }
    }

    private final DyeColor dyeColor;

    public ColoredRedstoneWireBlock(DyeColor dyecolor, Settings settings) {
        super(settings);

        this.dyeColor = dyecolor;
    }

    @Override
    public DyeColor getColor() {
        return dyeColor;
    }

    @Override
    public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tintIndex) {
        var vec3d = getWireColor(this.getColor(), state.get(RedstoneWireBlock.POWER));

        return MathHelper.packRgb((float)vec3d.getX(), (float)vec3d.getY(), (float)vec3d.getZ());
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int i = state.get(POWER);
        if (i != 0) {
            for(Direction direction : Direction.Type.HORIZONTAL) {
                WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
                var accessor = ((RedstoneWireBlockAccessor) this);

                switch(wireConnection) {
                    case UP:
                        accessor.chyzy$addPoweredParticles(world, random, pos, getWireColor(dyeColor, i), direction, Direction.UP, -0.5F, 0.5F);
                    case SIDE:
                        accessor.chyzy$addPoweredParticles(world, random, pos, getWireColor(dyeColor, i), Direction.DOWN, direction, 0.0F, 0.5F);
                        break;
                    case NONE:
                    default:
                        accessor.chyzy$addPoweredParticles(world, random, pos, getWireColor(dyeColor, i), Direction.DOWN, direction, 0.0F, 0.3F);
                }
            }
        }
    }

    public static Vec3d getWireColor(DyeColor dyeColor, int i) {
        return COLOR_DATA.get(dyeColor)[i];
        /*Color color = Color.ofDye(dyeColor);

        float red = color.red();
        float green = color.green();
        float blue = color.blue();

        double averageComp = (red + green + blue) / 3;

        float f;
        Vec3d vec3d;

        if(averageComp > 0.3){
            f = 0.4f + (0.6f * ((float)i / 15.0F));

            vec3d = new Vec3d(red * f, green * f, blue * f);
        } else {
            f = 0.35f + (0.6f * ((float)i / 15.0F));

            vec3d = new Vec3d(red * f, green * f, blue * f);
        }

        return vec3d;*/
    }

    public static boolean isValid(Block block, Block block2){
        if(!(block2 instanceof RedstoneWireBlock)) return true;

        DyeColor dyeColor = null;

        if(block instanceof ColoredRedstoneWireBlock coloredWiredBlock){
            dyeColor = coloredWiredBlock.getColor();
        }

        DyeColor dyeColor2 = null;

        if(block2 instanceof ColoredRedstoneWireBlock coloredWiredBlock2){
            dyeColor2 = coloredWiredBlock2.getColor();
        }

        return dyeColor == dyeColor2;
    }
}
