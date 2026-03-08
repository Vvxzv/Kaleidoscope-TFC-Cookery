package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.WithTooltipsItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.vvxzv.ktfcc.Config;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Objects;

@Mixin(WithTooltipsItem.class)
public class WithTooltipsItemMixin {
    @ModifyArg(
            method = "appendHoverText",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
            )
    )
    public String tooltipReplace(String pKey) {
        if(Objects.equals(pKey, "tooltip.kaleidoscope_cookery.caterpillar")){
            return "tooltip.ktfcc.caterpillar";
        }
        return pKey;
    }
}
