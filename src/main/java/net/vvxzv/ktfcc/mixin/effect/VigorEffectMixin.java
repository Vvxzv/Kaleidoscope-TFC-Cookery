package net.vvxzv.ktfcc.mixin.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.effect.VigorEffect;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VigorEffect.class)
public class VigorEffectMixin {
    @Inject(method = "applyEffectTick", at = @At("HEAD"))
    private void applyEffectTick(LivingEntity livingEntity, int amplifier, CallbackInfo ci) {
        if (livingEntity instanceof Player player) {
            if (player.isSprinting()) {
                if(player.getFoodData() instanceof TFCFoodData foodData){
                    foodData.setExhaustion(0);
                }
            }
        }
    }
}
