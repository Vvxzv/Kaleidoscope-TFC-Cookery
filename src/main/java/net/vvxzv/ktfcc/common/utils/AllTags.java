package net.vvxzv.ktfcc.common.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;

public class AllTags {
    public static class Items {
        public static final TagKey<Item> POT_INGREDIENT = ItemTags.create(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "pot_ingredient"));
        public static final TagKey<Item> STOCKPOT_INGREDIENT = ItemTags.create(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "stockpot_ingredient"));
    }

    public static class Blocks {
        public static final TagKey<Block> HEAT_SOURCE = BlockTags.create(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "heat_source"));
        public static final TagKey<Block> PLANT_DROPS_EXTRA_STRAW = BlockTags.create(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "plant_drops_extra_straw"));
    }
}
