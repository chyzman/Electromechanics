package com.chyzman.electromechanics.logic.api.state;

import com.chyzman.electromechanics.logic.api.configuration.Side;
import com.chyzman.electromechanics.util.EndecUtils;
import com.chyzman.electromechanics.util.ImplMapCarrier;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import io.wispforest.owo.serialization.format.nbt.NbtEndec;
import io.wispforest.owo.serialization.util.MapCarrier;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ImplGateStateStorage implements GateStateStorage {

    public static final Endec<Map<Side, Integer>> POWER_LEVEL_ENDEC = EndecUtils.mapOf(Endec.INT, Side::valueOf, Side::name);

    public static final KeyedEndec<Map<Side, Integer>> INPUT = POWER_LEVEL_ENDEC.keyed("Input", new HashMap<>());
    public static final KeyedEndec<Map<Side, Integer>> OUTPUT = POWER_LEVEL_ENDEC.keyed("Output", new HashMap<>());

    public static final KeyedEndec<Integer> MODE = Endec.INT.keyed("Mode", 0);

    public static final KeyedEndec<NbtCompound> DYNAMIC_DATA = NbtEndec.COMPOUND.keyed("DynamicData", NbtCompound::new);

    private Map<Side, Integer> inputPowerLevel = new HashMap<>();
    private Map<Side, Integer> outputPowerLevel = new HashMap<>();

    private int mode = 0;

    private final ImplMapCarrier<NbtCompound> dynamicData;

    private final Predicate<GateStateStorage> shouldUpdateValue;
    private final Consumer<GateStateStorage> onChange;

    public ImplGateStateStorage(Predicate<GateStateStorage> shouldUpdateValue, Consumer<GateStateStorage> onChange){
        this.shouldUpdateValue = shouldUpdateValue;
        this.onChange = onChange;

        this.dynamicData = new ImplMapCarrier<>(new NbtCompound())
                .onChange(nbtCompound -> {
                    if(this.shouldUpdateValue.test(this)) this.onChange.accept(this);
                });
    }

    public void readNbt(NbtCompound nbt) {
        this.inputPowerLevel = nbt.get(INPUT);
        this.outputPowerLevel = nbt.get(OUTPUT);

        this.mode = nbt.get(MODE);

        this.dynamicData.setMapCarrier(nbt.get(DYNAMIC_DATA));
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.put(INPUT, this.inputPowerLevel);
        nbt.put(OUTPUT, this.outputPowerLevel);

        nbt.put(MODE, this.mode);

        nbt.put(DYNAMIC_DATA, this.dynamicData.getMapCarrier());
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;

        this.onChange.accept(this);
    }

    @Override
    public int getMode() {
        return this.mode;
    }

    @Override
    public void setOutputPower(Side side, int power){
        if(!this.shouldUpdateValue.test(this) || (this.outputPowerLevel.containsKey(side) && this.outputPowerLevel.get(side) == power)) return;

        this.outputPowerLevel.put(side, power);

        this.onChange.accept(this);
    }

    @Override
    public void setInputPower(Side side, int power){
        if(!this.shouldUpdateValue.test(this) || (this.inputPowerLevel.containsKey(side) && this.inputPowerLevel.get(side) == power)) return;

        this.inputPowerLevel.put(side, power);

        this.onChange.accept(this);
    }

    @Override
    public int getInputPower(Side side){
        return this.inputPowerLevel.getOrDefault(side, 0);
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
        return this.outputPowerLevel.values().stream().anyMatch(integer -> integer > 0);
    }

    @Override
    public <M extends MapCarrier> M dynamicStorage() {
        return (M) this.dynamicData;
    }
}
