package net.vvxzv.ktfcc.mixin.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.StrawHatItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(StrawHatItem.class)
public class StrawHatItemMixin {

    /**
     * @author Vvxzv
     * @reason 改变功能
     */
    @Overwrite
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.ktfcc.straw_hat").withStyle(ChatFormatting.GRAY));
    }
}
