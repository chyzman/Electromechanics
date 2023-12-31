package com.chyzman.electromechanics.logic.api.handlers;

import com.chyzman.electromechanics.logic.GateMathUtils;
import com.chyzman.electromechanics.logic.api.GateContext;
import com.chyzman.electromechanics.logic.api.IOConfiguration;
import com.chyzman.electromechanics.logic.api.Side;
import com.chyzman.electromechanics.logic.api.mode.MultiExpressionModeHandler;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MultiOutputGateHandler extends StaticIOGateHandler {

    private final MultiExpressionModeHandler modeHandler;

    public MultiOutputGateHandler(Identifier id, List<Side> inputs, List<Side> outputs, MultiExpressionModeHandler modeHandler) {
        super(id, inputs, outputs, modeHandler.interactEvent(), modeHandler.getSetup());

        this.modeHandler = modeHandler;
    }

    public static <H extends MultiExpressionModeHandler> MultiOutputGateHandler of(Identifier id, IOConfiguration configuration, H handler, Consumer<H> builder) {
        builder.accept(handler);

        return new MultiOutputGateHandler(id, configuration.inputs(), configuration.outputs(), handler);
    }

    public Map<Side, Integer> calculateOutputData(GateContext context, Map<Side, Integer> inputData) {
        var expressions = modeHandler.getExpression(context.storage().getMode());

        return GateMathUtils.calculateMultiExpressionOutputData(this, expressions, context, inputData);
    }
}
