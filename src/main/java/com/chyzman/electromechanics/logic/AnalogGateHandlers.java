package com.chyzman.electromechanics.logic;

import com.chyzman.electromechanics.ElectromechanicsLogistics;
import com.chyzman.electromechanics.logic.api.GateLogicFunction;
import com.chyzman.electromechanics.logic.api.SignalType;
import com.chyzman.electromechanics.logic.api.handlers.GateHandler;
import com.chyzman.electromechanics.logic.api.handlers.SingleOutputGateHandler;
import com.chyzman.electromechanics.logic.api.mode.ExpressionModeHandler;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AnalogGateHandlers {

    public static GateHandler GATE = SingleOutputGateHandler.of(ElectromechanicsLogistics.id("repeater"),
            IOConfigurations.MONO_TO_MONO,
            new ExpressionModeHandler(SignalType.ANALOG),
            handler -> {
                handler.add((context, integers) -> integers[0])
                        .add((context, integers) -> 15 - integers[0]);
            }
    );

    public static GateHandler ADDITION = biGate(ElectromechanicsLogistics.id("addition"), (left, right) -> left + right).displaySymbol("+");
    public static GateHandler SUBTRACTION = biGate(ElectromechanicsLogistics.id("subtraction"), (left, right) -> left - right).displaySymbol("-");
    public static GateHandler MULTIPLICATION = biGate(ElectromechanicsLogistics.id("multiplication"), (left, right) -> left * right).displaySymbol("*");
    public static GateHandler DIVISION = biGate(ElectromechanicsLogistics.id("division"), (left, right) -> left / right).displaySymbol("/");

    public static GateHandler MODULUS = SingleOutputGateHandler.of(ElectromechanicsLogistics.id("modulus"),
            IOConfigurations.BI_TO_MONO,
            new ExpressionModeHandler(SignalType.ANALOG),
            handler -> handler.add((context, integers) -> {
                var input = integers[0];
                var mod = integers[1];

                if(mod == 0) return 0;

                return input % mod;
            })
    ).displaySymbol("%");

    private static final KeyedEndec<Integer> COUNT = Endec.INT.keyed("Count", 0);
    private static final KeyedEndec<Boolean> COUNT_LOCK = Endec.BOOLEAN.keyed("CountLock", false);

    public static GateHandler COUNTER = SingleOutputGateHandler.of(
            ElectromechanicsLogistics.id("counter"),
            IOConfigurations.MONO_TO_MONO,
            new ExpressionModeHandler(SignalType.DIGITAL, SignalType.ANALOG),
            handler -> {
                handler.add((context, integers) -> {
                    var map = context.storage().dynamicStorage();

                    var count = map.get(COUNT);
                    var countLock = map.get(COUNT_LOCK);

                    if(integers[0] > 0){
                        if(countLock) return count;

                        count++;

                        if(count > 15) count = 0;

                        map.put(COUNT, count);
                        map.put(COUNT_LOCK, true);
                    } else {
                        map.put(COUNT_LOCK, false);
                    }

                    return count;
                });
            }
    ).displaySymbol("i++");

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
