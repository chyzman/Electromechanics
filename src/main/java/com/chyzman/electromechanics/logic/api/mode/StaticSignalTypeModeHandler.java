package com.chyzman.electromechanics.logic.api.mode;

import com.chyzman.electromechanics.block.gate.GateStateStorage;
import com.chyzman.electromechanics.logic.api.SetupDynamicStorage;
import com.chyzman.electromechanics.logic.api.SignalConfiguration;
import com.chyzman.electromechanics.logic.api.SignalType;

public abstract class StaticSignalTypeModeHandler implements SetupDynamicStorage {

    protected final SignalType inputSignalType;
    protected final SignalType outputSignalType;

    protected StaticSignalTypeModeHandler(SignalType signalType){
        this(signalType, signalType);
    }

    protected StaticSignalTypeModeHandler(SignalType inputSignalType, SignalType outputSignalType){
        this.inputSignalType = inputSignalType;
        this.outputSignalType = outputSignalType;
    }


    public SetupDynamicStorage getSetup() {
        return this;
    }

    @Override
    public void setup(GateStateStorage storage) {
        var map = storage.dynamicStorage();

        map.put(GateStateStorage.SIGNAL_CONFIGURATION, new SignalConfiguration(this.inputSignalType, this.outputSignalType));
    }
}
