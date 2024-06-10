package com.chyzman.electromechanics.mixin.client;

import com.chyzman.electromechanics.util.ModelStaticDefinitionAddition;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Mutable
    @Final
    @Shadow
    private static Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS;

    @Unique
    private boolean hasRegisterDefs = false;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V"))
    private void addToStaticDef(CallbackInfo ci) {
        if(hasRegisterDefs) return;

        var mutableMap = new LinkedHashMap<>(STATIC_DEFINITIONS);

        ModelStaticDefinitionAddition.EVENT.invoker().register((id, manager) -> {
            if(mutableMap.containsKey(id)) return false;

            mutableMap.put(id, manager);

            return true;
        });

        STATIC_DEFINITIONS = mutableMap;

        this.hasRegisterDefs = true;
    }
}
