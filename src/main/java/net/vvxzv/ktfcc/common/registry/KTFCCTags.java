package net.vvxzv.ktfcc.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.vvxzv.ktfcc.KTFCC;

@SuppressWarnings("removal")
public class KTFCCTags {
    public static final TagKey<Block> HEAT_SOURCE = TagKey.create(Registries.BLOCK, new ResourceLocation(KTFCC.MODID, "heat_source"));
}
