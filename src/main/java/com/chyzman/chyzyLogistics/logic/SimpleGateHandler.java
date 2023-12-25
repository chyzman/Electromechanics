package com.chyzman.chyzyLogistics.logic;

import com.chyzman.chyzyLogistics.ChyzyLogistics;
import com.mojang.datafixers.util.Function4;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.function.TriFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class SimpleGateHandler extends AbstractGateHandler {

    public static final Consumer<GateContext> INVERSION = context -> {
        var storage = context.stateStorage;
        storage.setMode(storage.getMode() == 1 ? 0 : 1);
    };

    public static final SimpleGateHandler BASE = monoGate(
            ChyzyLogistics.id("base"),
            (context, aBoolean) -> (context.stateStorage.getMode() == 1) != aBoolean);

    public static final SimpleGateHandler AND = biGate(
            ChyzyLogistics.id("and"),
            (context, right, left) -> (context.stateStorage.getMode() == 1) != (right && left));

    public static final SimpleGateHandler OR = biGate(
            ChyzyLogistics.id("or"),
            (context, right, left) -> (context.stateStorage.getMode() == 1) != (right || left));

    public static final SimpleGateHandler XOR = biGate(
            ChyzyLogistics.id("xor"),
            (context, right, left) -> (context.stateStorage.getMode() == 1) != (right ^ left));

    public static final SimpleGateHandler TRIPLE_AND = triGate(
            ChyzyLogistics.id("tri_and"),
            (context, right, middle, left) -> (context.stateStorage.getMode() == 1) != (right && middle && left));

    public static final SimpleGateHandler TRIPLE_OR = triGate(
            ChyzyLogistics.id("tri_or"),
            (context, right, middle, left) -> (context.stateStorage.getMode() == 1) != (right || middle || left));

    public static final SimpleGateHandler AND_THEN_OR = triGate(
            ChyzyLogistics.id("and_then_or"),
            (context, right, middle, left) -> (context.stateStorage.getMode() == 1) != ((right && middle) || left));

    public static final SimpleGateHandler OR_THEN_AND = triGate(
            ChyzyLogistics.id("or_then_and"),
            (context, right, middle, left) -> (context.stateStorage.getMode() == 1) != (right || (middle && left)));

    private final MultiBooleanFunc func;

    private SimpleGateHandler(Identifier id, List<Side> inputs, List<Side> outputs, MultiBooleanFunc func) {
        this(id, INVERSION, inputs, outputs, func);
    }

    private SimpleGateHandler(Identifier id, Consumer<GateContext> interactEvent, List<Side> inputs, List<Side> outputs, MultiBooleanFunc func) {
        super(id, interactEvent, inputs, outputs);
        this.func = func;
    }

    @Override
    public Map<Side, Integer> calculateOutputData(GateContext context, Map<Side, Integer> inputData) {
        Boolean[] booleans = new Boolean[inputData.size()];

        for (int i = 0; i < inputs.size(); i++) {
            booleans[i] = inputData.get(inputs.get(i)) > 0;
        }

        boolean isPowered = func.apply(context, booleans);

        Map<Side, Integer> outputData = new HashMap<>();

        for (Side output : outputs) {
            outputData.put(output, isPowered ? 15 : 0);
        }

        return outputData;
    }

    public static SimpleGateHandler monoGate(Identifier id, BiFunction<GateContext, Boolean, Boolean> func){
        return new SimpleGateHandler(
                id,
                List.of(Side.BACK),
                List.of(Side.FRONT),
                (context, booleans) -> func.apply(context, booleans[0])
        );
    }

    public static SimpleGateHandler biGate(Identifier id, TriFunction<GateContext, Boolean, Boolean, Boolean> func){
        return new SimpleGateHandler(
                id,
                List.of(Side.LEFT, Side.RIGHT),
                List.of(Side.FRONT),
                (context, booleans) -> func.apply(context, booleans[0], booleans[1])
        );
    }

    public static SimpleGateHandler triGate(Identifier id, Function4<GateContext, Boolean, Boolean, Boolean, Boolean> func){
        return new SimpleGateHandler(
                id,
                List.of(Side.LEFT, Side.BACK, Side.RIGHT),
                List.of(Side.FRONT),
                (context, booleans) -> func.apply(context, booleans[0], booleans[1], booleans[2])
        );
    }

    public interface MultiBooleanFunc {
        Boolean apply(GateContext context, Boolean... booleans);
    }
}
