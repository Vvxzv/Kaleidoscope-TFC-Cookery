package net.vvxzv.ktfcc.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record TeaEffect(Ingredient ingredient, List<MobEffectInstance> mobEffects) {
    public static final Codec<TeaEffect> CODEC = RecordCodecBuilder.create(i -> i.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
            MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(TeaEffect::mobEffects)
    ).apply(i, TeaEffect::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeaEffect> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
            ByteBufCodecs.collection(ArrayList::new, MobEffectInstance.STREAM_CODEC), TeaEffect::mobEffects,
            TeaEffect::new
    );

    public static final DataManager<TeaEffect> MANAGER = new DataManager<>(
            ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "tea_effect"),
            CODEC,
            STREAM_CODEC
    );

    public static final IndirectHashCollection<Item, TeaEffect> CACHE = IndirectHashCollection.create(
            c -> RecipeHelpers.itemKeys(c.ingredient),
            MANAGER::getValues
    );

    public static @Nullable TeaEffect get(ItemStack stack) {
        for (TeaEffect effects: CACHE.getAll(stack.getItem())) {
            if(effects.ingredient.test(stack)){
                return effects;
            }
        }
        return null;
    }

    public List<MobEffectInstance> getEffects() {
        return new ArrayList<>(this.mobEffects);
    }
}
