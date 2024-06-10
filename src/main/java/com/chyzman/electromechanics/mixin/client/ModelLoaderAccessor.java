package com.chyzman.electromechanics.mixin.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.IOException;
import java.util.Map;

@Mixin(ModelLoader.class)
public interface ModelLoaderAccessor {
    @Invoker("loadModelFromJson") JsonUnbakedModel gelatin$LoadModelFromJson(Identifier id) throws IOException;
}
