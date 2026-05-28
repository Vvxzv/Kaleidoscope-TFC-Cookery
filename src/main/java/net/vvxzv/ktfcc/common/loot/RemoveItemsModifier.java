package net.vvxzv.ktfcc.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class RemoveItemsModifier extends LootModifier {
    private final Set<Item> itemsToRemove;

    public static final Codec<RemoveItemsModifier> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(m -> m.conditions),
                    BuiltInRegistries.ITEM.byNameCodec()
                            .listOf()
                            .fieldOf("targets")
                            .xmap(
                                    HashSet::new,
                                    set -> set.stream().toList()
                            )
                            .forGetter(m -> (HashSet<Item>) m.itemsToRemove)
            ).apply(inst, RemoveItemsModifier::new)
    );

    protected RemoveItemsModifier(LootItemCondition[] conditionsIn, Set<Item> itemsToRemove) {
        super(conditionsIn);
        this.itemsToRemove = itemsToRemove;
    }

    @Override
    @NotNull
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, @NotNull LootContext context) {
        generatedLoot.removeIf(itemStack -> this.itemsToRemove.contains(itemStack.getItem()));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
