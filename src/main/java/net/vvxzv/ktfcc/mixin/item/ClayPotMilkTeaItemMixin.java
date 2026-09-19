package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.ClayPotMilkTeaItem;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClayPotMilkTeaItem.class)
public class ClayPotMilkTeaItemMixin {

    @Inject(method = "getContainerItem", at = @At("RETURN"), cancellable = true, remap = false)
    private void getContainerItem(CallbackInfoReturnable<Item> cir) {
        cir.setReturnValue(TFCItems.VESSEL.get());
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void finishUsingItem(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if(entity instanceof Player player) {
            IFood food = FoodCapability.get(stack);
            if(food != null) {
                if(player.getFoodData() instanceof TFCFoodData foodData) {
                    foodData.eat(food);
                }
            }
        }
    }
}
