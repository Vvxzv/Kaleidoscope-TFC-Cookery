package net.vvxzv.ktfcc.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.registry.Blocks;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = KaleidoscopeTFCCookery.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void clientSetupEvent(FMLClientSetupEvent event) {
        Blocks.TEA_TREES.forEach((tea, block) -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
        Blocks.WILD_TEA_TREES.forEach((tea, block) -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));

        ItemBlockRenderTypes.setRenderLayer(Blocks.MUGWORT.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(Blocks.WILD_MUGWORT.get(), RenderType.cutout());
    }
}
