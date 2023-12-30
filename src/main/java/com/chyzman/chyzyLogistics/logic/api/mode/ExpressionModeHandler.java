package com.chyzman.chyzyLogistics.logic.api.mode;

import com.chyzman.chyzyLogistics.logic.api.GateLogicFunction;
import com.chyzman.chyzyLogistics.logic.api.SignalType;

import java.util.ArrayList;
import java.util.List;

public class ExpressionModeHandler extends StaticSignalTypeModeHandler {

    private final List<GateLogicFunction> expressions = new ArrayList<>();

    public ExpressionModeHandler(SignalType signalType) {
        super(signalType);
    }

    public static ExpressionModeHandler of(SignalType signalType, GateLogicFunction expression){
        return new ExpressionModeHandler(signalType).add(expression);
    }

    public ExpressionModeHandler add(GateLogicFunction expression){
        expressions.add(expression);

        return this;
    }

    @Override
    public int totalModes() {
        return expressions.size();
    }

    public GateLogicFunction getExpression(int mode){
        return expressions.get(mode);
    }
}
