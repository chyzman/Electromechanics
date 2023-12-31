package com.chyzman.electromechanics.block.gate;

import com.mojang.serialization.MapCodec;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public class LatchGateBlock extends GateBlock {

    private static final MapCodec<LatchGateBlock> CODEC =  StructEndecBuilder.of(
            Endec.STRING.xmap(LatchGateType::getType, LatchGateType::type).fieldOf("variant", LatchGateBlock::getType),
            Endec.ofCodec(Settings.CODEC).fieldOf("properties", AbstractBlock::getSettings),
            LatchGateBlock::new
    ).mapCodec();

    private final LatchGateType type;

    public LatchGateBlock(LatchGateType type, Settings settings) {
        super(settings);

        this.type = type;
    }

    public LatchGateType getType(){
        return this.type;
    }

    @Override
    protected MapCodec<? extends AbstractRedstoneGateBlock> getCodec() {
        return CODEC;
    }

    @Override
    public boolean wireConnectsTo(BlockState state, Direction dir) {
        return dir == state.get(FACING) || dir == state.get(FACING).getOpposite();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        }

        world.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, state.get(POWERED) ? 0.55F : 0.5F);

        var newState = state.with(POWERED, !state.get(POWERED));

        world.setBlockState(pos, newState.with(POWERED, hasPower(world, pos, newState)), Block.NOTIFY_LISTENERS);

        return ActionResult.success(world.isClient);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.isLocked(world, pos, state)) {
            boolean powered = world.getEmittedRedstonePower(pos.offset(state.get(FACING)), state.get(FACING)) > 0;

            boolean isPowered = state.get(POWERED);
            boolean wasPowered = state.get(INVERTED);
            var tempState = state.with(POWERED, shouldPower(powered, state)).with(INVERTED, isPowered);

            if (!tempState.equals(state)) {
                world.setBlockState(pos, tempState, Block.NOTIFY_LISTENERS);
                world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
            }
        }
    }

    @Override
    protected void updatePowered(World world, BlockPos pos, BlockState state) {
        if (!this.isLocked(world, pos, state)) {
            boolean powered = world.getEmittedRedstonePower(pos.offset(state.get(FACING)), state.get(FACING)) > 0;

            boolean isPowered = state.get(POWERED);
            var tempState = state.with(POWERED, shouldPower(powered, state)).with(INVERTED, isPowered);

            if (!tempState.equals(state) && !world.getBlockTickScheduler().isTicking(pos, this)) {
                TickPriority tickPriority = TickPriority.HIGH;
                if (this.isTargetNotAligned(world, pos, state)) {
                    tickPriority = TickPriority.EXTREMELY_HIGH;
                } else if (isPowered) {
                    tickPriority = TickPriority.VERY_HIGH;
                }

                world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), tickPriority);
            }
        }
    }

    @Override
    protected int getPower(World world, BlockPos pos, BlockState state) {
        return hasPower(world, pos, state) ? 15 : 0;
    }

    @Override
    public boolean hasPower(World world, BlockPos pos, BlockState state) {
        boolean powered = world.getEmittedRedstonePower(pos.offset(state.get(FACING)), state.get(FACING)) > 0;
        return shouldPower(powered, state);
    }

    private boolean shouldPower(boolean powered, BlockState state){
        return this.type.logicFunc().apply(state.get(INVERTED), state.get(POWERED), powered);
    }
}