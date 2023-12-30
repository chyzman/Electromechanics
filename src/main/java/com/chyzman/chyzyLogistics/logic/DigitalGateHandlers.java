package com.chyzman.chyzyLogistics.logic;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.chyzman.chyzyLogistics.logic.api.GateLogicFunction;
import com.chyzman.chyzyLogistics.logic.api.IOConfiguration;
import com.chyzman.chyzyLogistics.logic.api.Side;
import com.chyzman.chyzyLogistics.logic.api.SignalType;
import com.chyzman.chyzyLogistics.logic.api.handlers.GateHandler;
import com.chyzman.chyzyLogistics.logic.api.handlers.MultiOutputGateHandler;
import com.chyzman.chyzyLogistics.logic.api.handlers.SingleOutputGateHandler;
import com.chyzman.chyzyLogistics.logic.api.mode.ExpressionModeHandler;
import com.chyzman.chyzyLogistics.logic.api.mode.MultiExpressionModeHandler;
import com.chyzman.chyzyLogistics.mixin.ExpressionBuilderAccessor;
import net.minecraft.util.Identifier;
import net.objecthunter.exp4j.operator.Operator;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
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

    public static final GateHandler<?> BASE = monoGate(ChyzyLogistics.id("base"), (input) -> input);

    public static final GateHandler<?> AND = biGate(ChyzyLogistics.id("and"), (right, left) -> (right * left));
    public static final GateHandler<?> OR = biGate(ChyzyLogistics.id("or"), (right, left) -> (right + left));
    public static final GateHandler<?> XOR = biGate(ChyzyLogistics.id("xor"), (right, left) -> (right ^ left));

    public static final GateHandler<?> TRIPLE_AND = triGateExpression(ChyzyLogistics.id("tri_and"), "r*b*l");
    public static final GateHandler<?> TRIPLE_OR = triGate(ChyzyLogistics.id("tri_or"), (right, middle, left) -> (right + middle + left));
    public static final GateHandler<?> AND_THEN_OR = triGate(ChyzyLogistics.id("and_then_or"), (right, middle, left) -> ((right * middle) + left));
    public static final GateHandler<?> OR_THEN_AND = triGate(ChyzyLogistics.id("or_then_and"), (right, middle, left) -> ((right + middle) * left));

    public static final GateHandler<?> CROSS = MultiOutputGateHandler.of(
            ChyzyLogistics.id("cross"),
            new IOConfiguration(List.of(Side.LEFT, Side.BACK), List.of(Side.RIGHT, Side.FRONT)),
            new MultiExpressionModeHandler(SignalType.DIGITAL),
            multiExpressionModeHandler -> {
                multiExpressionModeHandler.add(consumer -> {
                    consumer.accept(
                            new IOConfiguration(Side.LEFT, Side.RIGHT),
                            GateLogicFunction.of((left) -> left)
                    );
                    consumer.accept(
                            new IOConfiguration(Side.BACK, Side.FRONT),
                            GateLogicFunction.of((front) -> front)
                    );
                });
            }
    );

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
                digitalExpressionInversion(new GateExpressionBuilder(expression).variable(Side.BACK)));
    }

    public static SingleOutputGateHandler biGateExpression(Identifier id, String expression){
        return SingleOutputGateHandler.of(id,
                IOConfigurations.BI_TO_MONO,
                new ExpressionModeHandler(SignalType.DIGITAL),
                digitalExpressionInversion(new GateExpressionBuilder(expression).variable(Side.LEFT, Side.RIGHT)));
    }

    public static SingleOutputGateHandler triGateExpression(Identifier id, String expression){
        return SingleOutputGateHandler.of(id,
                IOConfigurations.TRI_TO_MONO,
                new ExpressionModeHandler(SignalType.DIGITAL),
                digitalExpressionInversion(new GateExpressionBuilder(expression).variable(Side.LEFT, Side.BACK, Side.RIGHT)));
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
