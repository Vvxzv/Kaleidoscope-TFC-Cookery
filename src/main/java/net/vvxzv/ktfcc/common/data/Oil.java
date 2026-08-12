package net.vvxzv.ktfcc.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import org.jetbrains.annotations.Nullable;

public record Oil(FluidIngredient fluidIngredient, int consume) {
    public static final Codec<Oil> CODEC = RecordCodecBuilder.create(i -> i.group(
            FluidIngredient.CODEC.fieldOf("fluid").forGetter(c -> c.fluidIngredient),
            Codec.INT.fieldOf("consume").validate(v -> DataResult.success(Math.max(v, 1))).forGetter(c -> c.consume)
    ).apply(i, Oil::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Oil> STREAM_CODEC = StreamCodec.composite(
            FluidIngredient.STREAM_CODEC, c -> c.fluidIngredient,
            ByteBufCodecs.INT, c -> c.consume,
            Oil::new
    );

    public static final DataManager<Oil> MANAGER = new DataManager<>(
            ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "oil"),
            CODEC,
            STREAM_CODEC
    );

    public static final IndirectHashCollection<Fluid, Oil> CACHE = IndirectHashCollection.create(
            r -> RecipeHelpers.fluidKeys(r.fluidIngredient),
            MANAGER::getValues
    );

    public static @Nullable Oil get(Fluid fluid) {
        for(Oil oil : CACHE.getAll(fluid)) {
            if (oil.fluidIngredient.test(new FluidStack(fluid, 1))) {
                return oil;
            }
        }

        return null;
    }
}
