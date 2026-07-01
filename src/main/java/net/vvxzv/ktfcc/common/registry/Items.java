package net.vvxzv.ktfcc.common.registry;

import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.util.Helpers;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.block.Tea;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Items {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KaleidoscopeTFCCookery.MODID);

    private static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item){
        return ITEMS.register(name, item);
    }

    private static DeferredItem<Item> registerItem(String name, Item.Properties properties){
        return registerItem(name, () -> new Item(properties));
    }

    private static DeferredItem<Item> registerItem(String name){
        return registerItem(name, new Item.Properties());
    }

    public static final DeferredItem<Item> UNFIRED_ENAMEL_BASIN = registerItem("unfired_enamel_basin");

    public static final DeferredItem<Item> UNFIRED_CUP = registerItem("unfired_cup");

    public static final DeferredItem<Item> KITCHEN_SHOVEL_HEAD = registerItem("kitchen_shovel_head");

    public static final DeferredItem<Item> IRON_KITCHEN_KNIFE_HEAD = registerItem("iron_kitchen_knife_head");

    public static final Map<Tea, DeferredItem<Item>> TEA_TREE_LEAVES = Helpers.mapOf(
            Tea.class,
            Tea::isHasTree,
            (tea) -> registerItem("tea_leaves/" + tea.name().toLowerCase(Locale.ROOT))
    );

    public static final Map<Tea, DeferredItem<Item>> TEA_LEAVES = Helpers.mapOf(
            Tea.class,
            (tea) -> registerItem("tea/" + tea.name().toLowerCase(Locale.ROOT))
    );

    public static final DeferredItem<Item> MUGWORT_LEAVES = registerItem("mugwort_leaves");

    public static final DeferredItem<Item> RAW_QINGTUAN = registerItem("raw_qingtuan");

    public static final DeferredItem<Item> RAW_DUMPLING = registerItem("raw_dumpling");

    public static final DeferredItem<Item> ZONGZI_LEAVES = registerItem("zongzi_leaves");

    public static final Map<Nutrient, DeferredItem<Item>> DISHES = Helpers.mapOf(
            Nutrient.class,
            nutrient -> registerItem("dish/" + nutrient.name().toLowerCase(Locale.ROOT))
    );
}
