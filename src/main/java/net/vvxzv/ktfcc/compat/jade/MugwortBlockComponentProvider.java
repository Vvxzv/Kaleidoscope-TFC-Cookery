package net.vvxzv.ktfcc.compat.jade;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.block.MugwortBlock;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum MugwortBlockComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockState state = blockAccessor.getBlockState();
        if(state.getBlock() instanceof MugwortBlock) {
            if(state.getValue(TFCBlockStateProperties.STAGE_2) == 2) {
                iTooltip.add(Component.translatable("ktfcc.mugwort.can_cut"));
            }
            if(state.getValue(TFCBlockStateProperties.LIFECYCLE) == Lifecycle.FRUITING) {
                iTooltip.add(Component.translatable("ktfcc.mugwort.can_pick"));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "mugwort");
    }
}