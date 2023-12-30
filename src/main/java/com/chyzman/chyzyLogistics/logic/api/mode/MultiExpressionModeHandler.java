package com.chyzman.chyzyLogistics.logic.api.mode;

import com.chyzman.chyzyLogistics.logic.api.GateLogicFunction;
import com.chyzman.chyzyLogistics.logic.api.IOConfiguration;
import com.chyzman.chyzyLogistics.logic.api.SignalType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MultiExpressionModeHandler extends StaticSignalTypeModeHandler {

    List<Map<IOConfiguration, GateLogicFunction>> expressions = new ArrayList<>();

    public MultiExpressionModeHandler(SignalType signalType){
        super(signalType);
    }

    public MultiExpressionModeHandler add(Consumer<BiConsumer<IOConfiguration, GateLogicFunction>> builder){
        var map = new HashMap<IOConfiguration, GateLogicFunction>();

        builder.accept(map::put);

        expressions.add(map);

        return this;
    }

    public MultiExpressionModeHandler add(Map<IOConfiguration, GateLogicFunction> mappedExpressions){
        expressions.add(mappedExpressions);

        return this;
    }

    public Map<IOConfiguration, GateLogicFunction> getExpression(int mode){
        return expressions.get(mode);
    }

    @Override
    public int totalModes() {
        return expressions.size();
    }
}
