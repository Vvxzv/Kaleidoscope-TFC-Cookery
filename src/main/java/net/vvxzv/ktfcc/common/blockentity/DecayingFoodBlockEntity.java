package net.vvxzv.ktfcc.common.blockentity;

import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.vvxzv.ktfcc.common.block.decay.DecayingFoodBiteOneByTwoBlock;
import net.vvxzv.ktfcc.common.block.decay.DecayingFoodBlock;
import net.vvxzv.ktfcc.common.registry.KBlockEntity;

public class DecayingFoodBlockEntity extends DecayingBlockEntity {
    public DecayingFoodBlockEntity(BlockPos pos, BlockState state) {
        super(KBlockEntity.DECAYING.get(), pos, state);
    }

    protected DecayingFoodBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
        if(blockEntity instanceof DecayingFoodBlockEntity decaying){
            if (level.getGameTime() % 20L == 0L && decaying.isRotten()) {
                if (blockState.getBlock() instanceof DecayingFoodBlock block) {
                    decaying.setStack(ItemStack.EMPTY);
                    if(handleOneByTwoBlock(level, blockPos, blockState)) return;
                    level.setBlockAndUpdate(blockPos, block.getRottedBlock().defaultBlockState());
                }
            }
        }
    }

    private static boolean handleOneByTwoBlock(Level level, BlockPos blockPos, BlockState blockState){
        if(blockState.getBlock() instanceof DecayingFoodBiteOneByTwoBlock oneByTwoBlock){
            if(blockState.getValue(DecayingFoodBiteOneByTwoBlock.POSITION) == 0) return false;
            BlockState rottenblockstate = oneByTwoBlock.getRottedBlock().defaultBlockState();
            Direction facing = blockState.getValue(DecayingFoodBiteOneByTwoBlock.FACING);
            BlockPos leftPos = blockPos.relative(facing.getClockWise());
            level.removeBlock(blockPos, false);
            level.setBlockAndUpdate(
                    blockPos,
                    rottenblockstate.setValue(DecayingFoodBiteOneByTwoBlock.FACING, facing).setValue(DecayingFoodBiteOneByTwoBlock.POSITION, 1)
            );
            level.setBlockAndUpdate(
                    leftPos,
                    rottenblockstate.setValue(DecayingFoodBiteOneByTwoBlock.FACING, facing).setValue(DecayingFoodBiteOneByTwoBlock.POSITION, 0)
            );
            return true;
        }
        return false;
    }
}
