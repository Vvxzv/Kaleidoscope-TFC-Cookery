package net.vvxzv.ktfcc.client;

import com.github.ysbbbbbb.kaleidoscopecookery.item.OilPotItem;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.registry.Blocks;

@Mod.EventBusSubscriber(modid = KaleidoscopeTFCCookery.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void clientSetupEvent(FMLClientSetupEvent event) {
        Blocks.TEA_TREES.forEach((tea, block) -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));
        Blocks.WILD_TEA_TREES.forEach((tea, block) -> ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.cutout()));

        ItemBlockRenderTypes.setRenderLayer(Blocks.MUGWORT.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(Blocks.WILD_MUGWORT.get(), RenderType.cutout());

        event.enqueueWork(() -> ItemProperties.register(Blocks.OIL_POT.get().asItem(), OilPotItem.HAS_OIL_PROPERTY, ((pStack, pLevel, pEntity, pSeed) -> pStack.hasTag()? 1: 0)));
    }
}
