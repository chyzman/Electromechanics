package com.chyzman.electromechanics.logic.api;

import com.chyzman.electromechanics.logic.api.state.GateContext;
import com.chyzman.electromechanics.logic.api.configuration.IOConfiguration;
import com.chyzman.electromechanics.logic.api.configuration.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface GateOutputFunction {

    Map<Side, Integer> calculateOutput(GateHandler handler, GateContext context, Map<Side, Integer> inputData);

    static GateOutputFunction singleExpression(GateLogicFunction logicFunc){
        return singleExpression(context -> logicFunc);
    }

    static GateOutputFunction singleExpression(Function<GateContext, GateLogicFunction> func){
        return (handler, context, inputData) -> {
            var inputs = handler.getInputs(context.storage());

            Integer[] integers = new Integer[inputData.size()];

            for (int i = 0; i < inputs.size(); i++) {
                var side = inputs.get(i);

                integers[i] = handler.getSideSignalType(context.storage(), side).evaluateInput(inputData.get(side));
            }

            var logicExpression = func.apply(context);

            Integer outputPower = logicExpression.apply(context, integers);

            Map<Side, Integer> outputData = new HashMap<>();

            for (Side output : handler.getOutputs(context.storage())) {
                outputData.put(output, handler.getSideSignalType(context.storage(), output).evaluateOutput(outputPower));
            }

            return outputData;
        };
    }

    static GateOutputFunction multiExpression(Map<IOConfiguration, GateLogicFunction> logicFuncs){
        return multiExpression(context -> logicFuncs);
    }

    static GateOutputFunction multiExpression(Function<GateContext, Map<IOConfiguration, GateLogicFunction>> func){
        return (handler, context, inputData) -> {
            Map<Side, Integer> outputData = new HashMap<>();

            var logicExpressions = func.apply(context);

            for (var entry : logicExpressions.entrySet()) {
                var ioConfig = entry.getKey();

                var inputs = ioConfig.inputs();

                Integer[] integers = new Integer[inputs.size()];

                for (int i = 0; i < inputs.size(); i++) {
                    var side = inputs.get(i);

                    integers[i] = handler.getSideSignalType(context.storage(), side).evaluateInput(inputData.get(side));
                }

                Integer outputPower = entry.getValue().apply(context, integers);

                for (Side output : ioConfig.outputs()) {
                    outputData.put(output, handler.getSideSignalType(context.storage(), output).evaluateOutput(outputPower));
                }
            }

            return outputData;
        };
    }
}
