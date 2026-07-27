package net.vvxzv.ktfcc.common.utils;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
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
        public static final TagKey<Item> DISHES = ItemTags.create(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "dishes"));
        public static final TagKey<Item> SOUPS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "soups"));
        public static final TagKey<Item> STRAW_HAT = ItemTags.create(ResourceLocation.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "straw_hat"));
    }

    public static class Blocks {
        public static final TagKey<Block> HEAT_SOURCE = BlockTags.create(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "heat_source"));
        public static final TagKey<Block> PLANT_DROPS_EXTRA_STRAW = BlockTags.create(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "plant_drops_extra_straw"));
    }
}
