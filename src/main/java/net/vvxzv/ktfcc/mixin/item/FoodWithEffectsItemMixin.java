package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.CompatRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.item.FoodWithEffectsItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.vvxzv.ktfcc.Config;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(FoodWithEffectsItem.class)
public class FoodWithEffectsItemMixin {
    @Final
    @Shadow(remap = false)
    private List<MobEffectInstance> effectInstances;

    @Unique
    private static boolean hasTooltip(){
        return Config.foodTooltips;
    }
    /**
     * @author Vvxzv
     * @reason Turn on or turn off food tooltips
     */
    @Overwrite
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id != null) {
            String key = "tooltip.%s.%s.maxim".formatted(KaleidoscopeCookery.MOD_ID, id.getPath());
            MutableComponent full = Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
            String text = full.getString();

            if(hasTooltip()){
                for(String line : text.split("\n")) {
                    if (!line.isEmpty()) {
                        tooltip.add(Component.literal(line).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                    } else {
                        tooltip.add(CommonComponents.EMPTY);
                    }
                }
            }
        }

        if (!this.effectInstances.isEmpty() && CompatRegistry.SHOW_POTION_EFFECT_TOOLTIPS) {
            tooltip.add(CommonComponents.space());
            PotionUtils.addPotionTooltip(this.effectInstances, tooltip, 1.0F);
        }
    }
}
