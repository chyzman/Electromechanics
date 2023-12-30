package com.chyzman.chyzyLogistics.logic.api.handlers;

import com.chyzman.chyzyLogistics.logic.api.GateContext;
import com.chyzman.chyzyLogistics.logic.api.IOConfiguration;
import com.chyzman.chyzyLogistics.logic.api.Side;
import com.chyzman.chyzyLogistics.logic.api.mode.MultiExpressionModeHandler;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MultiOutputGateHandler extends StaticIOGateHandler<MultiExpressionModeHandler> {

    public MultiOutputGateHandler(Identifier id, List<Side> inputs, List<Side> outputs, MultiExpressionModeHandler modeHandler) {
        super(id, inputs, outputs, modeHandler);
    }

    public static <H extends MultiExpressionModeHandler> MultiOutputGateHandler of(Identifier id, IOConfiguration configuration, H handler, Consumer<H> builder) {
        builder.accept(handler);

        return new MultiOutputGateHandler(id, configuration.inputs(), configuration.outputs(), handler);
    }

    public Map<Side, Integer> calculateOutputData(GateContext context, Map<Side, Integer> inputData) {
        Map<Side, Integer> outputData = new HashMap<>();

        for (var entry : modeHandler.getExpression(context.storage().getMode()).entrySet()) {
            var ioConfig = entry.getKey();

            var inputs = ioConfig.inputs();

            Integer[] integers = new Integer[inputs.size()];

            for (int i = 0; i < inputs.size(); i++) {
                var side = inputs.get(i);

                integers[i] = this.getInputType(context, side).evaluateInput(inputData.get(side));
            }

            Integer outputPower = entry.getValue().apply(context, integers);

            for (Side output : ioConfig.outputs()) {
                outputData.put(output, getOutputType(context, output).evaluateOutput(outputPower));
            }
        }

        return outputData;
    }
}
