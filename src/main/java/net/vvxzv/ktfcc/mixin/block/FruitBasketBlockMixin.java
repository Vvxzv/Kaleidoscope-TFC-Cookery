package net.vvxzv.ktfcc.mixin.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.FruitBasketBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FruitBasketBlock.class)
public class FruitBasketBlockMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        tooltip.add(Component.translatable("ktfcc.tooltip.pick_fruit").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
