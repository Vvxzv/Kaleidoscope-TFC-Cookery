package net.vvxzv.ktfcc;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.vvxzv.ktfcc.common.registry.KBlockEntity;
import net.vvxzv.ktfcc.common.registry.KBlocks;
import net.vvxzv.ktfcc.common.registry.KCreativeTAB;
import net.vvxzv.ktfcc.common.registry.KItems;

@Mod(KTFCC.MODID)
public class KTFCC {
    public static final String MODID = "ktfcc";

    @SuppressWarnings("removal")
    public KTFCC() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        KBlocks.BLOCKS.register(modEventBus);
        KItems.ITEMS.register(modEventBus);
        KCreativeTAB.CREATIVE_MODE_TAB.register(modEventBus);
        KBlockEntity.BLOCK_ENTITIES.register(modEventBus);

        EventHandler.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
