package net.vvxzv.ktfcc.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemUtils.class)
public class ItemUtilsMixin {

    @Inject(method = "getContainerItem", at = @At("HEAD"), cancellable = true)
    private static void getContainerItem(ItemStack stack, CallbackInfoReturnable<Item> cir) {
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof FluidContainerItem) {
                cir.setReturnValue(item);
            }
        }
    }
}
