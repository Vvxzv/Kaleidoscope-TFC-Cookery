package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.vvxzv.ktfcc.Config;
import net.vvxzv.ktfcc.common.data.FoodEffect;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BowlFoodBlockItem.class)
public class BowlFoodBlockItemMixin {

    @Final
    @Shadow(remap = false)
    private List<MobEffectInstance> effectInstances;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;food(Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/Item$Properties;"))
    private static Item.Properties food(Item.Properties instance, FoodProperties pFood) {
        if(pFood == ModFoods.DARK_CUISINE_ITEM || pFood == ModFoods.SUSPICIOUS_STIR_FRY_ITEM) {
            return instance.food(pFood);
        }
        FoodProperties foodProperties = Utils.foodPropertiesRemoveEffect(pFood);
        return instance.food(foodProperties);
    }

    @Unique
    private static boolean hasTooltip(){
        return Config.foodTooltips;
    }

    @Redirect(method = "appendHoverText", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    private boolean text(List<Object> instance, Object e) {
        if(hasTooltip()) {
            return instance.add(e);
        }
        return false;
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void effectTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        FoodEffect foodEffect = FoodEffect.get(stack);
        if(foodEffect != null) {
            this.effectInstances.clear();
            List<MobEffectInstance> effects = foodEffect.getEffects();
            if(!effects.isEmpty()) {
                effectInstances.addAll(effects);
            }
        }
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void finishUsingItem(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        Utils.applyFoodEffect(stack, entity);
    }
}
