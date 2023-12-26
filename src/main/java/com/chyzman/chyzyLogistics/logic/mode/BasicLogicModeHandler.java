package com.chyzman.chyzyLogistics.logic.mode;

import com.chyzman.chyzyLogistics.logic.LogicExpression;
import com.chyzman.chyzyLogistics.logic.Side;
import com.chyzman.chyzyLogistics.logic.SignalType;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;

public class BasicLogicModeHandler extends StaticIOModeHandler {

    private final List<LogicExpression> expressions = new ArrayList<>();

    public BasicLogicModeHandler(List<Side> inputs, List<Side> outputs, SignalType signalType) {
        super(inputs, outputs, signalType);
    }

    public static BasicLogicModeHandler of(List<Side> inputs, List<Side> outputs, SignalType signalType, LogicExpression expression){
        return new BasicLogicModeHandler(inputs, outputs, signalType).add(expression);
    }

    public BasicLogicModeHandler add(LogicExpression expression){
        expressions.add(expression);

        return this;
    }

    @Override
    public int totalModes() {
        return expressions.size();
    }

    public LogicExpression getExpression(int mode){
        return expressions.get(mode);
    }
}
