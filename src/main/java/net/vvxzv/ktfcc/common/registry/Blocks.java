package net.vvxzv.ktfcc.common.registry;

import net.dries007.tfc.util.Helpers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.block.*;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Blocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(KaleidoscopeTFCCookery.MODID);

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
        return registerBlockWithItem(name, block, new Item.Properties());
    }

    private static <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Supplier<T> block, Item.Properties properties){
        DeferredBlock<T> blockObject = BLOCKS.register(name, block);
        Items.ITEMS.register(name, () -> new BlockItem(blockObject.get(), properties));
        return blockObject;
    }

    public static final DeferredBlock<Block> PAN = registerBlock("pan", PanBlock::new);

    public static final Map<Tea, DeferredBlock<Block>> WILD_TEA_TREES = Helpers.mapOf(
            Tea.class,
            Tea::isHasTree,
            (tea) -> registerBlock(
                    "wild_tea_tree/" + tea.name().toLowerCase(Locale.ROOT),
                    WildTeaTreeBlock::new
            )
    );

    public static final DeferredBlock<Block> WILD_MUGWORT = registerBlock("wild_mugwort",  WildMugwortBlock::new);

    public static final Map<Tea, DeferredBlock<TeaTreeBlock>> TEA_TREES = Helpers.mapOf(
            Tea.class,
            Tea::isHasTree,
            (tea) -> registerBlock(
                    "tea_tree/" + tea.name().toLowerCase(Locale.ROOT),
                    () -> new TeaTreeBlock(
                            Items.TEA_TREE_LEAVES.get(tea),
                            ResourceLocation.fromNamespaceAndPath(
                                    KaleidoscopeTFCCookery.MODID,
                                    "tea_tree/" + tea.name().toLowerCase(Locale.ROOT)
                            )
                    )
            )
    );

    public static final DeferredBlock<Block> MUGWORT = registerBlock(
            "mugwort",
            () -> new MugwortBlock(
                    Items.MUGWORT_LEAVES,
                    ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "mugwort")
            )
    );

    public static final DeferredBlock<Block> OIL_POT = registerBlock("oil_pot", OilPotBlock::new);
}
