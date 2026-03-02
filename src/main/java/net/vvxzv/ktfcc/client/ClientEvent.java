package net.vvxzv.ktfcc.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.vvxzv.ktfcc.KTFCC;
import net.vvxzv.ktfcc.client.render.ThreeDecayingRender;
import net.vvxzv.ktfcc.common.registry.KBlockEntity;

@Mod.EventBusSubscriber(modid = KTFCC.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvent {
    @SubscribeEvent
    public static void onEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        BlockEntityRenderers.register(KBlockEntity.THREE_DECAYING.get(), ThreeDecayingRender::new);
    }
}
