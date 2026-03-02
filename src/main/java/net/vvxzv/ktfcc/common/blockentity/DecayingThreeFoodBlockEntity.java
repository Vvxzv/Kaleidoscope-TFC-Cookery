package net.vvxzv.ktfcc.common.blockentity;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.NinePart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.vvxzv.ktfcc.common.block.decay.DecayingFoodBiteThreeByThreeBlock;
import net.vvxzv.ktfcc.common.registry.KBlockEntity;

public class DecayingThreeFoodBlockEntity extends DecayingFoodBlockEntity{
    public DecayingThreeFoodBlockEntity(BlockPos pos, BlockState state) {
        super(KBlockEntity.THREE_DECAYING.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity blockEntity) {
//        if(blockEntity instanceof DecayingThreeFoodBlockEntity decaying){
//            if (level.getGameTime() % 20L == 0L && decaying.isRotten()) {
//                if (blockState.getBlock() instanceof DecayingFoodBiteThreeByThreeBlock block) {
//                    level.setBlockAndUpdate(blockPos, blockState.setValue(block.getBites(), 0));
//                    level.removeBlock(blockPos, true);
//                    if(blockState.getValue(DecayingFoodBiteThreeByThreeBlock.PART).isCenter()){
//                        decaying.setStack(ItemStack.EMPTY);
//                        for(int i = -1; i < 2; ++i) {
//                            for(int j = -1; j < 2; ++j) {
//                                BlockPos searchPos = blockPos.offset(i, 0, j);
//                                NinePart part = NinePart.getPartByPos(i, j);
//                                if (part != null) {
//                                    level.setBlockAndUpdate(searchPos, block.getRottedBlock().defaultBlockState().setValue(DecayingFoodBiteThreeByThreeBlock.PART, part));
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}
