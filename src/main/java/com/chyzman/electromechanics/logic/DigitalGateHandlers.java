package com.chyzman.electromechanics.logic;

import com.chyzman.electromechanics.ElectromechanicsLogistics;
import com.chyzman.electromechanics.logic.api.GateLogicFunction;
import com.chyzman.electromechanics.logic.api.configuration.IOConfiguration;
import com.chyzman.electromechanics.logic.api.configuration.SignalType;
import com.chyzman.electromechanics.logic.api.GateHandler;
import com.chyzman.electromechanics.logic.api.mode.ExpressionModeHandler;
import com.chyzman.electromechanics.mixin.ExpressionBuilderAccessor;
import io.wispforest.owo.serialization.Endec;
import io.wispforest.owo.serialization.endec.KeyedEndec;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
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

    public static final GateHandler REPEATER = monoGate(ElectromechanicsLogistics.id("repeater"), "0-1", (input) -> input);

    public static final GateHandler AND = biGate(ElectromechanicsLogistics.id("and"), "&&", (right, left) -> (right * left));
    public static final GateHandler OR = biGate(ElectromechanicsLogistics.id("or"), "||", (right, left) -> (right + left));
    public static final GateHandler XOR = biGate(ElectromechanicsLogistics.id("xor"), "⊕", (right, left) -> (right ^ left));

    public static final GateHandler TRIPLE_AND = triGateExpression(ElectromechanicsLogistics.id("tri_and"), "&&&", "r*b*l");
    public static final GateHandler TRIPLE_OR = triGate(ElectromechanicsLogistics.id("tri_or"), "|||", (right, middle, left) -> (right + middle + left));
    public static final GateHandler AND_THEN_OR = triGate(ElectromechanicsLogistics.id("and_then_or"), "&&|", (right, middle, left) -> ((right * middle) + left));
    public static final GateHandler OR_THEN_AND = triGate(ElectromechanicsLogistics.id("or_then_and"), "||&", (right, middle, left) -> ((right + middle) * left));

    private static final KeyedEndec<Boolean> IS_FLOPPED = Endec.BOOLEAN.keyed("IsFlopped", false);
    private static final KeyedEndec<Boolean> FLOP_LOCK = Endec.BOOLEAN.keyed("FlopLock", false);

    public static GateHandler T_FLIP_FLOP = GateHandler.singleExpression(
            ElectromechanicsLogistics.id("t_flip_flop"), "TFF",
            IOConfigurations.MONO_TO_MONO,
            Util.make(
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
            )
    );

    public static GateHandler monoGate(Identifier id, String displaySymbol, Function<Integer, Integer> func){
        var handler = Util.make(new ExpressionModeHandler(SignalType.DIGITAL), digitalInversion(GateLogicFunction.of(func)));

        return GateHandler.singleExpression(id, displaySymbol, IOConfigurations.MONO_TO_MONO, handler);
    }

    public static GateHandler biGate(Identifier id, String displaySymbol, BiFunction<Integer, Integer, Integer> func){
        var handler = Util.make(new ExpressionModeHandler(SignalType.DIGITAL), digitalInversion(GateLogicFunction.of(func)));

        return GateHandler.singleExpression(id, displaySymbol, IOConfigurations.BI_TO_MONO, handler);
    }

    public static GateHandler triGate(Identifier id, String displaySymbol, TriFunction<Integer, Integer, Integer, Integer> func){
        var handler = Util.make(new ExpressionModeHandler(SignalType.DIGITAL), digitalInversion(GateLogicFunction.of(func)));

        return GateHandler.singleExpression(id, displaySymbol, IOConfigurations.TRI_TO_MONO, handler);
    }

    private static Consumer<ExpressionModeHandler> digitalInversion(GateLogicFunction logicExpression){
        return handler -> handler.add(logicExpression)
                .add((context, integers) -> BooleanUtils.toInteger(logicExpression.apply(context, integers) == 0));
    }

    //--

    public static GateHandler monoGateExpression(Identifier id, String displaySymbol, String expression){
        return gateExpression(id, displaySymbol, expression, IOConfigurations.MONO_TO_MONO);
    }

    public static GateHandler biGateExpression(Identifier id, String displaySymbol, String expression){
        return gateExpression(id, displaySymbol, expression, IOConfigurations.BI_TO_MONO);
    }

    public static GateHandler triGateExpression(Identifier id, String displaySymbol, String expression){
        return gateExpression(id, displaySymbol, expression, IOConfigurations.TRI_TO_MONO);
    }

    public static GateHandler gateExpression(Identifier id, String displaySymbol, String expression, IOConfiguration ioConfiguration){
        var handler = Util.make(
                new ExpressionModeHandler(SignalType.DIGITAL),
                digitalInversion(new GateExpressionBuilder(expression).variable(ioConfiguration.inputs()))
        );

        return GateHandler.singleExpression(id, displaySymbol, ioConfiguration, handler);
    }

    private static Consumer<ExpressionModeHandler> digitalInversion(GateExpressionBuilder builder){
        String expression = ((ExpressionBuilderAccessor) builder).chyz$getExpression();

        var invertedBuilder = new GateExpressionBuilder("!(" + expression + ")")
                .variable(builder.getInputs())
                .operator(booleanInversion);

        return handler -> handler.add(new Exp4jGateLogic(builder.getInputs(), builder.build()))
                .add(new Exp4jGateLogic(builder.getInputs(), invertedBuilder.build()));
    }

    //--
}
