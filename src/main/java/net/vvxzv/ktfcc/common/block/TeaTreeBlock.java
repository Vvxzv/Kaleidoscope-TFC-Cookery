package net.vvxzv.ktfcc.common.block;

import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class TeaTreeBlock extends AbstractBushBlock {

    public TeaTreeBlock(Supplier<? extends Item> productItem, ResourceLocation id) {
        super(
                productItem,
                id,
                new Lifecycle[]{
                        Lifecycle.HEALTHY,
                        Lifecycle.FLOWERING,
                        Lifecycle.FRUITING,
                        Lifecycle.FRUITING,
                        Lifecycle.FRUITING,
                        Lifecycle.HEALTHY,
                        Lifecycle.HEALTHY,
                        Lifecycle.DORMANT,
                        Lifecycle.DORMANT,
                        Lifecycle.DORMANT,
                        Lifecycle.DORMANT,
                        Lifecycle.HEALTHY
                }
        );
    }

}
