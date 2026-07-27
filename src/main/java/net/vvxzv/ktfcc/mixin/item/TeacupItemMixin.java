package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.TeacupItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.vvxzv.ktfcc.Config;
import net.vvxzv.ktfcc.common.data.TeaEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(TeacupItem.class)
public class TeacupItemMixin {

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void addFoodData(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        entity.eat(level, new ItemStack(stack.getItem()));
    }

    @Redirect(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lcom/github/ysbbbbbb/kaleidoscopecookery/item/TeacupItem;addTeaEffect(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void addEffect(TeacupItem instance, Level level, LivingEntity entity) {
        TeaEffect tea = TeaEffect.get(new ItemStack(instance));
        if(tea != null) {
            List<MobEffectInstance> effects = tea.getEffects();
            for (MobEffectInstance effect: effects) {
                entity.addEffect(effect);
            }
        }
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

    @Redirect(method = "appendHoverText", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    private boolean removeEffectText(List<Object> instance) {
        return true;
    }
}
