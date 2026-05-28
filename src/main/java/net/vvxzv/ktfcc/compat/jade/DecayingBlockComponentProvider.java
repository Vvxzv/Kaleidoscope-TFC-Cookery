package net.vvxzv.ktfcc.compat.jade;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.utils.Decaying;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.ArrayList;
import java.util.List;

public enum DecayingBlockComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockEntity blockEntity = blockAccessor.getBlockEntity();
        if (blockEntity instanceof Decaying decay) {
            ItemStack stack = decay.getStack();
            if (!stack.isEmpty()) {
                iTooltip.add(stack.getHoverName());
                List<Component> lines = new ArrayList<>();
                FoodCapability.addTooltipInfo(stack, lines);
                lines.forEach(iTooltip::add);
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "decaying");
    }
}