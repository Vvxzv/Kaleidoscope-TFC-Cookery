package net.vvxzv.ktfcc.mixin.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.config.GeneralConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.event.effect.SatiatedShieldEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SatiatedShieldEvent.class)
public class SatiatedShieldEventMixin {

    /**
     * @author Vvxzv
     * @reason TFCFoodData compat
     */
    @SubscribeEvent
    @Overwrite(remap = false)
    public static void onPlayerHurt(LivingDamageEvent event) {
        int amount = Math.round(event.getAmount()) * 2;
        DamageSource source = event.getSource();
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player) {
            if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                if (!(Boolean)GeneralConfig.SATIATED_SHIELD_ABSORB_ENABLED.get()) {
                    return;
                }

                if(player.getFoodData() instanceof TFCFoodData foodData){
                    if (foodData.getFoodLevel() > 8 && player.hasEffect(ModEffects.SATIATED_SHIELD.get())) {
                        if (source.is(TagMod.SATIATED_SHIELD_WEAKNESS)) {
                            amount *= 2;
                        }

                        float exhaustionLevel = Math.max(0.0F, (float)amount / 4.0F);
                        float playerFoodLevel = (float) foodData.getFoodLevel();
                        foodData.addExhaustion(exhaustionLevel);
                        if (GeneralConfig.SATIATED_SHIELD_ABSORB_EXCESS_DAMAGE.get()) {
                            event.setCanceled(true);
                        } else {
                            float consumedFoodLevel = exhaustionLevel / 4.0F;
                            if (consumedFoodLevel >= playerFoodLevel) {
                                float extraDamage;
                                if (source.is(TagMod.SATIATED_SHIELD_WEAKNESS)) {
                                    extraDamage = (consumedFoodLevel - playerFoodLevel) * 2.0F;
                                } else {
                                    extraDamage = (consumedFoodLevel - playerFoodLevel) * 4.0F;
                                }

                                float remainingDamage = event.getAmount() - extraDamage;
                                event.setAmount(Math.max(0.0F, remainingDamage));
                            } else {
                                event.setAmount(0.0F);
                            }
                        }
                    }
                }
            }
        }
    }
}
