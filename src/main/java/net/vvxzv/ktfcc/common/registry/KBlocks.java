package net.vvxzv.ktfcc.common.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteAnimateTicks;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.vvxzv.ktfcc.KTFCC;
import net.vvxzv.ktfcc.common.block.*;
import net.vvxzv.ktfcc.common.block.decay.DecayingFoodBiteBlock;
import net.vvxzv.ktfcc.common.block.decay.DecayingFoodBiteOneByTwoBlock;
import net.vvxzv.ktfcc.common.block.decay.DecayingFoodBiteThreeByThreeBlock;
import net.vvxzv.ktfcc.common.blockentity.DecayingFoodBlockEntity;
import net.vvxzv.ktfcc.common.blockentity.DecayingThreeFoodBlockEntity;

import java.util.function.Supplier;

public class KBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, KTFCC.MODID);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> blockObject = BLOCKS.register(name, block);
        KItems.ITEMS.register(name, () -> new BlockItem(blockObject.get(), new Item.Properties()));
        return blockObject;
    }

    private static <T extends Block> RegistryObject<T> registerBlockNoItem(String name, Supplier<T> block){
        return BLOCKS.register(name, block);
    }

    private static ExtendedProperties decayingFoodBlockProperties(){
        return ExtendedProperties.of(MapColor.COLOR_ORANGE).mapColor(MapColor.COLOR_GREEN).strength(1.0F).sound(SoundType.WOOD).blockEntity(KBlockEntity.DECAYING).serverTicks(DecayingFoodBlockEntity::serverTick).instrument(NoteBlockInstrument.DIDGERIDOO).pushReaction(PushReaction.DESTROY);
    }

    private static RegistryObject<Block> bowlRottenFood(String name){
        return BLOCKS.register(name, RottenBlock::new);
    }

    private static RegistryObject<Block> bigBowlRottenFood(String name){
        return BLOCKS.register(name, () -> new RottenBlock(Block.box(2, 0, 2, 14, 6, 14)));
    }

    public static final RegistryObject<Block> FONDANT_PIE = registerBlockNoItem(
            "fondant_pie",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_FONDANT_PIE,
                    KItems.FONDANT_PIE,
                    4
            )
    );

    public static final RegistryObject<Block> DONGPO_PORK = registerBlockNoItem(
            "dongpo_pork",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_DONGPO_PORK,
                    KItems.DONGPO_PORK
            )
    );

    public static final RegistryObject<Block> FONDANT_SPIDER_EYE = registerBlockNoItem(
            "fondant_spider_eye",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_FONDANT_SPIDER_EYE,
                    KItems.FONDANT_SPIDER_EYE,
                    4
            )
    );


    public static final RegistryObject<Block> CHORUS_FRIED_EGG = registerBlockNoItem(
            "chorus_fried_egg",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_CHORUS_FRIED_EGG,
                    KItems.CHORUS_FRIED_EGG
            )
    );


    public static final RegistryObject<Block> BRAISED_FISH = registerBlockNoItem(
            "braised_fish",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_BRAISED_FISH,
                    KItems.BRAISED_FISH,
                    4
            )
    );


    public static final RegistryObject<Block> GOLDEN_SALAD = registerBlockNoItem(
            "golden_salad",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_GOLDEN_SALAD,
                    KItems.GOLDEN_SALAD,
                    6
            )
    );


    public static final RegistryObject<Block> SPICY_CHICKEN = registerBlockNoItem(
            "spicy_chicken",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_SPICY_CHICKEN,
                    KItems.SPICY_CHICKEN,
                    4
            )
    );


    public static final RegistryObject<Block> YAKITORI = registerBlockNoItem(
            "yakitori",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_YAKITORI,
                    KItems.YAKITORI,
                    4
            )
    );


    public static final RegistryObject<Block> PAN_SEARED_KNIGHT_STEAK = registerBlockNoItem(
            "pan_seared_knight_steak",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_PAN_SEARED_KNIGHT_STEAK,
                    KItems.PAN_SEARED_KNIGHT_STEAK,
                    4
            )
    );


    public static final RegistryObject<Block> STARGAZY_PIE = registerBlockNoItem(
            "stargazy_pie",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_STARGAZY_PIE,
                    KItems.STARGAZY_PIE,
                    4
            )
    );


    public static final RegistryObject<Block> SWEET_AND_SOUR_ENDER_PEARLS = registerBlockNoItem(
            "sweet_and_sour_ender_pearls",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_SWEET_AND_SOUR_ENDER_PEARLS,
                    KItems.SWEET_AND_SOUR_ENDER_PEARLS,
                    4
            )
    );


    public static final RegistryObject<Block> CRYSTAL_LAMB_CHOP = registerBlockNoItem(
            "crystal_lamb_chop",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_CRYSTAL_LAMB_CHOP,
                    KItems.CRYSTAL_LAMB_CHOP
            )
    );


    public static final RegistryObject<Block> BLAZE_LAMB_CHOP = registerBlockNoItem(
            "blaze_lamb_chop",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_BLAZE_LAMB_CHOP,
                    KItems.BLAZE_LAMB_CHOP
            )
    );


    public static final RegistryObject<Block> FROST_LAMB_CHOP = registerBlockNoItem(
            "frost_lamb_chop",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_FROST_LAMB_CHOP,
                    KItems.FROST_LAMB_CHOP
            )
    );


    public static final RegistryObject<Block> NETHER_STYLE_SASHIMI = registerBlockNoItem(
            "nether_style_sashimi",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_NETHER_STYLE_SASHIMI,
                    KItems.NETHER_STYLE_SASHIMI,
                    4
            )
    );


    public static final RegistryObject<Block> END_STYLE_SASHIMI = registerBlockNoItem(
            "end_style_sashimi",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_END_STYLE_SASHIMI,
                    KItems.END_STYLE_SASHIMI,
                    4
            )
    );


    public static final RegistryObject<Block> DESERT_STYLE_SASHIMI = registerBlockNoItem(
            "desert_style_sashimi",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_DESERT_STYLE_SASHIMI,
                    KItems.DESERT_STYLE_SASHIMI,
                    4
            )
    );


    public static final RegistryObject<Block> TUNDRA_STYLE_SASHIMI = registerBlockNoItem(
            "tundra_style_sashimi",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_TUNDRA_STYLE_SASHIMI,
                    KItems.TUNDRA_STYLE_SASHIMI,
                    4
            )
    );


    public static final RegistryObject<Block> COLD_STYLE_SASHIMI = registerBlockNoItem(
            "cold_style_sashimi",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_COLD_STYLE_SASHIMI,
                    KItems.COLD_STYLE_SASHIMI,
                    4
            )
    );


    public static final RegistryObject<Block> SHENGJIAN_MANTOU = registerBlockNoItem(
            "shengjian_mantou",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_SHENGJIAN_MANTOU,
                    KItems.SHENGJIAN_MANTOU,
                    4
            )
    );

    public static final RegistryObject<Block> CANDIED_POTATO = registerBlockNoItem(
            "candied_potato",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_CANDIED_POTATO,
                    KItems.CANDIED_POTATO
            )
    );


    public static final RegistryObject<Block> DOUGH_DROP_SOUP = registerBlockNoItem(
            "dough_drop_soup",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_DOUGH_DROP_SOUP,
                    KItems.DOUGH_DROP_SOUP,
                    4
            ).setAABB(Block.box(2, 0, 2, 14, 6, 14))
    );


    public static final RegistryObject<Block> STUFFED_TIGER_SKIN_PEPPER = registerBlockNoItem(
            "stuffed_tiger_skin_pepper",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_STUFFED_TIGER_SKIN_PEPPER,
                    KItems.STUFFED_TIGER_SKIN_PEPPER,
                    5
            )
    );


    public static final RegistryObject<Block> SPICY_RABBIT_HEAD = registerBlockNoItem(
            "spicy_rabbit_head",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_SPICY_RABBIT_HEAD,
                    KItems.SPICY_RABBIT_HEAD
            )
    );


    public static final RegistryObject<Block> FOUR_JOY_MEATBALL_SOUP = registerBlockNoItem(
            "four_joy_meatball_soup",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_FOUR_JOY_MEATBALL_SOUP,
                    KItems.FOUR_JOY_MEATBALL_SOUP,
                    4
            ).setAABB(Block.box(2, 0, 2, 14, 6, 14))
    );


    public static final RegistryObject<Block> NUMBING_SPICY_CHICKEN = registerBlockNoItem(
            "numbing_spicy_chicken",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_NUMBING_SPICY_CHICKEN,
                    KItems.NUMBING_SPICY_CHICKEN
            ).setAABB(Block.box(2, 0, 2, 14, 6, 14))
    );


    public static final RegistryObject<Block> FRIED_CATERPILLAR = registerBlockNoItem(
            "fried_caterpillar",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_FRIED_CATERPILLAR,
                    KItems.FRIED_CATERPILLAR
            ).setAABB(Block.box(3, 0, 3, 13, 4, 13))
    );


    public static final RegistryObject<Block> FRIED_SPRING_ROLL = registerBlockNoItem(
            "fried_spring_roll",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_FRIED_SPRING_ROLL,
                    KItems.FRIED_SPRING_ROLL
            )
    );


    public static final RegistryObject<Block> SPICY_BLOOD_STEW = registerBlockNoItem(
            "spicy_blood_stew",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_SPICY_BLOOD_STEW,
                    KItems.SPICY_BLOOD_STEW
            ).setAABB(Block.box(2, 0, 2, 14, 6, 14))
    );


    public static final RegistryObject<Block> FRUIT_PLATTER = registerBlockNoItem(
            "fruit_platter",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_FRUIT_PLATTER,
                    KItems.FRUIT_PLATTER,
                    4
            )
    );

    public static final RegistryObject<Block> BRAISED_PORK_RIBS = registerBlockNoItem(
            "braised_pork_ribs",
            () -> new DecayingFoodBiteOneByTwoBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_BRAISED_PORK_RIBS,
                    KItems.BRAISED_PORK_RIBS,
                    4
            )
    );

    public static final RegistryObject<Block> COLD_ROASTED_MEAT = registerBlockNoItem(
            "cold_roasted_meat",
            () -> new DecayingFoodBiteOneByTwoBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_COLD_ROASTED_MEAT,
                    KItems.COLD_ROASTED_MEAT,
                    3
            )
    );


    public static final RegistryObject<Block> OIL_SPLASHED_FISH = registerBlockNoItem(
            "oil_splashed_fish",
            () -> new DecayingFoodBiteOneByTwoBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_OIL_SPLASHED_FISH,
                    KItems.OIL_SPLASHED_FISH,
                    5
            )
    );

    public static final RegistryObject<Block> COLD_CUT_HAM_SLICES = registerBlockNoItem(
            "cold_cut_ham_slices",
            () -> new DecayingFoodBiteThreeByThreeBlock(
                    ExtendedProperties.of(MapColor.COLOR_ORANGE).mapColor(MapColor.COLOR_GREEN).strength(1.0F).sound(SoundType.WOOD).blockEntity(KBlockEntity.THREE_DECAYING).serverTicks(DecayingThreeFoodBlockEntity::serverTick).instrument(NoteBlockInstrument.DIDGERIDOO).pushReaction(PushReaction.DESTROY),
                    KBlocks.COLD_CUT_HAM_SLICES,
                    KItems.COLD_CUT_HAM_SLICES
            )
    );

    public static final RegistryObject<Block> BROWN_MUSHROOM_POT_SOUP = registerBlockNoItem(
            "brown_mushroom_pot_soup",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_BROWN_MUSHROOM_POT_SOUP,
                    KItems.BROWN_MUSHROOM_POT_SOUP,
                    2,
                    FoodBiteAnimateTicks.POT_SOUP_ANIMATE_TICK
            ).setAABB(Shapes.or(Block.box(1, 0, 1, 15, 1, 15), Block.box(4, 1, 4, 12, 7, 12)))
    );


    public static final RegistryObject<Block> RED_MUSHROOM_POT_SOUP = registerBlockNoItem(
            "red_mushroom_pot_soup",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_RED_MUSHROOM_POT_SOUP,
                    KItems.RED_MUSHROOM_POT_SOUP,
                    2,
                    FoodBiteAnimateTicks.POT_SOUP_ANIMATE_TICK
            ).setAABB(Shapes.or(Block.box(1, 0, 1, 15, 1, 15), Block.box(4, 1, 4, 12, 7, 12)))
    );


    public static final RegistryObject<Block> WARPED_FUNGUS_POT_SOUP = registerBlockNoItem(
            "warped_fungus_pot_soup",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_WARPED_FUNGUS_POT_SOUP,
                    KItems.WARPED_FUNGUS_POT_SOUP,
                    2,
                    FoodBiteAnimateTicks.POT_SOUP_ANIMATE_TICK
            ).setAABB(Shapes.or(Block.box(1, 0, 1, 15, 1, 15), Block.box(4, 1, 4, 12, 7, 12)))
    );


    public static final RegistryObject<Block> CRIMSON_FUNGUS_POT_SOUP = registerBlockNoItem(
            "crimson_fungus_pot_soup",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_CRIMSON_FUNGUS_POT_SOUP,
                    KItems.CRIMSON_FUNGUS_POT_SOUP,
                    2,
                    FoodBiteAnimateTicks.POT_SOUP_ANIMATE_TICK
            ).setAABB(Shapes.or(Block.box(1, 0, 1, 15, 1, 15), Block.box(4, 1, 4, 12, 7, 12)))
    );


    public static final RegistryObject<Block> BUDDHA_JUMPS_OVER_THE_WALL = registerBlockNoItem(
            "buddha_jumps_over_the_wall",
            () -> new DecayingFoodBiteBlock(
                    decayingFoodBlockProperties(),
                    KBlocks.ROTTEN_BUDDHA_JUMPS_OVER_THE_WALL,
                    KItems.BUDDHA_JUMPS_OVER_THE_WALL,
                    2,
                    FoodBiteAnimateTicks.POT_SOUP_ANIMATE_TICK
            ).setAABB(Shapes.or(Block.box(1, 0, 1, 15, 1, 15), Block.box(4, 1, 4, 12, 7, 12)))
    );

    public static final RegistryObject<Block> ROTTEN_FONDANT_PIE = bowlRottenFood("rotten_fondant_pie");
    public static final RegistryObject<Block> ROTTEN_DONGPO_PORK = bowlRottenFood("rotten_dongpo_pork");
    public static final RegistryObject<Block> ROTTEN_FONDANT_SPIDER_EYE = bowlRottenFood("rotten_fondant_spider_eye");
    public static final RegistryObject<Block> ROTTEN_CHORUS_FRIED_EGG = bowlRottenFood("rotten_chorus_fried_egg");
    public static final RegistryObject<Block> ROTTEN_BRAISED_FISH = bowlRottenFood("rotten_braised_fish");
    public static final RegistryObject<Block> ROTTEN_GOLDEN_SALAD = bowlRottenFood("rotten_golden_salad");
    public static final RegistryObject<Block> ROTTEN_SPICY_CHICKEN = bowlRottenFood("rotten_spicy_chicken");
    public static final RegistryObject<Block> ROTTEN_YAKITORI = bowlRottenFood("rotten_yakitori");
    public static final RegistryObject<Block> ROTTEN_PAN_SEARED_KNIGHT_STEAK = bowlRottenFood("rotten_pan_seared_knight_steak");
    public static final RegistryObject<Block> ROTTEN_STARGAZY_PIE = bowlRottenFood("rotten_stargazy_pie");
    public static final RegistryObject<Block> ROTTEN_SWEET_AND_SOUR_ENDER_PEARLS = bowlRottenFood("rotten_sweet_and_sour_ender_pearls");
    public static final RegistryObject<Block> ROTTEN_CRYSTAL_LAMB_CHOP = bowlRottenFood("rotten_crystal_lamb_chop");
    public static final RegistryObject<Block> ROTTEN_BLAZE_LAMB_CHOP = bowlRottenFood("rotten_blaze_lamb_chop");
    public static final RegistryObject<Block> ROTTEN_FROST_LAMB_CHOP = bowlRottenFood("rotten_frost_lamb_chop");
    public static final RegistryObject<Block> ROTTEN_NETHER_STYLE_SASHIMI = bowlRottenFood("rotten_nether_style_sashimi");
    public static final RegistryObject<Block> ROTTEN_END_STYLE_SASHIMI = bowlRottenFood("rotten_end_style_sashimi");
    public static final RegistryObject<Block> ROTTEN_DESERT_STYLE_SASHIMI = bowlRottenFood("rotten_desert_style_sashimi");
    public static final RegistryObject<Block> ROTTEN_TUNDRA_STYLE_SASHIMI = bowlRottenFood("rotten_tundra_style_sashimi");
    public static final RegistryObject<Block> ROTTEN_COLD_STYLE_SASHIMI = bowlRottenFood("rotten_cold_style_sashimi");
    public static final RegistryObject<Block> ROTTEN_SHENGJIAN_MANTOU = bowlRottenFood("rotten_shengjian_mantou");
    public static final RegistryObject<Block> ROTTEN_CANDIED_POTATO = bowlRottenFood("rotten_candied_potato");
    public static final RegistryObject<Block> ROTTEN_DOUGH_DROP_SOUP = bigBowlRottenFood("rotten_dough_drop_soup");
    public static final RegistryObject<Block> ROTTEN_STUFFED_TIGER_SKIN_PEPPER = bowlRottenFood("rotten_stuffed_tiger_skin_pepper");
    public static final RegistryObject<Block> ROTTEN_SPICY_RABBIT_HEAD = bowlRottenFood("rotten_spicy_rabbit_head");
    public static final RegistryObject<Block> ROTTEN_FOUR_JOY_MEATBALL_SOUP = bigBowlRottenFood("rotten_four_joy_meatball_soup");
    public static final RegistryObject<Block> ROTTEN_NUMBING_SPICY_CHICKEN = bigBowlRottenFood("rotten_numbing_spicy_chicken");
    public static final RegistryObject<Block> ROTTEN_FRIED_CATERPILLAR = registerBlockNoItem("rotten_fried_caterpillar", () -> new RottenBlock(Block.box(3, 0, 3, 13, 4, 13)));
    public static final RegistryObject<Block> ROTTEN_FRIED_SPRING_ROLL = bowlRottenFood("rotten_fried_spring_roll");
    public static final RegistryObject<Block> ROTTEN_SPICY_BLOOD_STEW = bigBowlRottenFood("rotten_spicy_blood_stew");
    public static final RegistryObject<Block> ROTTEN_FRUIT_PLATTER = bowlRottenFood("rotten_fruit_platter");

    public static final RegistryObject<Block> ROTTEN_BRAISED_PORK_RIBS = registerBlockNoItem("rotten_braised_pork_ribs", RottenOneByTwoBlock::new);
    public static final RegistryObject<Block> ROTTEN_COLD_ROASTED_MEAT = registerBlockNoItem("rotten_cold_roasted_meat", RottenOneByTwoBlock::new);
    public static final RegistryObject<Block> ROTTEN_OIL_SPLASHED_FISH = registerBlockNoItem("rotten_oil_splashed_fish", RottenOneByTwoBlock::new);

    //public static final RegistryObject<Block> ROTTEN_COLD_CUT_HAM_SLICES = registerBlockNoItem("rotten_cold_cut_ham_slices", RottenThreeByThreeBlock::new);

    public static final RegistryObject<Block> ROTTEN_BROWN_MUSHROOM_POT_SOUP = registerBlockNoItem(
            "rotten_brown_mushroom_pot_soup",
            () -> new RottenBlock(
                    Shapes.or(
                            Block.box(1, 0, 1, 15, 1, 15),
                            Block.box(4, 1, 4, 12, 7, 12)
                    )
            )
    );
    public static final RegistryObject<Block> ROTTEN_RED_MUSHROOM_POT_SOUP = registerBlockNoItem(
            "rotten_red_mushroom_pot_soup",
            () -> new RottenBlock(
                    Shapes.or(
                            Block.box(1, 0, 1, 15, 1, 15),
                            Block.box(4, 1, 4, 12, 7, 12))
            )
    );
    public static final RegistryObject<Block> ROTTEN_WARPED_FUNGUS_POT_SOUP = registerBlockNoItem(
            "rotten_warped_fungus_pot_soup",
            () -> new RottenBlock(
                    Shapes.or(
                            Block.box(1, 0, 1, 15, 1, 15),
                            Block.box(4, 1, 4, 12, 7, 12)
                    )
            )
    );
    public static final RegistryObject<Block> ROTTEN_CRIMSON_FUNGUS_POT_SOUP = registerBlockNoItem(
            "rotten_crimson_fungus_pot_soup",
            () -> new RottenBlock(
                    Shapes.or(
                            Block.box(1, 0, 1, 15, 1, 15),
                            Block.box(4, 1, 4, 12, 7, 12)
                    )
            )
    );
    public static final RegistryObject<Block> ROTTEN_BUDDHA_JUMPS_OVER_THE_WALL = registerBlockNoItem(
            "rotten_buddha_jumps_over_the_wall",
            () -> new RottenBlock(
                    Shapes.or(
                            Block.box(1, 0, 1, 15, 1, 15),
                            Block.box(4, 1, 4, 12, 7, 12)
                    )
            )
    );

    public static final RegistryObject<Block> STOVE = registerBlockNoItem("stove", StoveBlock::new);

    public static final RegistryObject<Block> PAN = registerBlock("pan", PanBlock::new);

}
