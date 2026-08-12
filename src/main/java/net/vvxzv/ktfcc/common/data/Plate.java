package net.vvxzv.ktfcc.common.data;

import com.google.gson.JsonObject;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import net.vvxzv.ktfcc.common.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class Plate {
    public static final DataManager<Plate> MANAGER = new DataManager<>(
            ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "plate"),
            "plate", Plate::new, Plate::new, Plate::encode, Plate.Packet::new
    );

    private final ResourceLocation id;
    private final ResourceLocation plateItem;
    private final ResourceLocation itemInPlate;

    public Plate(ResourceLocation id, JsonObject json) {
        this.id = id;
        this.plateItem = Utils.getResourceLocation(JsonHelpers.getAsString(json, "plateItem"));
        this.itemInPlate = Utils.getResourceLocation(JsonHelpers.getAsString(json, "itemInPlate"));

    }

    public Plate(ResourceLocation id, FriendlyByteBuf buffer) {
        this.id = id;
        this.plateItem = buffer.readResourceLocation();
        this.itemInPlate = buffer.readResourceLocation();

    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(this.plateItem);
        buffer.writeResourceLocation(this.itemInPlate);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public ResourceLocation getPlateItem() {
        return this.plateItem;
    }

    public ResourceLocation getItemInPlate() {
        return this.itemInPlate;
    }

    public static final IndirectHashCollection<ResourceLocation, Plate> ITEM_IN_PLATE_CACHE = IndirectHashCollection.create(
            c -> Collections.singleton(c.plateItem),
            MANAGER::getValues
    );

    public static final IndirectHashCollection<ResourceLocation, Plate> PLATE_CACHE = IndirectHashCollection.create(
            c -> Collections.singleton(c.itemInPlate),
            MANAGER::getValues
    );

    public static @Nullable ItemStack getItemInPlate(ItemStack plateItem) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(plateItem.getItem());
        for (Plate plate: ITEM_IN_PLATE_CACHE.getAll(id)) {
            ItemStack plateStack = BuiltInRegistries.ITEM.get(plate.getPlateItem()).getDefaultInstance();
            if(plateStack.is(plateItem.getItem())){
                return BuiltInRegistries.ITEM.get(plate.getItemInPlate()).getDefaultInstance();
            }
        }
        return null;
    }

    public static @Nullable ItemStack getPlateItem(ItemStack itemInPlate) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(itemInPlate.getItem());
        for (Plate plate: PLATE_CACHE.getAll(id)) {
            ItemStack stackInPlate = BuiltInRegistries.ITEM.get(plate.getItemInPlate()).getDefaultInstance();
            if(stackInPlate.is(itemInPlate.getItem())) {
                return BuiltInRegistries.ITEM.get(plate.getPlateItem()).getDefaultInstance();
            }
        }
        return null;
    }

    public static class Packet extends DataManagerSyncPacket<Plate> {
    }
}
