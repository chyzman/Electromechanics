package com.chyzman.chyzyLogistics.logic;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.logic.api.GateLogicFunction;
import com.chyzman.chyzyLogistics.logic.api.SignalType;
import com.chyzman.chyzyLogistics.logic.api.handlers.GateHandler;
import com.chyzman.chyzyLogistics.logic.api.handlers.SingleOutputGateHandler;
import com.chyzman.chyzyLogistics.logic.api.mode.ExpressionModeHandler;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AnalogGateHandlers {

    public static GateHandler<?> GATE = SingleOutputGateHandler.of(ChyzyLogistics.id("repeater"),
            IOConfigurations.MONO_TO_MONO,
            new ExpressionModeHandler(SignalType.ANALOG),
            handler -> {
                handler.add((context, integers) -> integers[0])
                        .add((context, integers) -> 15 - integers[0]);
            }
    );

    public static GateHandler<?> ADDITION = biGate(ChyzyLogistics.id("addition"), (left, right) -> left + right);
    public static GateHandler<?> SUBTRACTION = biGate(ChyzyLogistics.id("subtraction"), (left, right) -> left - right);
    public static GateHandler<?> MULTIPLICATION = biGate(ChyzyLogistics.id("multiplication"), (left, right) -> left * right);
    public static GateHandler<?> DIVISION = biGate(ChyzyLogistics.id("division"), (left, right) -> left / right);

    public static GateHandler<?> MODULUS = SingleOutputGateHandler.of(ChyzyLogistics.id("modulus"),
            IOConfigurations.BI_TO_MONO,
            new ExpressionModeHandler(SignalType.ANALOG),
            handler -> handler.add((context, integers) -> {
                var input = integers[0];
                var mod = integers[1];

                if(mod == 0) return 0;

                return input % mod;
            })
    );

    public static SingleOutputGateHandler monoGate(Identifier id, Function<Integer, Integer> func){
        return SingleOutputGateHandler.of(id,
                IOConfigurations.MONO_TO_MONO,
                new ExpressionModeHandler(SignalType.ANALOG),
                handler -> handler.add(GateLogicFunction.of(func)));
    }

    public static SingleOutputGateHandler biGate(Identifier id, BiFunction<Integer, Integer, Integer> func){
        return SingleOutputGateHandler.of(id,
                IOConfigurations.BI_TO_MONO,
                new ExpressionModeHandler(SignalType.ANALOG),
                handler -> handler.add(GateLogicFunction.of(func)));
    }

    public static SingleOutputGateHandler triGate(Identifier id, TriFunction<Integer, Integer, Integer, Integer> func){
        return SingleOutputGateHandler.of(id,
                IOConfigurations.TRI_TO_MONO,
                new ExpressionModeHandler(SignalType.ANALOG),
                handler -> handler.add(GateLogicFunction.of(func)));
    }
}
