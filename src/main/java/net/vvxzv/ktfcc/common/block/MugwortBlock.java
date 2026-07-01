package net.vvxzv.ktfcc.common.block;

import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class MugwortBlock extends AbstractBushBlock {
    public MugwortBlock(Supplier<? extends Item> productItem, ResourceLocation id) {
        super(
                productItem,
                id,
                new Lifecycle[]{
                        Lifecycle.DORMANT,
                        Lifecycle.DORMANT,
                        Lifecycle.HEALTHY,
                        Lifecycle.HEALTHY,
                        Lifecycle.FLOWERING,
                        Lifecycle.FLOWERING,
                        Lifecycle.FRUITING,
                        Lifecycle.FRUITING,
                        Lifecycle.FRUITING,
                        Lifecycle.FRUITING,
                        Lifecycle.DORMANT,
                        Lifecycle.DORMANT
                }
        );
    }
}
