package net.vvxzv.ktfcc.mixin.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import net.dries007.tfc.common.component.size.IItemSize;
import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.component.size.Size;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FruitBasketBlockEntity.class)
public class FruitBasketBlockEntityMixin {
    @Inject(
            method = "putOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void putOn(ItemStack stack, CallbackInfo ci) {
        IItemSize size = ItemSizeManager.get(stack);
        if(!size.getSize(stack).isSmallerThan(Size.NORMAL)){
            ci.cancel();
        }
    }
}
