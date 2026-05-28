package net.vvxzv.ktfcc.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;

public class Tags {
    public static final TagKey<Block> HEAT_SOURCE = BlockTags.create(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "heat_source"));
}
