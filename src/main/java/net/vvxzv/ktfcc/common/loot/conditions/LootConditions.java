package net.vvxzv.ktfcc.common.loot.conditions;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vvxzv.ktfcc.KaleidoscopeTFCCookery;

public class LootConditions {
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, KaleidoscopeTFCCookery.MODID);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> BLOCK_TAG = LOOT_CONDITIONS.register("block_tag", () -> BlockTagLootCondition.TYPE);
}
