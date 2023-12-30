package com.chyzman.chyzyLogistics.logic.api.handlers;

import com.chyzman.chyzyLogistics.logic.api.GateContext;
import com.chyzman.chyzyLogistics.logic.api.IOConfiguration;
import com.chyzman.chyzyLogistics.logic.api.Side;
import com.chyzman.chyzyLogistics.logic.api.mode.ExpressionModeHandler;
import com.chyzman.chyzyLogistics.logic.GateMathUtils;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SingleOutputGateHandler extends StaticIOGateHandler {

    private final ExpressionModeHandler modeHandler;

    public SingleOutputGateHandler(Identifier id, List<Side> inputs, List<Side> outputs, ExpressionModeHandler modeHandler) {
        super(id, inputs, outputs, modeHandler.interactEvent(), modeHandler.getSetup());

        this.modeHandler = modeHandler;
    }

    public static <H extends ExpressionModeHandler> SingleOutputGateHandler of(Identifier id, IOConfiguration configuration, H handler, Consumer<H> builder) {
        builder.accept(handler);

        return new SingleOutputGateHandler(id, configuration.inputs(), configuration.outputs(), handler);
    }

    public Map<Side, Integer> calculateOutputData(GateContext context, Map<Side, Integer> inputData) {
        var expression = modeHandler.getExpression(context.storage().getMode());

        return GateMathUtils.calculateSingleExpressionOutputData(this, expression, context, inputData);
    }
}
