package net.vvxzv.ktfcc.common.loot.modifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class RemoveItemsModifier extends LootModifier {
    private final Set<Item> itemsToRemove;

    public static final MapCodec<RemoveItemsModifier> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
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

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, @NotNull LootContext context) {
        generatedLoot.removeIf(itemStack -> this.itemsToRemove.contains(itemStack.getItem()));
        return generatedLoot;
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
