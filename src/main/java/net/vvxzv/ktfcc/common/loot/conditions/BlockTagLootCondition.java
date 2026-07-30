package net.vvxzv.ktfcc.common.loot.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record BlockTagLootCondition(TagKey<Block> tag) implements LootItemCondition {

    public static final MapCodec<BlockTagLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    TagKey.codec(Registries.BLOCK)
                            .fieldOf("tag")
                            .forGetter(BlockTagLootCondition::tag)
            ).apply(instance, BlockTagLootCondition::new)
    );

    public static final LootItemConditionType TYPE = new LootItemConditionType(CODEC);

    @Override
    public @NotNull LootItemConditionType getType() {
        return TYPE;
    }

    @Override
    public boolean test(LootContext lootContext) {
        if (!lootContext.hasParam(LootContextParams.BLOCK_STATE)) {
            return false;
        }
        BlockState blockState = lootContext.getParam(LootContextParams.BLOCK_STATE);
        return blockState.is(tag);
    }

    @Override
    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.BLOCK_STATE);
    }
}