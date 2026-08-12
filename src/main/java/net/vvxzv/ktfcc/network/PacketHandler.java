package net.vvxzv.ktfcc.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;

public final class PacketHandler {
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> type(String id) {
        return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, id));
    }

    public static void setup(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar register = event.registrar(ModList.get().getModFileById(KaleidoscopeTFCCookery.MODID).versionString());
        register.playToClient(
                DataManagerSyncPacket.TYPE,
                DataManagerSyncPacket.CODEC,
                (packet, context) -> context.enqueueWork(
                        () -> packet.handle(context.connection().isMemoryConnection())
                )
        );
    }
}
