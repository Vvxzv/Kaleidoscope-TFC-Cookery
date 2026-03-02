package net.vvxzv.ktfcc.common.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import com.github.ysbbbbbb.kaleidoscopecookery.item.LiftBlockItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.vvxzv.ktfcc.KTFCC;
import net.vvxzv.ktfcc.common.item.BowlFoodBlockItem;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class KItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, KTFCC.MODID);

    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item){
        return ITEMS.register(name , item);
    }

    private static RegistryObject<Item> registerItem(String name){
        return registerItem(name, () -> new Item(new Item.Properties()));
    }

    private static RegistryObject<Item> registerBowlFoodBlockItem(String name, Supplier<? extends Block> pBlock, FoodProperties properties){
        return registerItem(name, () -> new BowlFoodBlockItem(pBlock, properties));
    }


    public static final RegistryObject<Item> FONDANT_PIE = registerBowlFoodBlockItem(
            "fondant_pie",
            KBlocks.FONDANT_PIE,
            ModFoods.FONDANT_PIE_ITEM
    );

    public static final RegistryObject<Item> DONGPO_PORK = registerBowlFoodBlockItem(
            "dongpo_pork",
            KBlocks.DONGPO_PORK,
            ModFoods.DONGPO_PORK_ITEM
    );

    public static final RegistryObject<Item> FONDANT_SPIDER_EYE = registerBowlFoodBlockItem(
            "fondant_spider_eye",
            KBlocks.FONDANT_SPIDER_EYE,
            ModFoods.FONDANT_SPIDER_EYE_ITEM
    );


    public static final RegistryObject<Item> CHORUS_FRIED_EGG = registerBowlFoodBlockItem(
            "chorus_fried_egg",
            KBlocks.CHORUS_FRIED_EGG,
            ModFoods.CHORUS_FRIED_EGG_ITEM
    );


    public static final RegistryObject<Item> BRAISED_FISH = registerBowlFoodBlockItem(
            "braised_fish",
            KBlocks.BRAISED_FISH,
            ModFoods.BRAISED_FISH_ITEM
    );


    public static final RegistryObject<Item> GOLDEN_SALAD = registerBowlFoodBlockItem(
            "golden_salad",
            KBlocks.GOLDEN_SALAD,
            ModFoods.GOLDEN_SALAD_ITEM
    );


    public static final RegistryObject<Item> SPICY_CHICKEN = registerBowlFoodBlockItem(
            "spicy_chicken",
            KBlocks.SPICY_CHICKEN,
            ModFoods.SPICY_CHICKEN_ITEM
    );


    public static final RegistryObject<Item> YAKITORI = registerBowlFoodBlockItem(
            "yakitori",
            KBlocks.YAKITORI,
            ModFoods.YAKITORI_ITEM
    );


    public static final RegistryObject<Item> PAN_SEARED_KNIGHT_STEAK = registerBowlFoodBlockItem(
            "pan_seared_knight_steak",
            KBlocks.PAN_SEARED_KNIGHT_STEAK,
            ModFoods.PAN_SEARED_KNIGHT_STEAK_ITEM
    );


    public static final RegistryObject<Item> STARGAZY_PIE = registerBowlFoodBlockItem(
            "stargazy_pie",
            KBlocks.STARGAZY_PIE,
            ModFoods.STARGAZY_PIE_ITEM
    );


    public static final RegistryObject<Item> SWEET_AND_SOUR_ENDER_PEARLS = registerBowlFoodBlockItem(
            "sweet_and_sour_ender_pearls",
            KBlocks.SWEET_AND_SOUR_ENDER_PEARLS,
            ModFoods.SWEET_AND_SOUR_ENDER_PEARLS_ITEM
    );


    public static final RegistryObject<Item> CRYSTAL_LAMB_CHOP = registerBowlFoodBlockItem(
            "crystal_lamb_chop",
            KBlocks.CRYSTAL_LAMB_CHOP,
            ModFoods.CRYSTAL_LAMB_CHOP_ITEM
    );


    public static final RegistryObject<Item> BLAZE_LAMB_CHOP = registerBowlFoodBlockItem(
            "blaze_lamb_chop",
            KBlocks.BLAZE_LAMB_CHOP,
            ModFoods.BLAZE_LAMB_CHOP_ITEM
    );


    public static final RegistryObject<Item> FROST_LAMB_CHOP = registerBowlFoodBlockItem(
            "frost_lamb_chop",
            KBlocks.FROST_LAMB_CHOP,
            ModFoods.FROST_LAMB_CHOP_ITEM
    );


    public static final RegistryObject<Item> NETHER_STYLE_SASHIMI = registerBowlFoodBlockItem(
            "nether_style_sashimi",
            KBlocks.NETHER_STYLE_SASHIMI,
            ModFoods.NETHER_STYLE_SASHIMI_ITEM
    );


    public static final RegistryObject<Item> END_STYLE_SASHIMI = registerBowlFoodBlockItem(
            "end_style_sashimi",
            KBlocks.END_STYLE_SASHIMI,
            ModFoods.END_STYLE_SASHIMI_ITEM
    );


    public static final RegistryObject<Item> DESERT_STYLE_SASHIMI = registerBowlFoodBlockItem(
            "desert_style_sashimi",
            KBlocks.DESERT_STYLE_SASHIMI,
            ModFoods.DESERT_STYLE_SASHIMI_ITEM
    );


    public static final RegistryObject<Item> TUNDRA_STYLE_SASHIMI = registerBowlFoodBlockItem(
            "tundra_style_sashimi",
            KBlocks.TUNDRA_STYLE_SASHIMI,
            ModFoods.TUNDRA_STYLE_SASHIMI_ITEM
    );


    public static final RegistryObject<Item> COLD_STYLE_SASHIMI = registerBowlFoodBlockItem(
            "cold_style_sashimi",
            KBlocks.COLD_STYLE_SASHIMI,
            ModFoods.COLD_STYLE_SASHIMI_ITEM
    );


    public static final RegistryObject<Item> SHENGJIAN_MANTOU = registerBowlFoodBlockItem(
            "shengjian_mantou",
            KBlocks.SHENGJIAN_MANTOU,
            ModFoods.SHENGJIAN_MANTOU_ITEM
    );

    public static final RegistryObject<Item> CANDIED_POTATO = registerBowlFoodBlockItem(
            "candied_potato",
            KBlocks.CANDIED_POTATO,
            ModFoods.CANDIED_POTATO_ITEM
    );


    public static final RegistryObject<Item> DOUGH_DROP_SOUP = registerBowlFoodBlockItem(
            "dough_drop_soup",
            KBlocks.DOUGH_DROP_SOUP,
            ModFoods.DOUGH_DROP_SOUP_ITEM
    );


    public static final RegistryObject<Item> STUFFED_TIGER_SKIN_PEPPER = registerBowlFoodBlockItem(
            "stuffed_tiger_skin_pepper",
            KBlocks.STUFFED_TIGER_SKIN_PEPPER,
            ModFoods.STUFFED_TIGER_SKIN_PEPPER_ITEM
    );


    public static final RegistryObject<Item> SPICY_RABBIT_HEAD = registerBowlFoodBlockItem(
            "spicy_rabbit_head",
            KBlocks.SPICY_RABBIT_HEAD,
            ModFoods.SPICY_RABBIT_HEAD_ITEM
    );


    public static final RegistryObject<Item> FOUR_JOY_MEATBALL_SOUP = registerBowlFoodBlockItem(
            "four_joy_meatball_soup",
            KBlocks.FOUR_JOY_MEATBALL_SOUP,
            ModFoods.FOUR_JOY_MEATBALL_SOUP_ITEM
    );


    public static final RegistryObject<Item> NUMBING_SPICY_CHICKEN = registerBowlFoodBlockItem(
            "numbing_spicy_chicken",
            KBlocks.NUMBING_SPICY_CHICKEN,
            ModFoods.NUMBING_SPICY_CHICKEN_ITEM
    );


    public static final RegistryObject<Item> FRIED_CATERPILLAR = registerBowlFoodBlockItem(
            "fried_caterpillar",
            KBlocks.FRIED_CATERPILLAR,
            ModFoods.FRIED_CATERPILLAR_ITEM
    );


    public static final RegistryObject<Item> FRIED_SPRING_ROLL = registerBowlFoodBlockItem(
            "fried_spring_roll",
            KBlocks.FRIED_SPRING_ROLL,
            ModFoods.FRIED_SPRING_ROLL_ITEM
    );


    public static final RegistryObject<Item> SPICY_BLOOD_STEW = registerBowlFoodBlockItem(
            "spicy_blood_stew",
            KBlocks.SPICY_BLOOD_STEW,
            ModFoods.SPICY_BLOOD_STEW_ITEM
    );


    public static final RegistryObject<Item> FRUIT_PLATTER = registerBowlFoodBlockItem(
            "fruit_platter",
            KBlocks.FRUIT_PLATTER,
            ModFoods.FRUIT_PLATTER_ITEM
    );

    public static final RegistryObject<Item> BRAISED_PORK_RIBS = registerBowlFoodBlockItem(
            "braised_pork_ribs",
            KBlocks.BRAISED_PORK_RIBS,
            ModFoods.BRAISED_PORK_RIBS_ITEM
    );

    public static final RegistryObject<Item> COLD_ROASTED_MEAT = registerBowlFoodBlockItem(
            "cold_roasted_meat",
            KBlocks.COLD_ROASTED_MEAT,
            ModFoods.COLD_ROASTED_MEAT_ITEM
    );


    public static final RegistryObject<Item> OIL_SPLASHED_FISH = registerBowlFoodBlockItem(
            "oil_splashed_fish",
            KBlocks.OIL_SPLASHED_FISH,
            ModFoods.OIL_SPLASHED_FISH_ITEM
    );

    public static final RegistryObject<Item> COLD_CUT_HAM_SLICES = registerItem(
            "cold_cut_ham_slices",
            () -> new LiftBlockItem(KBlocks.COLD_CUT_HAM_SLICES.get(), "cold_cut_ham_slices")
    );


    public static final RegistryObject<Item> BROWN_MUSHROOM_POT_SOUP = registerBowlFoodBlockItem(
            "brown_mushroom_pot_soup",
            KBlocks.BROWN_MUSHROOM_POT_SOUP,
            ModFoods.BROWN_MUSHROOM_POT_SOUP_ITEM
    );


    public static final RegistryObject<Item> RED_MUSHROOM_POT_SOUP = registerBowlFoodBlockItem(
            "red_mushroom_pot_soup",
            KBlocks.RED_MUSHROOM_POT_SOUP,
            ModFoods.RED_MUSHROOM_POT_SOUP_ITEM
    );


    public static final RegistryObject<Item> WARPED_FUNGUS_POT_SOUP = registerBowlFoodBlockItem(
            "warped_fungus_pot_soup",
            KBlocks.WARPED_FUNGUS_POT_SOUP,
            ModFoods.WARPED_FUNGUS_POT_SOUP_ITEM
    );


    public static final RegistryObject<Item> CRIMSON_FUNGUS_POT_SOUP = registerBowlFoodBlockItem(
            "crimson_fungus_pot_soup",
            KBlocks.CRIMSON_FUNGUS_POT_SOUP,
            ModFoods.CRIMSON_FUNGUS_POT_SOUP_ITEM
    );


    public static final RegistryObject<Item> BUDDHA_JUMPS_OVER_THE_WALL = registerBowlFoodBlockItem(
            "buddha_jumps_over_the_wall",
            KBlocks.BUDDHA_JUMPS_OVER_THE_WALL,
            ModFoods.BUDDHA_JUMPS_OVER_THE_WALL_ITEM
    );

    public static final RegistryObject<Item> UNFIRED_ENAMEL_BASIN = registerItem("unfired_enamel_basin");

    public static final RegistryObject<Item> KITCHEN_SHOVEL_HEAD = registerItem("kitchen_shovel_head");

    public static final RegistryObject<Item> IRON_KITCHEN_KNIFE_HEAD = registerItem("iron_kitchen_knife_head");
}
