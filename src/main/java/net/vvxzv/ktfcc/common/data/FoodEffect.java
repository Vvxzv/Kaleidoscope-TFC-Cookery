package net.vvxzv.ktfcc.common.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.ItemDefinition;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FoodEffect extends ItemDefinition {
    public static final DataManager<FoodEffect> MANAGER = new DataManager<>(
            ResourceLocation.fromNamespaceAndPath(KaleidoscopeTFCCookery.MODID, "food_effect"),
            "food_effect", FoodEffect::new, FoodEffect::new, FoodEffect::encode, FoodEffect.Packet::new
    );

    private List<MobEffectInstance> mobEffects = new ArrayList<>();

    public FoodEffect(ResourceLocation id, JsonObject json) {
        super(id, Ingredient.fromJson(JsonHelpers.get(json, "ingredient")));
        JsonArray array = JsonHelpers.getAsJsonArray(json, "effects");
        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            ResourceLocation effectId = JsonHelpers.getResourceLocation(object, "id");
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);
            if (effect != null) {
                int duration = JsonHelpers.getAsInt(object, "duration");
                int amplifier = JsonHelpers.getAsInt(object, "amplifier");
                this.mobEffects.add(new MobEffectInstance(effect, duration, amplifier));
            }
        }
    }

    public FoodEffect(ResourceLocation id, FriendlyByteBuf buffer) {
        super(id, Ingredient.fromNetwork(buffer));
        this.mobEffects = buffer.readList(buf ->
                new MobEffectInstance(
                        MobEffect.byId(buf.readVarInt()),
                        buf.readVarInt(),
                        buf.readVarInt()
                )
        );
    }

    public void encode(FriendlyByteBuf buffer) {
        this.ingredient.toNetwork(buffer);
        buffer.writeCollection(this.mobEffects, (buf, effect) -> {
            buf.writeVarInt(MobEffect.getId(effect.getEffect()));
            buf.writeVarInt(effect.getDuration());
            buf.writeVarInt(effect.getAmplifier());
        });
    }

    public static final IndirectHashCollection<Item, FoodEffect> CACHE = IndirectHashCollection.create(
            ItemDefinition::getValidItems,
            MANAGER::getValues
    );

    public static @Nullable FoodEffect get(ItemStack stack) {
        for (FoodEffect effects: CACHE.getAll(stack.getItem())) {
            if(effects.matches(stack)){
                return effects;
            }
        }
        return null;
    }

    public List<MobEffectInstance> getEffects() {
        return new ArrayList<>(this.mobEffects);
    }

    public static class Packet extends DataManagerSyncPacket<FoodEffect> {
    }
}
