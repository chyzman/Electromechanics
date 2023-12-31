package com.chyzman.electromechanics.block.gate;

import com.mojang.serialization.MapCodec;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public class MonoGateBlock extends GateBlock {

    private static final MapCodec<MonoGateBlock> CODEC =  StructEndecBuilder.of(
            Endec.STRING.xmap(GateType::getType, GateType::type).fieldOf("variant", MonoGateBlock::getType),
            Endec.ofCodec(AbstractBlock.Settings.CODEC).fieldOf("properties", AbstractBlock::getSettings),
            MonoGateBlock::new
    ).mapCodec();

    private final GateType type;

    public MonoGateBlock(GateType type, Settings settings) {
        super(settings);

        this.type = type;
    }

    public GateType getType(){
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
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.isLocked(world, pos, state)) {
            boolean powered = world.getEmittedRedstonePower(pos.offset(state.get(FACING)), state.get(FACING)) > 0;

            var tempState = state
                    .with(POWERED, shouldPower(powered, state));

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

            boolean bl = state.get(POWERED);

            var tempState = state
                    .with(POWERED, shouldPower(powered, state));

            if (!tempState.equals(state) && !world.getBlockTickScheduler().isTicking(pos, this)) {
                TickPriority tickPriority = TickPriority.HIGH;
                if (this.isTargetNotAligned(world, pos, state)) {
                    tickPriority = TickPriority.EXTREMELY_HIGH;
                } else if (bl) {
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
        return state.get(INVERTED) != this.type.logicFunc().apply(powered);
    }
}