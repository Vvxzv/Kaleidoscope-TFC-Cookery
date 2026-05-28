package net.vvxzv.ktfcc.compat.jade;

import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltips;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.block.entity.StoveBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;

import java.util.ArrayList;
import java.util.List;

public enum StoveComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockEntity blockEntity = blockAccessor.getBlockEntity();
        if(blockEntity instanceof StoveBlockEntity stove) {
            float currentTemp = stove.getTemperature();
            Heat heatLevel = Heat.getHeat(currentTemp);
            if (heatLevel != null) {
                BlockEntityTooltips.heat(iTooltip::add, currentTemp);
            }

            String timeText = stove.getTimeText();
            if(timeText != null) {
                iTooltip.add(Component.literal(timeText));
            }

            ItemStack[] fuels = stove.getFuels();
            List<IElement> list = new ArrayList<>();
            for (ItemStack fuel: fuels) {
                if(!fuel.isEmpty()) {
                    list.add(iTooltip.getElementHelper().item(fuel));
                }
            }
            iTooltip.add(list);
        }

    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "stove");
    }
}
