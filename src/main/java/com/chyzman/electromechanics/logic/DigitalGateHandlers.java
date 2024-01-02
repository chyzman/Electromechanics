package com.chyzman.electromechanics.logic;

import com.chyzman.electromechanics.ElectromechanicsLogistics;
import com.chyzman.electromechanics.logic.api.*;
import com.chyzman.electromechanics.logic.api.handlers.GateHandler;
import com.chyzman.electromechanics.logic.api.handlers.SingleOutputGateHandler;
import com.chyzman.electromechanics.logic.api.mode.ExpressionModeHandler;
import com.chyzman.electromechanics.mixin.ExpressionBuilderAccessor;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import net.minecraft.util.Identifier;
import net.objecthunter.exp4j.operator.Operator;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class DigitalGateHandlers {

    private static final Operator booleanInversion = new Operator("!", 1, true, 1) {
        @Override
        public double apply(double... args) {
            return BooleanUtils.toInteger(args[0] != 1);
        }
    };

    public static final GateHandler REPEATER = monoGate(ElectromechanicsLogistics.id("repeater"), (input) -> input).displaySymbol("0-1");

    public static final GateHandler AND = biGate(ElectromechanicsLogistics.id("and"), (right, left) -> (right * left)).displaySymbol("&&");
    public static final GateHandler OR = biGate(ElectromechanicsLogistics.id("or"), (right, left) -> (right + left)).displaySymbol("||");
    public static final GateHandler XOR = biGate(ElectromechanicsLogistics.id("xor"), (right, left) -> (right ^ left)).displaySymbol("⊕");

    public static final GateHandler TRIPLE_AND = triGateExpression(ElectromechanicsLogistics.id("tri_and"), "r*b*l").displaySymbol("&&&");
    public static final GateHandler TRIPLE_OR = triGate(ElectromechanicsLogistics.id("tri_or"), (right, middle, left) -> (right + middle + left)).displaySymbol("|||");
    public static final GateHandler AND_THEN_OR = triGate(ElectromechanicsLogistics.id("and_then_or"), (right, middle, left) -> ((right * middle) + left)).displaySymbol("&&|");
    public static final GateHandler OR_THEN_AND = triGate(ElectromechanicsLogistics.id("or_then_and"), (right, middle, left) -> ((right + middle) * left)).displaySymbol("||&");

    private static final KeyedEndec<Boolean> IS_FLOPPED = Endec.BOOLEAN.keyed("IsFlopped", false);
    private static final KeyedEndec<Boolean> FLOP_LOCK = Endec.BOOLEAN.keyed("FlopLock", false);

    public static GateHandler T_FLIP_FLOP = SingleOutputGateHandler.of(
            ElectromechanicsLogistics.id("t_flip_flop"),
            IOConfigurations.MONO_TO_MONO,
            new ExpressionModeHandler(SignalType.DIGITAL, SignalType.DIGITAL),
            handler -> {
                handler.add((context, integers) -> {
                    var map = context.storage().dynamicStorage();

                    var isFlopped = map.get(IS_FLOPPED);
                    var flopLock = map.get(FLOP_LOCK);

                    var powered = integers[0] > 0;

                    if(powered){
                        if(flopLock) return BooleanUtils.toInteger(isFlopped);

                        isFlopped = !isFlopped;

                        map.put(IS_FLOPPED, isFlopped);
                        map.put(FLOP_LOCK, true);
                    } else {
                        map.put(FLOP_LOCK, false);
                    }

                    return BooleanUtils.toInteger(isFlopped);
                });
            }
    ).displaySymbol("TFF");

    public static SingleOutputGateHandler monoGate(Identifier id, Function<Integer, Integer> func){
        return SingleOutputGateHandler.of(id,
                IOConfigurations.MONO_TO_MONO,
                new ExpressionModeHandler(SignalType.DIGITAL),
                digitalInversion(GateLogicFunction.of(func)));
    }

    public static SingleOutputGateHandler biGate(Identifier id, BiFunction<Integer, Integer, Integer> func){
        return SingleOutputGateHandler.of(id,
                IOConfigurations.BI_TO_MONO,
                new ExpressionModeHandler(SignalType.DIGITAL),
                digitalInversion(GateLogicFunction.of(func)));
    }

    public static SingleOutputGateHandler triGate(Identifier id, TriFunction<Integer, Integer, Integer, Integer> func){
        return SingleOutputGateHandler.of(id,
                IOConfigurations.TRI_TO_MONO,
                new ExpressionModeHandler(SignalType.DIGITAL),
                digitalInversion(GateLogicFunction.of(func)));
    }

    private static Consumer<ExpressionModeHandler> digitalInversion(GateLogicFunction logicExpression){
        GateLogicFunction invertedBuilder = (context, integers) -> BooleanUtils.toInteger(logicExpression.apply(context, integers) == 0);

        return handler -> handler.add(logicExpression)
                .add(invertedBuilder);
    }

    //--

    public static SingleOutputGateHandler monoGateExpression(Identifier id, String expression){
        return SingleOutputGateHandler.of(id,
                IOConfigurations.MONO_TO_MONO,
                new ExpressionModeHandler(SignalType.DIGITAL),
                digitalExpressionInversion(new GateExpressionBuilder(expression).variable(IOConfigurations.MONO_TO_MONO.inputs())));
    }

    public static SingleOutputGateHandler biGateExpression(Identifier id, String expression){
        return SingleOutputGateHandler.of(id,
                IOConfigurations.BI_TO_MONO,
                new ExpressionModeHandler(SignalType.DIGITAL),
                digitalExpressionInversion(new GateExpressionBuilder(expression).variable(IOConfigurations.BI_TO_MONO.inputs())));
    }

    public static SingleOutputGateHandler triGateExpression(Identifier id, String expression){
        return SingleOutputGateHandler.of(id,
                IOConfigurations.TRI_TO_MONO,
                new ExpressionModeHandler(SignalType.DIGITAL),
                digitalExpressionInversion(new GateExpressionBuilder(expression).variable(IOConfigurations.TRI_TO_MONO.inputs())));
    }

    private static Consumer<ExpressionModeHandler> digitalExpressionInversion(GateExpressionBuilder builder){
        String expression = ((ExpressionBuilderAccessor) builder).chyz$getExpression();

        var invertedBuilder = new GateExpressionBuilder("!(" + expression + ")")
                .variable(builder.getInputs())
                .operator(booleanInversion);

        return handler -> handler.add(new Exp4jGateLogic(builder.getInputs(), builder.build()))
                .add(new Exp4jGateLogic(builder.getInputs(), invertedBuilder.build()));
    }

    //--
}
