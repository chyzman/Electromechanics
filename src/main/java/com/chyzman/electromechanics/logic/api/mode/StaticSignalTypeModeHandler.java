package com.chyzman.electromechanics.logic.api.mode;

import com.chyzman.electromechanics.logic.api.GateSetupEvent;
import com.chyzman.electromechanics.logic.api.configuration.SignalConfiguration;
import com.chyzman.electromechanics.logic.api.configuration.SignalType;
import com.chyzman.electromechanics.logic.api.state.GateStateStorage;

public abstract class StaticSignalTypeModeHandler implements GateSetupEvent {

    protected final SignalType inputSignalType;
    protected final SignalType outputSignalType;

    protected StaticSignalTypeModeHandler(SignalType signalType){
        this(signalType, signalType);
    }

    protected StaticSignalTypeModeHandler(SignalType inputSignalType, SignalType outputSignalType){
        this.inputSignalType = inputSignalType;
        this.outputSignalType = outputSignalType;
    }

    public GateSetupEvent getSetup() {
        return this;
    }

    @Override
    public void setup(GateStateStorage storage) {
        var map = storage.dynamicStorage();

        map.put(GateStateStorage.SIGNAL_CONFIGURATION, new SignalConfiguration(this.inputSignalType, this.outputSignalType));
    }
}
