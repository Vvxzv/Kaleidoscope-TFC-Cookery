package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodBlockItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.vvxzv.ktfcc.Config;
import net.vvxzv.ktfcc.common.data.FoodEffect;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BowlFoodBlockItem.class)
public class BowlFoodBlockItemMixin{

    @Final
    @Shadow
    private List<MobEffectInstance> effectInstances;

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
    private void effectTooltip(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        FoodEffect foodEffect = FoodEffect.get(stack);
        if(foodEffect != null) {
            this.effectInstances.clear();
            List<MobEffectInstance> effects = foodEffect.getEffects();
            if(!effects.isEmpty()) {
                effectInstances.addAll(effects);
            }
        }
    }

    @Redirect(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack finishUsingItem(BlockItem instance, ItemStack stack, Level level, LivingEntity entity) {
        FoodEffect foodEffect = FoodEffect.get(stack);
        if(foodEffect != null) {
            FoodProperties foodproperties = stack.getFoodProperties(entity);
            if(foodproperties != null) {
                FoodProperties foodProperties = Utils.foodPropertiesRemoveEffect(foodproperties);
                Utils.applyFoodEffect(foodEffect, entity);
                return entity.eat(level, stack, foodProperties);
            }
        }
        return instance.finishUsingItem(stack, level, entity);
    }
}
