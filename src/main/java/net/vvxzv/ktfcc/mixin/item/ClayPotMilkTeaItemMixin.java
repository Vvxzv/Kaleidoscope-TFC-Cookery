package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.ClayPotMilkTeaItem;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClayPotMilkTeaItem.class)
public class ClayPotMilkTeaItemMixin {

    @Inject(method = "getContainerItem", at = @At("RETURN"), cancellable = true)
    public void getContainerItem(CallbackInfoReturnable<Item> cir) {
        cir.setReturnValue(TFCItems.VESSEL.get());
    }
}
