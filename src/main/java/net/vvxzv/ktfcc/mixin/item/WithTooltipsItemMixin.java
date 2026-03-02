package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.WithTooltipsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

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
