package net.vvxzv.ktfcc.common.block.entity;

import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.vvxzv.ktfcc.common.registry.BlockEntities;

public class BushBlockEntity extends BerryBushBlockEntity {
    public BushBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.BUSH.get(), pos, state);
    }
}
