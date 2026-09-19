package net.vvxzv.ktfcc.mixin.block.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.BambooTrayBlockEntity;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BambooTrayBlockEntity.class)
public class BambooTrayBlockEntityMixin {

    @Inject(method = "onPutItem", at = @At("HEAD"), cancellable = true)
    private void onPutItem(Level level, LivingEntity user, ItemStack held, int slot, CallbackInfoReturnable<Boolean> cir) {
        IFood food = FoodCapability.get(held);
        if(food != null && food.isRotten()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
