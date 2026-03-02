package net.vvxzv.ktfcc.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.dries007.tfc.common.items.FluidContainerItem;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ItemUtils.class)
public class ItemUtilsMixin {

    /**
     * @author Vvxzv
     * @reason 可以返回 tfc 的容器
     */
    @Overwrite(remap = false)
    public static Item getContainerItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return Items.AIR;
        } else {
            Item item = stack.getItem();
            ItemStack remainingItem = item.getCraftingRemainingItem(stack);
            if (remainingItem != null && !remainingItem.isEmpty()) {
                return remainingItem.getItem();
            } else if (item instanceof IHasContainer hasContainer) {
                return hasContainer.getContainerItem();
            } else if (item instanceof BowlFoodItem) {
                return Items.BOWL;
            } else if (stack.is(TagMod.BOWL_CONTAINER)) {
                return Items.BOWL;
            } else if (stack.is(TagMod.GLASS_BOTTLE_CONTAINER)) {
                return Items.GLASS_BOTTLE;
            } else if (stack.is(TagMod.BUCKET_CONTAINER)) {
                return Items.BUCKET;
            } else if (item instanceof FluidContainerItem) {
                return item;
            } else {
                return stack.is(Items.POTION) ? Items.GLASS_BOTTLE : Items.AIR;
            }
        }
    }
}
