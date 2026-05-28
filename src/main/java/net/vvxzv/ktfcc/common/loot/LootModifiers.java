package net.vvxzv.ktfcc.common.loot;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;

public class LootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, KaleidoscopeTFCCookery.MODID);

    static {
        LOOT_MODIFIER_SERIALIZERS.register("remove_items", () -> RemoveItemsModifier.CODEC);
        LOOT_MODIFIER_SERIALIZERS.register("add_item", () -> AddItemModifier.CODEC);
        LOOT_MODIFIER_SERIALIZERS.register("composite", () -> CompositeModifier.CODEC);
    }
}
