package net.vvxzv.ktfcc.common.data;

import com.google.gson.JsonObject;
import net.dries007.tfc.common.recipes.ingredients.FluidIngredient;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import org.jetbrains.annotations.Nullable;

public class Oil {
    public static final DataManager<Oil> MANAGER = new DataManager<>(
            ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "oil"),
            "oil", Oil::new, Oil::new, Oil::encode, Oil.Packet::new
    );

    private final ResourceLocation id;
    private final FluidIngredient fluidIngredient;
    private final int consume;

    public Oil(ResourceLocation id, JsonObject json) {
        this.id = id;
        this.fluidIngredient = FluidIngredient.fromJson(JsonHelpers.get(json, "fluid"));
        this.consume = clampConsume(JsonHelpers.getAsInt(json, "consume"));
    }

    public Oil(ResourceLocation id, FriendlyByteBuf buffer) {
        this.id = id;
        this.fluidIngredient = FluidIngredient.fromNetwork(buffer);
        this.consume = clampConsume(buffer.readVarInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        this.fluidIngredient.toNetwork(buffer);
        buffer.writeVarInt(clampConsume(this.consume));
    }

    public static final IndirectHashCollection<Fluid, Oil> CACHE = IndirectHashCollection.create(
            oil -> oil.getFluidIngredient().fluids(),
            MANAGER::getValues
    );

    public static @Nullable Oil get(Fluid fluid) {
        for(Oil oil : CACHE.getAll(fluid)) {
            if (oil.fluidIngredient.test(fluid)) {
                return oil;
            }
        }

        return null;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public FluidIngredient getFluidIngredient() {
        return this.fluidIngredient;
    }

    public int getConsumeAmount() {
        return this.consume;
    }

    private static int clampConsume(int value) {
        return Math.max(1, value);
    }

    public static class Packet extends DataManagerSyncPacket<Oil> {
    }
}
