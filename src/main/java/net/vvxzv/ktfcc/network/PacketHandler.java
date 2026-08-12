package net.vvxzv.ktfcc.network;

import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.DataManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.data.FoodEffect;
import net.vvxzv.ktfcc.common.data.Oil;
import net.vvxzv.ktfcc.common.data.Plate;
import net.vvxzv.ktfcc.common.data.TeaEffect;
import org.apache.commons.lang3.mutable.MutableInt;

public final class PacketHandler {
    private static final String VERSION = ModList.get().getModFileById(KaleidoscopeTFCCookery.MODID).versionString();
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "network"),
            () -> VERSION,
            VERSION::equals,
            VERSION::equals
    );
    private static final MutableInt ID = new MutableInt(0);

    public static void send(PacketDistributor.PacketTarget target, Object message) {
        CHANNEL.send(target, message);
    }

    public static void init() {
        registerDataManager(FoodEffect.Packet.class, FoodEffect.MANAGER);
        registerDataManager(Plate.Packet.class, Plate.MANAGER);
        registerDataManager(TeaEffect.Packet.class, TeaEffect.MANAGER);
        registerDataManager(Oil.Packet.class, Oil.MANAGER);
    }

    public static <T extends DataManagerSyncPacket<E>, E> void registerDataManager(Class<T> cls, DataManager<E> manager, SimpleChannel channel, int id) {
        channel.registerMessage(id, cls, (packet, buffer) -> packet.encode(manager, buffer), (buffer) -> {
            T packet = (T)manager.createEmptyPacket();
            packet.decode(manager, buffer);
            return packet;
        }, (packet, context) -> {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> packet.handle(context.get(), manager));
        });
    }

    private static <T extends DataManagerSyncPacket<E>, E> void registerDataManager(Class<T> cls, DataManager<E> manager) {
        registerDataManager(cls, manager, CHANNEL, ID.getAndIncrement());
    }
}
