package net.vvxzv.ktfcc;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.vvxzv.ktfcc.common.loot.LootModifiers;
import net.vvxzv.ktfcc.common.registry.BlockEntities;
import net.vvxzv.ktfcc.common.registry.Blocks;
import net.vvxzv.ktfcc.common.registry.CreativeTab;
import net.vvxzv.ktfcc.common.registry.Items;
import net.vvxzv.ktfcc.common.utils.FoodTraits;

@Mod(KaleidoscopeTFCCookery.MODID)
public class KaleidoscopeTFCCookery {
    public static final String MODID = "ktfcc";

    @SuppressWarnings("removal")
    public KaleidoscopeTFCCookery() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Blocks.BLOCKS.register(modEventBus);
        Items.ITEMS.register(modEventBus);
        BlockEntities.BLOCK_ENTITIES.register(modEventBus);
        LootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        CreativeTab.CREATIVE_MODE_TAB.register(modEventBus);
        ForgeEventHandler.init();

        modEventBus.addListener(this::setup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(FoodTraits::registerFoodTraits);
    }
}
