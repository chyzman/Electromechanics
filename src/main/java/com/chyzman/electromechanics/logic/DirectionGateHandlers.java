package com.chyzman.electromechanics.logic;

import com.chyzman.electromechanics.ElectromechanicsLogistics;
import com.chyzman.electromechanics.block.gate.GateStateStorage;
import com.chyzman.electromechanics.logic.api.GateLogicFunction;
import com.chyzman.electromechanics.logic.api.IOConfiguration;
import com.chyzman.electromechanics.logic.api.Side;
import com.chyzman.electromechanics.logic.api.handlers.GateHandler;
import net.minecraft.util.ActionResult;

import java.util.List;
import java.util.Map;

public class DirectionGateHandlers {

//    public static final GateHandler CROSS = MultiOutputGateHandler.of(
//            ChyzyLogistics.id("cross"),
//            new IOConfiguration(List.of(Side.LEFT, Side.BACK), List.of(Side.RIGHT, Side.FRONT)),
//            new MultiExpressionModeHandler(SignalType.DIGITAL),
//            multiExpressionModeHandler -> {
//                multiExpressionModeHandler.add(consumer -> {
//                    consumer.accept(new IOConfiguration(Side.LEFT, Side.RIGHT), GateLogicFunction.of((left) -> left));
//                    consumer.accept(new IOConfiguration(Side.BACK, Side.FRONT), GateLogicFunction.of((front) -> front));
//                });
//            }
//    );

    public static final GateHandler DIRECTABLE = GateHandler.of(
            ElectromechanicsLogistics.id("directable_gate"),
            storage -> {
                var nextMode = storage.getMode() + 1;

                if(nextMode > 2) nextMode = 0;

                setOutput(storage, nextMode);

                storage.setMode(nextMode);

                return ActionResult.SUCCESS;
            },
            storage -> {
                var map = storage.dynamicStorage();

                setOutput(storage, storage.getMode());

                map.put(GateStateStorage.INPUTS, List.of(Side.BACK));
            },
            (handler, context, inputData) -> {
                return GateMathUtils.calculateSingleExpressionOutputData(handler, (context1, integers) -> integers[0], context, inputData);
            }
    );

    private static void setOutput(GateStateStorage storage, int mode){
        var map = storage.dynamicStorage();

        Side output = switch (mode){
            case 0 -> Side.FRONT;
            case 1 -> Side.RIGHT;
            default -> Side.LEFT;
        };

        storage.setMode(mode);

        map.put(GateStateStorage.OUTPUTS, List.of(output));
    }

    private static final List<IOConfiguration> CONFIGURATIONS = List.of(
            new IOConfiguration(List.of(Side.LEFT, Side.BACK), List.of(Side.RIGHT, Side.FRONT)),
            new IOConfiguration(List.of(Side.RIGHT, Side.BACK), List.of(Side.LEFT, Side.FRONT)),
            new IOConfiguration(List.of(Side.RIGHT, Side.FRONT), List.of(Side.LEFT, Side.BACK)),
            new IOConfiguration(List.of(Side.LEFT, Side.FRONT), List.of(Side.RIGHT, Side.BACK))
    );

    public static final GateHandler CROSS = GateHandler.of(
            ElectromechanicsLogistics.id("adv_cross_gate"),
            storage -> {
                var nextMode = storage.getMode() + 1;

                if(nextMode >= CONFIGURATIONS.size()) nextMode = 0;

                var ioConfig = CONFIGURATIONS.get(nextMode);

                var map = storage.dynamicStorage();

                storage.setMode(nextMode);

                map.put(GateStateStorage.INPUTS, ioConfig.inputs());
                map.put(GateStateStorage.OUTPUTS, ioConfig.outputs());

                return ActionResult.SUCCESS;
            },
            storage -> {
                var ioConfig = CONFIGURATIONS.get(storage.getMode());

                var map = storage.dynamicStorage();

                map.put(GateStateStorage.INPUTS, ioConfig.inputs());
                map.put(GateStateStorage.OUTPUTS, ioConfig.outputs());
            },
            (handler, context, inputData) -> {
                var map = context.storage().dynamicStorage();

                var inputs = map.get(GateStateStorage.INPUTS);
                var outputs = map.get(GateStateStorage.OUTPUTS);

                GateLogicFunction expression = (context1, integers) -> integers[0];

                var expressions = Map.of(
                        new IOConfiguration(inputs.get(0), outputs.get(0)), expression,
                        new IOConfiguration(inputs.get(1), outputs.get(1)), expression
                );

                return GateMathUtils.calculateMultiExpressionOutputData(handler, expressions, context, inputData);
            }
    );

}
