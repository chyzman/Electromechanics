package com.chyzman.electromechanics.mixin;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Set;

import static com.chyzman.electromechanics.Electromechanics.bypassingAir;

@Mixin(ItemGroup.EntriesImpl.class)
public abstract class ItemGroupMixin {

    @Shadow
    @Final
    public Collection<ItemStack> parentTabStacks;

    @Shadow
    @Final
    public Set<ItemStack> searchTabStacks;

    @Inject(method = "add", at = @At(value = "HEAD"), cancellable = true)
    private void giveMyRedstoneSomeSpace(ItemStack stack, ItemGroup.StackVisibility visibility, CallbackInfo ci) {
        if (bypassingAir) {
            switch (visibility) {
                case PARENT_AND_SEARCH_TABS:
                    this.parentTabStacks.add(stack);
                    this.searchTabStacks.add(stack);
                    break;
                case PARENT_TAB_ONLY:
                    this.parentTabStacks.add(stack);
                    break;
                case SEARCH_TAB_ONLY:
                    this.searchTabStacks.add(stack);
            }
            ci.cancel();
        }
    }
}