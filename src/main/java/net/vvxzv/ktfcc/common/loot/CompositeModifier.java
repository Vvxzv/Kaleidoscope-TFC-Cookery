package net.vvxzv.ktfcc.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CompositeModifier implements IGlobalLootModifier {
    private final List<IGlobalLootModifier> modifiers;

    public static final Codec<CompositeModifier> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    IGlobalLootModifier.DIRECT_CODEC.listOf().fieldOf("modifiers").forGetter(m -> m.modifiers)
            ).apply(inst, CompositeModifier::new)
    );

    protected CompositeModifier(List<IGlobalLootModifier> modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public @NotNull ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ObjectArrayList<ItemStack> currentLoot = generatedLoot;
        for (IGlobalLootModifier modifier : modifiers) {
            currentLoot = modifier.apply(currentLoot, context);
        }
        return currentLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
