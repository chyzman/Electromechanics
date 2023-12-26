package com.chyzman.chyzyLogistics.logic.mode;

import com.chyzman.chyzyLogistics.block.gate.GateStateStorage;
import com.chyzman.chyzyLogistics.logic.LogicExpression;
import com.chyzman.chyzyLogistics.logic.Side;
import com.chyzman.chyzyLogistics.logic.SignalType;

import java.util.List;

public interface ModeHandler {

    default void handleModeChange(GateStateStorage storage){
        var nextMode = storage.getMode() + 1;

        if(nextMode >= totalModes()) nextMode = 0;

        storage.setMode(nextMode);
    }

    int totalModes();

    LogicExpression getExpression(int mode);

    List<Side> getInputSides(int mode);

    List<Side> getOutputSides(int mode);

    SignalType getInputType(Side side);

    SignalType getOutputType(Side side);

}
