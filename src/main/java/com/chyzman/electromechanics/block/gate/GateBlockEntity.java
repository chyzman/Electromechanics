package com.chyzman.electromechanics.block.gate;

import com.chyzman.electromechanics.Electromechanics;
import com.chyzman.electromechanics.logic.api.state.GateStateStorage;
import com.chyzman.electromechanics.logic.api.GateHandler;
import com.chyzman.electromechanics.logic.api.configuration.Side;
import com.chyzman.electromechanics.logic.api.state.WorldGateContext;
import com.chyzman.electromechanics.util.EndecUtils;
import com.chyzman.electromechanics.util.ImplMapCarrier;
import io.wispforest.owo.ops.WorldOps;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import io.wispforest.owo.serialization.format.nbt.NbtEndec;
import io.wispforest.owo.serialization.util.MapCarrier;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.tick.WorldTickScheduler;

import java.util.HashMap;
import java.util.Map;

public class GateBlockEntity extends BlockEntity implements GateStateStorage {

    private static BlockEntityType<GateBlockEntity> GATE_TYPE = null;

    protected static final FabricBlockEntityTypeBuilder<GateBlockEntity> GATE_TYPE_BUILDER =
            FabricBlockEntityTypeBuilder.create(GateBlockEntity::createBlockEntity);

    public static BlockEntityType<GateBlockEntity> getBlockEntityType(){
        if(GATE_TYPE == null){
            GATE_TYPE = Registry.register(Registries.BLOCK_ENTITY_TYPE, Electromechanics.id("pro_gate"), GATE_TYPE_BUILDER.build());
        }

        return GATE_TYPE;
    }

    //--

    public static final Endec<Map<Side, Integer>> POWER_LEVEL_ENDEC = EndecUtils.mapOf(Endec.INT, Side::valueOf, Side::name);

    public static final KeyedEndec<Map<Side, Integer>> INPUT = POWER_LEVEL_ENDEC.keyed("Input", new HashMap<>());
    public static final KeyedEndec<Map<Side, Integer>> OUTPUT = POWER_LEVEL_ENDEC.keyed("Output", new HashMap<>());

    public static final KeyedEndec<Integer> MODE = Endec.INT.keyed("Mode", 0);

    public static final KeyedEndec<NbtCompound> DYNAMIC_DATA = NbtEndec.COMPOUND.keyed("DynamicData", NbtCompound::new);

    //--

    private final GateHandler handler;

    private boolean hasChangesOccurred = false;

    private Map<Side, Integer> inputPowerLevel = new HashMap<>();
    private Map<Side, Integer> outputPowerLevel = new HashMap<>();

    private int mode = 0;

    private final ImplMapCarrier<NbtCompound> dynamicData = new ImplMapCarrier<>(new NbtCompound())
            .onChange(nbtCompound -> this.markDirty());

    private GateBlockEntity(BlockPos pos, BlockState state, GateHandler handler) {
        super(getBlockEntityType(), pos, state);

        this.handler = handler;
    }

    public static GateBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if(!(state.getBlock() instanceof GateBlock proGateBlock)){
            throw new IllegalStateException("Unable to get the needed AbstractGateHandler from the BlockState");
        }

        var blockEntity = new GateBlockEntity(pos, state, proGateBlock.handler);

        proGateBlock.handler.setupStorage(blockEntity);

        return blockEntity;
    }

    public GateHandler getHandler(){
        return this.handler;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        var nbt = super.toInitialChunkDataNbt();

        writeNbt(nbt);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.inputPowerLevel = nbt.get(INPUT);
        this.outputPowerLevel = nbt.get(OUTPUT);

        this.mode = nbt.get(MODE);

        this.dynamicData.setMapCarrier(nbt.get(DYNAMIC_DATA));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put(INPUT, this.inputPowerLevel);
        nbt.put(OUTPUT, this.outputPowerLevel);

        nbt.put(MODE, this.mode);

        nbt.put(DYNAMIC_DATA, this.dynamicData.getMapCarrier());
    }

    @Override
    public void markDirty() {
        super.markDirty();

        this.hasChangesOccurred = true;

        WorldOps.updateIfOnServer(this.world, this.pos);
    }

    //--

    public void tick() {
        if(this.world.isClient()) return;

        var result = this.handler.onTick(WorldGateContext.of(this.world, this.pos));

        if(result == ActionResult.SUCCESS){
            world.updateNeighborsAlways(pos, this.getCachedState().getBlock());

            //this.getCachedState().updatePowered(world, pos, state);
            ((GateBlock) this.getCachedState().getBlock()).updatePowered(this.world, this.pos, this.getCachedState());
        }
    }

    //--

    public void setHasChangesOccurred(boolean value){
        this.hasChangesOccurred = value;
    }

    @Override
    public boolean hasChangesOccurred(){
        var hasChanged = this.hasChangesOccurred;

        this.setHasChangesOccurred(false);

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
        return this.outputPowerLevel.getOrDefault(side, 0);
    }

    @Override
    public boolean isOutputtingPower(Side side){
        return getOutputPower(side) > 0;
    }

    @Override
    public boolean isOutputtingPower(){
        return outputPowerLevel.values().stream().anyMatch(integer -> integer > 0);
    }

    @Override
    public <M extends MapCarrier> M dynamicStorage() {
        return (M) this.dynamicData;
    }
}
