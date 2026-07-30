package net.vvxzv.ktfcc.common.loot.modifiers;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;

public class LootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, KaleidoscopeTFCCookery.MODID);

    static {
        LOOT_MODIFIER_SERIALIZERS.register("remove_items", () -> RemoveItemsModifier.CODEC);
        LOOT_MODIFIER_SERIALIZERS.register("add_item", () -> AddItemModifier.CODEC);
        LOOT_MODIFIER_SERIALIZERS.register("composite", () -> CompositeModifier.CODEC);
    }
}
