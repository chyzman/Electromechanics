package com.chyzman.chyzyLogistics.block.gate;

import com.chyzman.chyzyLogistics.logic.api.Side;
import com.chyzman.chyzyLogistics.logic.api.SignalConfiguration;
import com.chyzman.chyzyLogistics.logic.api.SignalType;
import com.chyzman.chyzyLogistics.util.DelagatingMapCarrier;
import com.chyzman.chyzyLogistics.util.EndecUtils;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import io.wispforest.owo.serialization.util.MapCarrier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GateStateStorage {

    KeyedEndec<Integer> UPDATE_DELAY = EndecUtils.POSITIVE_INT.keyed("UpdateDelay", () -> 2);

    KeyedEndec<SignalConfiguration> SIGNAL_CONFIGURATION = SignalConfiguration.ENDEC
            .keyed("SignalConfiguration", () -> new SignalConfiguration(SignalType.DIGITAL));

    KeyedEndec<List<Side>> INPUTS = Endec.forEnum(Side.class).listOf().keyed("Inputs", List::of);
    KeyedEndec<List<Side>> OUTPUTS = Endec.forEnum(Side.class).listOf().keyed("Outputs", List::of);

    // --

    void setOutputPower(Side side, int power);

    void setInputPower(Side side, int power);

    boolean hasChangesOccurred();

    // --

    int getInputPower(Side side);

    boolean isBeingPowered(Side side);

    int getOutputPower(Side side);

    boolean isOutputtingPower(Side side);

    boolean isOutputtingPower();

    //--

    void setMode(int mode);

    int getMode();

    //--

    <M extends MapCarrier> M dynamicStorage();

}
