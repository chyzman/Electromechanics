package com.chyzman.electromechanics.logic.api.mode;

import com.chyzman.electromechanics.block.gate.GateStateStorage;
import com.chyzman.electromechanics.logic.api.GateInteractEvent;
import com.chyzman.electromechanics.logic.api.GateLogicFunction;
import com.chyzman.electromechanics.logic.api.SignalType;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class ExpressionModeHandler extends StaticSignalTypeModeHandler implements GateInteractEvent {

    private final List<GateLogicFunction> expressions = new ArrayList<>();

    public ExpressionModeHandler(SignalType signalType) {
        super(signalType);
    }

    public ExpressionModeHandler(SignalType inputSignalType, SignalType outputSignalType) {
        super(inputSignalType, outputSignalType);
    }

    public static ExpressionModeHandler of(SignalType signalType, GateLogicFunction expression){
        return new ExpressionModeHandler(signalType).add(expression);
    }

    public ExpressionModeHandler add(GateLogicFunction expression){
        expressions.add(expression);

        return this;
    }

    public GateLogicFunction getExpression(int mode){
        return expressions.get(mode);
    }

    // --

    public GateInteractEvent interactEvent() {
        return this;
    }

    @Override
    public ActionResult interact(GateStateStorage storage) {
        var nextMode = storage.getMode() + 1;

        if(nextMode >= expressions.size()) nextMode = 0;

        storage.setMode(nextMode);

        return ActionResult.CONSUME;
    }
}
