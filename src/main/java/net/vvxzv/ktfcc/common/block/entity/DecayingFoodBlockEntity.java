package net.vvxzv.ktfcc.common.block.entity;

import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.vvxzv.ktfcc.common.registry.BlockEntities;
import net.vvxzv.ktfcc.common.utils.Decaying;

public class DecayingFoodBlockEntity extends DecayingBlockEntity implements Decaying {
    public DecayingFoodBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntities.DECAYING.get(), pos, state);
    }

    protected DecayingFoodBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
