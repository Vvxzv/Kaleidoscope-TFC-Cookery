package net.vvxzv.ktfcc;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.vvxzv.ktfcc.common.block.entity.OilPotBlockEntity;
import net.vvxzv.ktfcc.common.data.DataManagers;
import net.vvxzv.ktfcc.common.loot.conditions.LootConditions;
import net.vvxzv.ktfcc.common.loot.modifiers.LootModifiers;
import net.vvxzv.ktfcc.common.registry.*;
import net.vvxzv.ktfcc.common.utils.FoodTraits;
import net.vvxzv.ktfcc.network.PacketHandler;

@Mod(KaleidoscopeTFCCookery.MODID)
public class KaleidoscopeTFCCookery {
    public static final String MODID = "ktfcc";

    public KaleidoscopeTFCCookery(IEventBus modEventBus, ModContainer modContainer) {
        Items.ITEMS.register(modEventBus);
        Blocks.BLOCKS.register(modEventBus);
        BlockEntities.BLOCK_ENTITIES.register(modEventBus);
        LootConditions.LOOT_CONDITIONS.register(modEventBus);
        LootModifiers.LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        CreativeTab.CREATIVE_MODE_TAB.register(modEventBus);
        FoodTraits.TRAITS.register(modEventBus);
        DataManagers.MANAGERS.register(modEventBus);
        DataComponent.DATA_COMPONENT_TYPES.register(modEventBus);

        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(PacketHandler::setup);
        modEventBus.addListener(this::registerCapabilities);

        NeoForgeEventHandler.init();

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public void registerRegistries(NewRegistryEvent event) {
        event.register(DataManagers.REGISTRY);
    }

    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BlockEntities.OIL_POT.get(),
                OilPotBlockEntity::getSidedFluidInventory
        );
    }
}
