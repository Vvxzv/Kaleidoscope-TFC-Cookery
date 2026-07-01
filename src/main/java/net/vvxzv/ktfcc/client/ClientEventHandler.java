package net.vvxzv.ktfcc.client;

import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.registry.Blocks;

@Mod.EventBusSubscriber(modid = KaleidoscopeTFCCookery.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void clientSetupEvent(FMLClientSetupEvent event) {
        Blocks.TEA_TREES.forEach((tea, block) -> RenderTypeRegistry.register(RenderType.cutout(), block.get()));
        Blocks.WILD_TEA_TREES.forEach((tea, block) -> RenderTypeRegistry.register(RenderType.cutout(), block.get()));

        RenderTypeRegistry.register(RenderType.cutout(), Blocks.MUGWORT.get());
        RenderTypeRegistry.register(RenderType.cutout(), Blocks.WILD_MUGWORT.get());
    }
}
