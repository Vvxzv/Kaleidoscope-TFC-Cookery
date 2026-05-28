package net.vvxzv.ktfcc.common.utils;

import net.minecraft.world.item.ItemStack;

public interface Decaying {

    boolean isRotten();

    ItemStack getStack();

    void setStack(ItemStack stack);
}
