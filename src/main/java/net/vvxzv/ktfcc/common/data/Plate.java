package net.vvxzv.ktfcc.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Plate(ResourceLocation plateItem, ResourceLocation itemInPlate) {
    public static final Codec<Plate> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("plateItem").forGetter(c -> c.plateItem),
            ResourceLocation.CODEC.fieldOf("itemInPlate").forGetter(c -> c.itemInPlate)
    ).apply(i, Plate::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Plate> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, c -> c.plateItem,
            ResourceLocation.STREAM_CODEC, c -> c.itemInPlate,
            Plate::new
    );

    public static final DataManager<Plate> MANAGER = new DataManager<>(
            ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "plate"),
            CODEC,
            STREAM_CODEC
    );

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
            ItemStack plateStack = BuiltInRegistries.ITEM.get(plate.plateItem()).getDefaultInstance();
            if(plateStack.is(plateItem.getItem())){
                return BuiltInRegistries.ITEM.get(plate.itemInPlate()).getDefaultInstance();
            }
        }
        return null;
    }

    public static @Nullable ItemStack getPlateItem(ItemStack itemInPlate) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(itemInPlate.getItem());
        for (Plate plate: PLATE_CACHE.getAll(id)) {
            ItemStack stackInPlate = BuiltInRegistries.ITEM.get(plate.itemInPlate()).getDefaultInstance();
            if(stackInPlate.is(itemInPlate.getItem())) {
                return BuiltInRegistries.ITEM.get(plate.plateItem()).getDefaultInstance();
            }
        }
        return null;
    }
}
