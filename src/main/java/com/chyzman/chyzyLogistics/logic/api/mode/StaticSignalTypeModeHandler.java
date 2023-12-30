package com.chyzman.chyzyLogistics.logic.api.mode;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import com.chyzman.chyzyLogistics.logic.api.SetupDynamicStorage;
import com.chyzman.chyzyLogistics.logic.api.SignalConfiguration;
import com.chyzman.chyzyLogistics.logic.api.SignalType;

public abstract class StaticSignalTypeModeHandler implements SetupDynamicStorage {

    protected final SignalType signalType;

    protected StaticSignalTypeModeHandler(SignalType signalType){
        this.signalType = signalType;
    }

    public SetupDynamicStorage getSetup() {
        return this;
    }

    @Override
    public void setup(GateStateStorage storage) {
        var map = storage.dynamicStorage();

        map.put(GateStateStorage.SIGNAL_CONFIGURATION, new SignalConfiguration(this.signalType));
    }
}
