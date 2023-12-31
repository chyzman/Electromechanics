package com.chyzman.electromechanics.logic.api.mode;

import com.chyzman.electromechanics.block.gate.GateStateStorage;
import com.chyzman.electromechanics.logic.api.GateInteractEvent;
import com.chyzman.electromechanics.logic.api.GateLogicFunction;
import com.chyzman.electromechanics.logic.api.IOConfiguration;
import com.chyzman.electromechanics.logic.api.SignalType;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MultiExpressionModeHandler extends StaticSignalTypeModeHandler implements GateInteractEvent {

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
