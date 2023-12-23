package com.chyzman.chyzyLogistics.block.gate;

import com.mojang.serialization.MapCodec;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.ComparatorMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class GateBlock extends AbstractRedstoneGateBlock {
    public static final BooleanProperty INVERTED = Properties.INVERTED;

    public GateBlock(Settings settings) {
        super(settings);

        this.setDefaultState(buildDefaultState());
    }

    public BlockState buildDefaultState(){
        return this.stateManager
                .getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POWERED, Boolean.FALSE)
                .with(INVERTED, Boolean.FALSE);
    }

    public abstract boolean wireConnectsTo(BlockState state, Direction dir);

    @Override
    protected abstract MapCodec<? extends AbstractRedstoneGateBlock> getCodec();

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        }

        world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, state.get(INVERTED) ? 0.55F : 0.5F);

        var newState = state.with(INVERTED, !state.get(INVERTED));

        world.setBlockState(pos, newState.with(POWERED, hasPower(world, pos, newState)), Block.NOTIFY_LISTENERS);

        return ActionResult.success(world.isClient);
    }

    @Override
    protected int getUpdateDelayInternal(BlockState state) {
        return 2;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, INVERTED);
    }


}