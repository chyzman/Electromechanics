package com.chyzman.chyzyLogistics.logic.api.mode;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import com.chyzman.chyzyLogistics.logic.api.GateContext;
import com.chyzman.chyzyLogistics.logic.api.Side;
import com.chyzman.chyzyLogistics.logic.api.SignalType;

public interface ModeHandler {

    default void handleModeChange(GateStateStorage storage){
        var nextMode = storage.getMode() + 1;

        if(nextMode >= totalModes()) nextMode = 0;

        storage.setMode(nextMode);
    }

    int totalModes();

    SignalType getInputType(int mode, Side side);

    SignalType getOutputType(int mode, Side side);

    default int getUpdateDelay(int mode){
        return 2;
    }

}
