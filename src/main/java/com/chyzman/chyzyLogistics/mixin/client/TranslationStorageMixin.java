package com.chyzman.chyzyLogistics.mixin.client;

import com.chyzman.chyzyLogistics.client.utils.TranslationInjectionEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(TranslationStorage.class)
public abstract class TranslationStorageMixin {

    @Inject(method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/List;Z)Lnet/minecraft/client/resource/language/TranslationStorage;", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void dynamicLangStuff(ResourceManager resourceManager, List<String> definitionCodes, boolean rightToLeft, CallbackInfoReturnable<TranslationStorage> cir, Map<String, String> map) {
        List<LanguageDefinition> definitions = List.copyOf(MinecraftClient.getInstance().getLanguageManager().getAllLanguages().values());

        TranslationInjectionEvent.AFTER_LANGUAGE_LOAD.invoker().generateLanguageTranslations(new TranslationInjectionEvent.TranslationMapHelper(map, definitions));
    }

}