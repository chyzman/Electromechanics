package com.chyzman.chyzyLogistics.logic;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.logic.DefaultModeHandlers;
import com.chyzman.chyzyLogistics.logic.GateHandler;
import com.chyzman.chyzyLogistics.logic.Side;
import com.chyzman.chyzyLogistics.logic.SignalType;
import com.chyzman.chyzyLogistics.logic.mode.BasicLogicModeHandler;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AnalogGateHandlers {

    public static GateHandler GATE = GateHandler.of(ChyzyLogistics.id("repeater"),
            DefaultModeHandlers.monoToMono(SignalType.ANALOG, BasicLogicModeHandler::new),
            handler -> {
                handler.add((context, integers) -> integers[0])
                        .add((context, integers) -> 15 - integers[0]);
            }
    );

    public static GateHandler ADDITION = biGate(ChyzyLogistics.id("addition"), (left, right) -> left + right);
    public static GateHandler SUBTRACTION = biGate(ChyzyLogistics.id("subtraction"), (left, right) -> left - right);
    public static GateHandler MULTIPLICATION = biGate(ChyzyLogistics.id("multiplication"), (left, right) -> left * right);
    public static GateHandler DIVISION = biGate(ChyzyLogistics.id("division"), (left, right) -> left / right);

    public static GateHandler MODULUS = GateHandler.of(ChyzyLogistics.id("modulus"),
            new BasicLogicModeHandler(List.of(Side.BACK, Side.RIGHT), List.of(Side.FRONT), SignalType.ANALOG),
            handler -> handler.add((context, integers) -> {
                var mod = integers[1];

                if(mod == 0) return 0;

                return integers[0] % mod;
            })
    );

    public static GateHandler monoGate(Identifier id, Function<Integer, Integer> func){
        return GateHandler.of(id,
                DefaultModeHandlers.monoToMono(SignalType.ANALOG, BasicLogicModeHandler::new),
                handler -> handler.add((context, integers) -> func.apply(integers[0])));
    }

    public static GateHandler biGate(Identifier id, BiFunction<Integer, Integer, Integer> func){
        return GateHandler.of(id,
                DefaultModeHandlers.biToMono(SignalType.ANALOG, BasicLogicModeHandler::new),
                handler -> handler.add((context, integers) -> func.apply(integers[0], integers[1])));
    }

    public static GateHandler triGate(Identifier id, TriFunction<Integer, Integer, Integer, Integer> func){
        return GateHandler.of(id,
                DefaultModeHandlers.triToMono(SignalType.ANALOG, BasicLogicModeHandler::new),
                handler -> handler.add((context, integers) -> func.apply(integers[0], integers[1], integers[2])));
    }
}
