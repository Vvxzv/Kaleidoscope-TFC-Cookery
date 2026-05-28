package net.vvxzv.ktfcc;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.vvxzv.ktfcc.common.data.DataManagers;
import net.vvxzv.ktfcc.common.loot.LootModifiers;
import net.vvxzv.ktfcc.common.registry.BlockEntities;
import net.vvxzv.ktfcc.common.registry.Blocks;
import net.vvxzv.ktfcc.common.registry.CreativeTab;
import net.vvxzv.ktfcc.common.registry.Items;
import net.vvxzv.ktfcc.common.utils.FoodTraits;

@Mod(KaleidoscopeTFCCookery.MODID)
public class KaleidoscopeTFCCookery {
    public static final String MODID = "ktfcc";

    public KaleidoscopeTFCCookery(IEventBus modEventBus, ModContainer modContainer) {
        Items.ITEMS.register(modEventBus);
        Blocks.BLOCKS.register(modEventBus);
        BlockEntities.BLOCK_ENTITIES.register(modEventBus);
        LootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        CreativeTab.CREATIVE_MODE_TAB.register(modEventBus);
        FoodTraits.TRAITS.register(modEventBus);
        DataManagers.MANAGERS.register(modEventBus);

        modEventBus.addListener(this::registerRegistries);

        NeoForgeEventHandler.init();

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public void registerRegistries(NewRegistryEvent event) {
        event.register(DataManagers.REGISTRY);
    }
}
