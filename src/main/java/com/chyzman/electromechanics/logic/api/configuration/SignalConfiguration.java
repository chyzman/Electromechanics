package com.chyzman.electromechanics.logic.api.configuration;

import com.chyzman.electromechanics.util.EndecUtils;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import io.wispforest.owo.serialization.format.nbt.NbtEndec;
import io.wispforest.owo.serialization.util.MapCarrier;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignalConfiguration {

    public static final Endec<SignalConfiguration> ENDEC = NbtEndec.COMPOUND
            .xmap(SignalConfiguration::of, config -> config.write(new NbtCompound()));

    private static final Endec<SignalType> SIGNAL_TYPE_ENDEC = Endec.forEnum(SignalType.class);

    private static final KeyedEndec<SignalType> SIGNAL_TYPE = SIGNAL_TYPE_ENDEC.keyed("Base", () -> null);

    private static final KeyedEndec<SignalType> INPUT_SIGNAL_TYPE = SIGNAL_TYPE_ENDEC.keyed("Input", () -> null);
    private static final KeyedEndec<SignalType> OUTPUT_SIGNAL_TYPE = SIGNAL_TYPE_ENDEC.keyed("Output", () -> null);

    private static final KeyedEndec<Map<Side, SignalType>> SIDE_SIGNAL_TYPE_INFO = EndecUtils.mapOf(SIGNAL_TYPE_ENDEC, Side::valueOf, Side::name)
            .keyed("Sides", HashMap::new);

    @Nullable public SignalType inputType;
    @Nullable public SignalType outputType;

    @Nullable public Map<Side, SignalType> sideToSignalType;

    public SignalConfiguration(@NotNull SignalType inputType, @NotNull SignalType outputType){
        Objects.requireNonNull(inputType);
        Objects.requireNonNull(outputType);

        this.inputType = inputType;
        this.outputType = outputType;
    }

    public SignalConfiguration(@NotNull SignalType signalType){
        this(signalType, signalType);
    }

    public SignalConfiguration(@NotNull Map<Side, SignalType> sideToSignalType){
        Objects.requireNonNull(sideToSignalType);

        this.sideToSignalType = sideToSignalType;
    }

    public SignalType getSideSignalType(Side side, boolean isInput){
        if(this.sideToSignalType != null && this.sideToSignalType.containsKey(side)){
            return this.sideToSignalType.get(side);
        }

        if(isInput){
            return this.inputType;
        } else {
            return this.outputType;
        }
    }

    public static SignalConfiguration of(MapCarrier carrier){
        SignalConfiguration configuration;

        if(carrier.has(SIGNAL_TYPE)){
            configuration = new SignalConfiguration(carrier.get(SIGNAL_TYPE));
        } else if(carrier.has(INPUT_SIGNAL_TYPE) && carrier.has(OUTPUT_SIGNAL_TYPE)){
            configuration = new SignalConfiguration(carrier.get(INPUT_SIGNAL_TYPE), carrier.get(OUTPUT_SIGNAL_TYPE));
        } else if(carrier.has(SIDE_SIGNAL_TYPE_INFO)){
            configuration = new SignalConfiguration(carrier.get(SIDE_SIGNAL_TYPE_INFO));
        } else {
            throw new IllegalStateException("Unable to create a valid SignalConfiguration due to such being blank!");
        }

        return configuration;
    }

    public <M extends MapCarrier> M write(M carrier){
        if(inputType != null && outputType != null){
            carrier.put(INPUT_SIGNAL_TYPE, inputType);
            carrier.put(OUTPUT_SIGNAL_TYPE, outputType);
        } else {
            carrier.put(SIDE_SIGNAL_TYPE_INFO, sideToSignalType);
        }

        return carrier;
    }
}
