package net.vvxzv.ktfcc.common.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record BlockTagLootCondition(TagKey<Block> tag) implements LootItemCondition {

    public static final Serializer<BlockTagLootCondition> SERIALIZER = new Serializer<>() {
        @Override
        public void serialize(JsonObject json, BlockTagLootCondition value, @NotNull JsonSerializationContext ctx) {
            json.addProperty("tag", value.tag().location().toString());
        }

        @Override
        @SuppressWarnings("removal")
        public @NotNull BlockTagLootCondition deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext ctx) {
            String tagStr = GsonHelper.getAsString(json, "tag");
            ResourceLocation tagRl = new ResourceLocation(tagStr);
            TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, tagRl);
            return new BlockTagLootCondition(tagKey);
        }
    };

    public static final LootItemConditionType TYPE = new LootItemConditionType(SERIALIZER);

    @Override
    public boolean test(LootContext lootContext) {
        if (!lootContext.hasParam(LootContextParams.BLOCK_STATE)) {
            return false;
        }
        BlockState blockState = lootContext.getParam(LootContextParams.BLOCK_STATE);
        return blockState.is(tag);
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return TYPE;
    }

    @Override
    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.BLOCK_STATE);
    }
}
