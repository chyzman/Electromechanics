package com.chyzman.electromechanics.logic;

import com.chyzman.electromechanics.logic.api.GateContext;
import com.chyzman.electromechanics.logic.api.GateLogicFunction;
import com.chyzman.electromechanics.logic.api.IOConfiguration;
import com.chyzman.electromechanics.logic.api.Side;
import com.chyzman.electromechanics.logic.api.handlers.GateHandler;

import java.util.HashMap;
import java.util.Map;

public class GateMathUtils {

    public static boolean isOutputtingPower(Map<Side, Integer> outputData){
        return outputData.values().stream().anyMatch(integer -> integer > 0);
    }

    public static int getOutputPower(Map<Side, Integer> outputData, Side side){
        if(!outputData.containsKey(side)) return 0;

        return outputData.get(side);
    }

    public boolean isOutputtingPower(Map<Side, Integer> outputData, Side side){
        return getOutputPower(outputData, side) > 0;
    }

    //--

    public static Map<Side, Integer> calculateSingleExpressionOutputData(GateHandler handler, GateLogicFunction function, GateContext context, Map<Side, Integer> inputData) {
        var inputs = handler.getInputs(context.storage());

        Integer[] integers = new Integer[inputData.size()];

        for (int i = 0; i < inputs.size(); i++) {
            var side = inputs.get(i);

            integers[i] = handler.getSideSignalType(context.storage(), side).evaluateInput(inputData.get(side));
        }

        Integer outputPower = function.apply(context, integers);

        Map<Side, Integer> outputData = new HashMap<>();

        for (Side output : handler.getOutputs(context.storage())) {
            outputData.put(output, handler.getSideSignalType(context.storage(), output).evaluateOutput(outputPower));
        }

        return outputData;
    }

    public static Map<Side, Integer> calculateMultiExpressionOutputData(GateHandler handler, Map<IOConfiguration, GateLogicFunction> functions, GateContext context, Map<Side, Integer> inputData) {
        Map<Side, Integer> outputData = new HashMap<>();

        for (var entry : functions.entrySet()) {
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
    }
}
