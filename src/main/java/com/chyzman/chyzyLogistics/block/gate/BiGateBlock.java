package com.chyzman.chyzyLogistics.block.gate;

import com.mojang.serialization.MapCodec;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.StructEndecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public class BiGateBlock extends GateBlock {
    public static final BooleanProperty RIGHT_POWERED = BooleanProperty.of("right_powered");
    public static final BooleanProperty LEFT_POWERED = BooleanProperty.of("left_powered");

    private final BiGateType type;

    public BiGateBlock(BiGateType type, Settings settings) {
        super(settings);

        this.type = type;
    }

    public BiGateType getType(){
        return this.type;
    }

    @Override
    protected MapCodec<? extends AbstractRedstoneGateBlock> getCodec() {
        return StructEndecBuilder.of(
                Endec.STRING.xmap(BiGateType::getType, BiGateType::type).fieldOf("variant", BiGateBlock::getType),
                Endec.ofCodec(AbstractBlock.Settings.CODEC).fieldOf("properties", AbstractBlock::getSettings),
                BiGateBlock::new
        ).mapCodec();
    }

    @Override
    public BlockState buildDefaultState() {
        return super.buildDefaultState()
                .with(RIGHT_POWERED, Boolean.FALSE)
                .with(LEFT_POWERED, Boolean.FALSE);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);

        builder.add(RIGHT_POWERED, LEFT_POWERED);
    }

    @Override
    public boolean wireConnectsTo(BlockState state, Direction dir) {
        return dir.getAxis().isHorizontal() && dir != state.get(FACING).getOpposite();
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.isLocked(world, pos, state)) {
            boolean rightPowered = world.getEmittedRedstonePower(pos.offset(state.get(FACING).rotateYCounterclockwise()), state.get(FACING).rotateYCounterclockwise()) > 0;
            boolean leftPowered = world.getEmittedRedstonePower(pos.offset(state.get(FACING).rotateYClockwise()), state.get(FACING).rotateYClockwise()) > 0;

            var tempState = state
                    .with(RIGHT_POWERED, rightPowered)
                    .with(LEFT_POWERED, leftPowered)
                    .with(POWERED, shouldPower(rightPowered, leftPowered, state));

            if (!tempState.equals(state)) {
                world.setBlockState(pos, tempState, Block.NOTIFY_LISTENERS);
                world.scheduleBlockTick(pos, this, this.getUpdateDelayInternal(state), TickPriority.VERY_HIGH);
            }
        }
    }

    @Override
    protected void updatePowered(World world, BlockPos pos, BlockState state) {
        if (!this.isLocked(world, pos, state)) {
            boolean rightPowered = world.getEmittedRedstonePower(pos.offset(state.get(FACING).rotateYCounterclockwise()), state.get(FACING).rotateYCounterclockwise()) > 0;
            boolean leftPowered = world.getEmittedRedstonePower(pos.offset(state.get(FACING).rotateYClockwise()), state.get(FACING).rotateYClockwise()) > 0;

            boolean bl = state.get(POWERED);

            var tempState = state
                    .with(RIGHT_POWERED, rightPowered)
                    .with(LEFT_POWERED, leftPowered)
                    .with(POWERED, shouldPower(rightPowered, leftPowered, state));

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
    public boolean hasPower(World world, BlockPos pos, BlockState state){
        boolean rightPowered = world.getEmittedRedstonePower(pos.offset(state.get(FACING).rotateYCounterclockwise()), state.get(FACING).rotateYCounterclockwise()) > 0;
        boolean leftPowered = world.getEmittedRedstonePower(pos.offset(state.get(FACING).rotateYClockwise()), state.get(FACING).rotateYClockwise()) > 0;

        return shouldPower(rightPowered, leftPowered, state);
    }

    private boolean shouldPower(boolean rightPowered, boolean leftPowered, BlockState state){
        return state.get(INVERTED) != this.type.logicFunc().apply(rightPowered, leftPowered);
    }
}