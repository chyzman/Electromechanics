package com.chyzman.electromechanics.logic;

import com.chyzman.electromechanics.Electromechanics;
import com.chyzman.electromechanics.logic.api.GateHandler;
import com.chyzman.electromechanics.logic.api.GateLogicFunction;
import com.chyzman.electromechanics.logic.api.GateOutputFunction;
import com.chyzman.electromechanics.logic.api.configuration.IOConfiguration;
import com.chyzman.electromechanics.logic.api.configuration.Side;
import com.chyzman.electromechanics.logic.api.state.GateStateStorage;
import net.minecraft.util.ActionResult;

import java.util.List;
import java.util.Map;

public class DirectionGateHandlers {

    public static final GateHandler DIRECTABLE = GateHandler.of(
            Electromechanics.id("directable_gate"), "⤷",
            GateOutputFunction.singleExpression((context1, integers) -> integers[0]),
            storage -> {
                var map = storage.dynamicStorage();

                setOutput(storage, storage.getMode());

                map.put(GateStateStorage.INPUTS, List.of(Side.BACK));
            },
            storage -> {
                var nextMode = storage.getMode() + 1;

                if(nextMode > 2) nextMode = 0;

                setOutput(storage, nextMode);

                storage.setMode(nextMode);

                return ActionResult.SUCCESS;
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
            Electromechanics.id("adv_cross_gate"), "⤧",
            GateOutputFunction.multiExpression(context -> {
                var map = context.storage().dynamicStorage();

                var inputs = map.get(GateStateStorage.INPUTS);
                var outputs = map.get(GateStateStorage.OUTPUTS);

                GateLogicFunction expression = (context1, integers) -> integers[0];

                return Map.of(
                        new IOConfiguration(inputs.get(0), outputs.get(0)), expression,
                        new IOConfiguration(inputs.get(1), outputs.get(1)), expression
                );
            }),
            storage -> {
                var ioConfig = CONFIGURATIONS.get(storage.getMode());

                var map = storage.dynamicStorage();

                map.put(GateStateStorage.INPUTS, ioConfig.inputs());
                map.put(GateStateStorage.OUTPUTS, ioConfig.outputs());
            },
            storage -> {
                var nextMode = storage.getMode() + 1;

                if(nextMode >= CONFIGURATIONS.size()) nextMode = 0;

                var ioConfig = CONFIGURATIONS.get(nextMode);

                var map = storage.dynamicStorage();

                storage.setMode(nextMode);

                map.put(GateStateStorage.INPUTS, ioConfig.inputs());
                map.put(GateStateStorage.OUTPUTS, ioConfig.outputs());

                return ActionResult.SUCCESS;
            }
    );

}
