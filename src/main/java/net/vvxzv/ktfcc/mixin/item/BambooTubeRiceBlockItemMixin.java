package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.BambooTubeRiceBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BambooTubeRiceBlockItem.class)
public class BambooTubeRiceBlockItemMixin {

    @Inject(method = "getContainerItem", at = @At("RETURN"), cancellable = true)
    public void getContainerItem(CallbackInfoReturnable<Item> cir) {
        cir.setReturnValue(Items.AIR);
    }
}
