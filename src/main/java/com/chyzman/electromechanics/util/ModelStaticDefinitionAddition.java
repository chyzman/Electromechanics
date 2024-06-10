package com.chyzman.electromechanics.util;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;

public interface ModelStaticDefinitionAddition {

    Event<ModelStaticDefinitionAddition> EVENT = EventFactory.createArrayBacked(ModelStaticDefinitionAddition.class, invokers -> register -> {
        for (var invoker : invokers) invoker.register(register);
    });

    void register(StateMangerRegister register);

    @FunctionalInterface
    interface StateMangerRegister {
        /**
         * @return If such was added to the static def map or not
         */
        boolean addManager(Identifier id, StateManager<Block, BlockState> manager);
    }
}
