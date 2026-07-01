package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.FoodWithEffectsItem;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.vvxzv.ktfcc.Config;
import net.vvxzv.ktfcc.common.data.FoodEffect;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FoodWithEffectsItem.class)
public class FoodWithEffectsItemMixin extends Item {

    @Final
    @Shadow(remap = false)
    private List<MobEffectInstance> effectInstances;

    public FoodWithEffectsItemMixin(Properties pProperties) {
        super(pProperties);
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

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        IFood food = FoodCapability.get(stack);
        if(food != null) {
            Utils.applyFoodEffect(stack, entity);
            if(entity instanceof Player player && player.getFoodData() instanceof TFCFoodData foodData) {
                foodData.eat(food);
            }
            stack.shrink(1);
            return stack;
        }

        return super.finishUsingItem(stack, level, entity);
    }
}
