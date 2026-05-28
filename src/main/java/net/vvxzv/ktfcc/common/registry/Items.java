package net.vvxzv.ktfcc.common.registry;

import net.dries007.tfc.util.Helpers;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.block.Tea;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, KaleidoscopeTFCCookery.MODID);

    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item){
        return ITEMS.register(name, item);
    }

    private static RegistryObject<Item> registerItem(String name, Item.Properties properties){
        return registerItem(name, () -> new Item(properties));
    }

    private static RegistryObject<Item> registerItem(String name){
        return registerItem(name, new Item.Properties());
    }

    public static final RegistryObject<Item> UNFIRED_ENAMEL_BASIN = registerItem("unfired_enamel_basin");

    public static final RegistryObject<Item> UNFIRED_CUP = registerItem("unfired_cup");

    public static final RegistryObject<Item> KITCHEN_SHOVEL_HEAD = registerItem("kitchen_shovel_head");

    public static final RegistryObject<Item> IRON_KITCHEN_KNIFE_HEAD = registerItem("iron_kitchen_knife_head");

    public static final Map<Tea, RegistryObject<Item>> TEA_TREE_LEAVES = Helpers.mapOfKeys(
            Tea.class,
            Tea::isHasTree,
            (tea) -> registerItem("tea_leaves/" + tea.name().toLowerCase(Locale.ROOT))
    );

    public static final Map<Tea, RegistryObject<Item>> TEA_LEAVES = Helpers.mapOfKeys(
            Tea.class,
            (tea) -> registerItem("tea/" + tea.name().toLowerCase(Locale.ROOT))
    );
}
