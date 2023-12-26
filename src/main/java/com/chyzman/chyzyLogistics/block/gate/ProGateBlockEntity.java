package com.chyzman.chyzyLogistics.block.gate;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.logic.GateHandler;
import com.chyzman.chyzyLogistics.logic.Side;
import com.chyzman.chyzyLogistics.util.EndecUtils;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class ProGateBlockEntity extends BlockEntity implements GateStateStorage {

    private static BlockEntityType<ProGateBlockEntity> GATE_TYPE = null;

    protected static final FabricBlockEntityTypeBuilder<ProGateBlockEntity> GATE_TYPE_BUILDER =
            FabricBlockEntityTypeBuilder.create(ProGateBlockEntity::createBlockEntity);

    public static BlockEntityType<ProGateBlockEntity> getBlockEntityType(){
        if(GATE_TYPE == null){
            GATE_TYPE = Registry.register(Registries.BLOCK_ENTITY_TYPE, ChyzyLogistics.id("pro_gate"), GATE_TYPE_BUILDER.build());
        }

        return GATE_TYPE;
    }

    //--

    public static final Endec<Map<Side, Integer>> POWER_LEVEL_ENDEC = EndecUtils.mapOf(Endec.INT, Side::valueOf, Side::name);

    public static final KeyedEndec<Map<Side, Integer>> INPUT = POWER_LEVEL_ENDEC.keyed("Input", new HashMap<>());
    public static final KeyedEndec<Map<Side, Integer>> OUTPUT = POWER_LEVEL_ENDEC.keyed("Output", new HashMap<>());

    public static final KeyedEndec<Integer> MODE = Endec.INT.keyed("Mode", 0);

    //--

    private final GateHandler handler;

    private boolean hasChangesOccured = false;

    private Map<Side, Integer> inputPowerLevel = new HashMap<>();
    private Map<Side, Integer> outputPowerLevel = new HashMap<>();

    private int mode = 0;

    public ProGateBlockEntity(BlockPos pos, BlockState state, GateHandler handler) {
        super(getBlockEntityType(), pos, state);

        this.handler = handler;
    }

    public static ProGateBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if(!(state.getBlock() instanceof ProGateBlock proGateBlock)){
            throw new IllegalStateException("Unable to get the needed AbstractGateHandler from the BlockState");
        }

        return new ProGateBlockEntity(pos, state, proGateBlock.handler);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.inputPowerLevel = nbt.get(INPUT);
        this.outputPowerLevel = nbt.get(OUTPUT);

        this.mode = nbt.get(MODE);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put(INPUT, this.inputPowerLevel);
        nbt.put(OUTPUT, this.outputPowerLevel);

        nbt.put(MODE, this.mode);
    }

    @Override
    public void markDirty() {
        super.markDirty();

        this.hasChangesOccured = true;
    }

    //--

    @Override
    public boolean hasChangesOccurred(){
        var hasChanged = this.hasChangesOccured;

        if(hasChanged) this.hasChangesOccured = false;

        return hasChanged;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;
        this.markDirty();
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void setOutputPower(Side side, int power){
        if(this.getWorld().isClient() || (this.outputPowerLevel.containsKey(side) && this.outputPowerLevel.get(side) == power)) return;

        this.outputPowerLevel.put(side, power);

        this.markDirty();
    }

    @Override
    public void setInputPower(Side side, int power){
        if(this.getWorld().isClient() || (this.inputPowerLevel.containsKey(side) && this.inputPowerLevel.get(side) == power)) return;

        this.inputPowerLevel.put(side, power);

        this.markDirty();
    }

    @Override
    public int getInputPower(Side side){
        if(!inputPowerLevel.containsKey(side)) return 0;

        return inputPowerLevel.get(side);
    }

    @Override
    public boolean isBeingPowered(Side side){
        return getInputPower(side) > 0;
    }

    @Override
    public int getOutputPower(Side side){
        if(!outputPowerLevel.containsKey(side)) return 0;

        return outputPowerLevel.get(side);
    }

    @Override
    public boolean isOutputtingPower(Side side){
        return getOutputPower(side) > 0;
    }

    @Override
    public boolean isOutputtingPower(){
        return outputPowerLevel.values().stream().anyMatch(integer -> integer > 0);
    }
}
