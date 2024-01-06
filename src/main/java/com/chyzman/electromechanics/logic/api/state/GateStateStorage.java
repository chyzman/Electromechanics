package com.chyzman.electromechanics.logic.api.state;

import com.chyzman.electromechanics.logic.api.configuration.Side;
import com.chyzman.electromechanics.logic.api.configuration.SignalConfiguration;
import com.chyzman.electromechanics.logic.api.configuration.SignalType;
import com.chyzman.electromechanics.util.EndecUtils;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import io.wispforest.owo.serialization.util.MapCarrier;

import java.util.List;

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
