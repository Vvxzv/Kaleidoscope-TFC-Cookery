package net.vvxzv.ktfcc.common.blockentity;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.vvxzv.ktfcc.Config;
import net.vvxzv.ktfcc.common.block.StoveBlock;
import net.vvxzv.ktfcc.common.registry.KBlockEntity;

public class StoveBlockEntity extends BlockEntity {
    private static float stoveTemperature(){
        return (float) Config.stoveTemperature;
    }

    public StoveBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(KBlockEntity.STOVE.get(), pPos, pBlockState);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        boolean isStoveLit = state.getValue(StoveBlock.LIT);
        if(isStoveLit){
            BlockEntity above = level.getBlockEntity(pos.above());
            if (above != null) {
                above.getCapability(HeatCapability.BLOCK_CAPABILITY).ifPresent((cap) -> {
                    float blockTemperature = cap.getTemperature();
                    if(blockTemperature < stoveTemperature()){
                        float setTemperature = blockTemperature + 2;
                        if(setTemperature > stoveTemperature()) setTemperature = stoveTemperature();
                        cap.setTemperatureIfWarmer(setTemperature);
                    }
                });
            }
        }

    }
}
