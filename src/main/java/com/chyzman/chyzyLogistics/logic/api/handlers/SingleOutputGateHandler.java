package com.chyzman.chyzyLogistics.logic.api.handlers;

import com.chyzman.chyzyLogistics.logic.api.GateContext;
import com.chyzman.chyzyLogistics.logic.api.IOConfiguration;
import com.chyzman.chyzyLogistics.logic.api.Side;
import com.chyzman.chyzyLogistics.logic.api.mode.ExpressionModeHandler;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SingleOutputGateHandler extends StaticIOGateHandler<ExpressionModeHandler> {

    public SingleOutputGateHandler(Identifier id, List<Side> inputs, List<Side> outputs, ExpressionModeHandler modeHandler) {
        super(id, inputs, outputs, modeHandler);
    }

    public static <H extends ExpressionModeHandler> SingleOutputGateHandler of(Identifier id, IOConfiguration configuration, H handler, Consumer<H> builder) {
        builder.accept(handler);

        return new SingleOutputGateHandler(id, configuration.inputs(), configuration.outputs(), handler);
    }

    public Map<Side, Integer> calculateOutputData(GateContext context, Map<Side, Integer> inputData) {
        var inputs = this.getInputs(context.storage());

        Integer[] integers = new Integer[inputData.size()];

        for (int i = 0; i < inputs.size(); i++) {
            var side = inputs.get(i);

            integers[i] = this.getInputType(context, side).evaluateInput(inputData.get(side));
        }

        Integer outputPower = modeHandler.getExpression(context.storage().getMode()).apply(context, integers);

        Map<Side, Integer> outputData = new HashMap<>();

        for (Side output : getOutputs(context.storage())) {
            outputData.put(output, getOutputType(context, output).evaluateOutput(outputPower));
        }

        return outputData;
    }
}
