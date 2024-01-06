package com.chyzman.electromechanics.logic;

import com.chyzman.electromechanics.Electromechanics;
import com.chyzman.electromechanics.logic.api.GateLogicFunction;
import com.chyzman.electromechanics.logic.api.configuration.SignalType;
import com.chyzman.electromechanics.logic.api.GateHandler;
import com.chyzman.electromechanics.logic.api.mode.ExpressionModeHandler;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AnalogGateHandlers {

    public static GateHandler GATE = GateHandler.singleExpression(Electromechanics.id("analog_gate"), "0-15",
            IOConfigurations.MONO_TO_MONO,
            Util.make(
                new ExpressionModeHandler(SignalType.ANALOG),
                handler -> {
                    handler.add((context, integers) -> integers[0])
                            .add((context, integers) -> 15 - integers[0]);
                }
            )
    );

    public static GateHandler ADDITION = biGate(Electromechanics.id("addition"), "+", (left, right) -> left + right);
    public static GateHandler SUBTRACTION = biGate(Electromechanics.id("subtraction"), "-", (left, right) -> left - right);
    public static GateHandler MULTIPLICATION = biGate(Electromechanics.id("multiplication"), "x", (left, right) -> left * right);
    public static GateHandler DIVISION = biGate(Electromechanics.id("division"), "/", (left, right) -> right != 0 ? left / right : 0);

    public static GateHandler MODULUS = GateHandler.singleExpression(Electromechanics.id("modulus"), "%",
            IOConfigurations.BI_TO_MONO,
            Util.make(
                    new ExpressionModeHandler(SignalType.ANALOG),
                    handler -> handler.add((context, integers) -> {
                        var mod = integers[1];

                        return (mod != 0) ? integers[0] % mod : 0;
                    })
            )
    );

    private static final KeyedEndec<Integer> COUNT = Endec.INT.keyed("Count", 0);
    private static final KeyedEndec<Boolean> COUNT_LOCK = Endec.BOOLEAN.keyed("CountLock", false);

    public static GateHandler COUNTER = GateHandler.singleExpression(
            Electromechanics.id("counter"), "i++",
            IOConfigurations.MONO_TO_MONO,
            Util.make(
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
            )
    );

    public static GateHandler monoGate(Identifier id, String displaySymbol, Function<Integer, Integer> func){
        return GateHandler.singleExpression(id,
                displaySymbol,
                IOConfigurations.MONO_TO_MONO,
                new ExpressionModeHandler(SignalType.ANALOG).add(GateLogicFunction.of(func)));
    }

    public static GateHandler biGate(Identifier id, String displaySymbol, BiFunction<Integer, Integer, Integer> func){
        return GateHandler.singleExpression(id,
                displaySymbol,
                IOConfigurations.BI_TO_MONO,
                new ExpressionModeHandler(SignalType.ANALOG).add(GateLogicFunction.of(func)));
    }

    public static GateHandler triGate(Identifier id, String displaySymbol, TriFunction<Integer, Integer, Integer, Integer> func){
        return GateHandler.singleExpression(id,
                displaySymbol,
                IOConfigurations.TRI_TO_MONO,
                new ExpressionModeHandler(SignalType.ANALOG).add(GateLogicFunction.of(func)));
    }
}
